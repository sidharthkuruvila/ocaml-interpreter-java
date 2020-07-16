package interp;

import interp.value.ObjectValue;
import interp.value.Value;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;


public class Executable {
    private static final int EV_POS = 0;
    private static final int EV_MODULE = 1;
    private static final int EV_LOC = 2;
    private static final int EV_KIND = 3;
    private static final int EV_DEFNAME = 4;

    private static final int LOC_START = 0;
    private static final int LOC_END = 1;
    private static final int LOC_GHOST = 2;

    private static final int POS_FNAME = 0;
    private static final int POS_LNUM = 1;
    private static final int POS_BOL = 2;
    private static final int POS_CNUM = 3;

    private final List<Section> sections;
    private final String sharedLibPath;
    private final String sharedLibs;
    private final List<String> prims;
    private final Value globalData;
    private final CodeFragment codeFragment;
    private final List<DebugEvent> debugEvents;


    public Executable(CodeFragmentTable codeFragmentTable, Intern intern, List<Section> sections) throws IOException {
        Map<String, Section> sectionMap = new HashMap<>();
        for (Section section : sections) {
            sectionMap.put(section.getName(), section);
        }

        Section codeSection = sectionMap.get("CODE");
        codeFragment = new CodeFragment(codeSection.getData());
        codeFragmentTable.add(codeFragment);

        Section dlptSection = sectionMap.get("DLPT");
        sharedLibPath = new String(dlptSection.getData());

        Section dllsSection = sectionMap.get("DLLS");
        sharedLibs = new String(dllsSection.getData());


        Section primSection = sectionMap.get("PRIM");
        if (primSection.getSize() == 0) {
            throw new RuntimeException("Fatal error: no PRIM section");
        }
        prims = Arrays.asList(new String(primSection.getData()).split("\0"));

        Section dataSection = sectionMap.get("DATA");
        globalData = intern.inputValue(ByteBuffer.wrap(dataSection.getData()));

        Section dbugSection = sectionMap.get("DBUG");
        if(dbugSection != null) {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(dbugSection.data));
            int numEvents = dis.readInt();
            ObjectValue events = new ObjectValue(ValueTag.ForwardTag, numEvents);
            LongValue zero = LongValue.wrap(0);
            List<String> paths = new ArrayList<>();
            for (int i = 0; i < numEvents; i++) {
                int orig = dis.readInt();
                Value evl = intern.inputValue(dis);
                Value adn = intern.inputValue(dis);
                String path = ((ObjectValue)adn).getStringField(0);
                paths.add(path);

//      /* Relocate events in event list */

                for (Value l = evl; !l.equals(zero); l = ((ObjectValue) l).getField(1)) {
                    ObjectValue ev = (ObjectValue) ((ObjectValue) l).getField(0);
                    long v = LongValue.unwrap((LongValue) ((ObjectValue) ev).getField(0));
                    ev.setField(0, LongValue.wrap(v + orig));
                }
                events.setField(i, evl);
            }

            List<DebugEvent> debugEvents = new ArrayList<>();
            for (int i = 0; i < events.getSize(); i++) {
                Value evl = events.getField(i);
                for (Value l = evl; !l.equals(zero); l = ((ObjectValue) l).getField(1)) {
                    ObjectValue ev = ((ObjectValue) l).getObjectValueField(0);
                    ObjectValue evLoc = ev.getObjectValueField(EV_LOC);
                    ObjectValue evStart = evLoc.getObjectValueField(LOC_START);
                    ObjectValue evEnd = evLoc.getObjectValueField(LOC_END);
                    int codeOffset = ev.getIntField(EV_POS);
                    CodePointer codePointer = new CodePointer(codeFragment.code, codeOffset/4);
                    String filename = evStart.getStringField(POS_FNAME);
                    String defname = ev.getStringField(EV_DEFNAME);
                    int lineNumber = evStart.getIntField(POS_LNUM);
                    int startChar = evStart.getIntField(POS_CNUM) -
                            evStart.getIntField(POS_BOL);
                    int endChar = evEnd.getIntField(POS_CNUM) -
                            evStart.getIntField(POS_BOL);

                    debugEvents.add(new DebugEvent(codePointer, paths.get(i), filename, defname, lineNumber, startChar, endChar));
                }
            }
            Collections.reverse(debugEvents);
            this.debugEvents = debugEvents;
        } else {
            debugEvents = null;
        }
        this.sections = sections;
    }

    public long getNumSections() {
        return sections.size();
    }

    public String getSharedLibPath() {
        return sharedLibPath;
    }

    public List<String> getPrims() {
        return prims;
    }

    public CodeFragment getCodeFragment() {
        return codeFragment;
    }

    public ObjectValue getGlobalData() {
        return (ObjectValue) globalData;
    }

    public List<DebugEvent> getDebugEvents() {
        return debugEvents;
    }

    public static class Section {
        private final String name;
        private final byte[] data;

        public Section(String name, byte[] data) {
            this.name = name;
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public byte[] getData() {
            return data;
        }

        public long getSize() {
            return data.length;
        }

    }



    public static class DebugEvent {

        private final CodePointer codePointer;

        public String getPath() {
            return path;
        }

        public String getFilename() {
            return filename;
        }

        public String getDefname() {
            return defname;
        }

        public int getLineNumber() {
            return lineNumber;
        }

        public int getStartChar() {
            return startChar;
        }

        public int getEndChar() {
            return endChar;
        }

        private final String path;
        private final String filename;
        private final String defname;
        private final int lineNumber;
        private final int startChar;
        private final int endChar;

        public DebugEvent(CodePointer codePointer, String path, String filename, String defname, int lineNumber, int startChar, int endChar) {
            this.codePointer = codePointer;
            this.path = path;
            this.filename = filename;
            this.defname = defname;
            this.lineNumber = lineNumber;
            this.startChar = startChar;
            this.endChar = endChar;
        }

        @Override
        public String toString() {
            return "DebugEvent{" +
                    "codePointer=" + codePointer +
                    ", path='" + path + '\'' +
                    ", filename='" + filename + '\'' +
                    ", defname='" + defname + '\'' +
                    ", lineNumber=" + lineNumber +
                    ", startChar=" + startChar +
                    ", endChar=" + endChar +
                    '}';
        }

        public CodePointer getCodePointer() {
            return codePointer;
        }
    }
}
