package interp;

import interp.value.ObjectValue;
import interp.value.Value;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Executable {
    private final List<Section> sections;
    private final String sharedLibPath;
    private final String sharedLibs;
    private final List<String> prims;
    private final Value globalData;
    private final CodeFragment codeFragment;


    public Executable(CodeFragmentTable codeFragmentTable, Intern intern, List<Section> sections) throws IOException {
        Map<String, Section> sectionMap = new HashMap<>();
        for(Section section : sections) {
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
        if(primSection.getSize() == 0) {
            throw new RuntimeException("Fatal error: no PRIM section");
        }
        prims = Arrays.asList(new String(primSection.getData()).split("\0"));

        Section dataSection = sectionMap.get("DATA");
        globalData = intern.inputValue(ByteBuffer.wrap(dataSection.getData()));

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
}
