package interp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

public class ExecutableBuilder {
    private static final int TRAILER_SIZE = 16;

    private final Intern intern;
    private final CodeFragmentTable codeFragmentTable;
    public ExecutableBuilder(CodeFragmentTable codeFragmentTable, Intern intern){
        this.intern = intern;
        this.codeFragmentTable = codeFragmentTable;
    }


    Executable fromExe(FileChannel channel) throws IOException {
        List<Executable.Section> sections = readSections(channel);

        return new Executable(codeFragmentTable, intern, sections);
    }

    private List<Executable.Section> readSections(FileChannel channel) throws IOException {
        // Read the number of sections
        long fileSize = channel.size();
        channel.position(fileSize - TRAILER_SIZE);
        ByteBuffer bf = ByteBuffer.allocate(TRAILER_SIZE);
        channel.read(bf);
        long numSections = Util.getUint32(bf, 0);
        String magic = getAsciiString(bf, 4, 12);
        System.out.println(magic);

        // Get the sections names and sizes
        long tocSize = numSections * 8;
        channel.position(fileSize - TRAILER_SIZE - tocSize);
        ByteBuffer bf4 = ByteBuffer.allocate(4);
        List<Executable.Section> sections = new ArrayList<>();
        for (int i = 0; i < numSections; i++) {
            channel.read(bf4);
            String s = getAsciiString(bf4, 0, 4);
            bf4.clear();
            channel.read(bf4);
            long n = Integer.toUnsignedLong(bf4.getInt(0));
            bf4.clear();
            sections.add(new Executable.Section(s, new byte[(int)n]));
        }


        long sectionsSize = 0;
        for(Executable.Section section : sections) {
            sectionsSize += section.getSize();
        }

        channel.position(fileSize - (TRAILER_SIZE + tocSize + sectionsSize));

        //Read the data for each section into memory
        for(Executable.Section section : sections) {
            ByteBuffer dbf = ByteBuffer.wrap(section.getData());
            channel.read(dbf);
        }
        return sections;
    }

    public static String getAsciiString(ByteBuffer bf, int offset, int length) {
        try {
            return new String(bf.array(), offset, length, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
