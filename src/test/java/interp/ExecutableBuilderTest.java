package interp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class ExecutableBuilderTest {
    @Test
    public void testReadingExecutable() throws IOException {
        try {
            Path path = new File(this.getClass().getResource("/interp/executables/test1.exe").getFile()).toPath();
            FileChannel ch = FileChannel.open(path);
            CodeFragmentTable codeFragmentTable = new CodeFragmentTable();
            CustomOperationsList customOperationsList = new CustomOperationsList();
            ExecutableBuilder eb = new ExecutableBuilder(codeFragmentTable, new Intern(customOperationsList, codeFragmentTable));
            Executable ex = eb.fromExe(ch);
            assertEquals(7, ex.getNumSections());
            System.out.println("prims:" + ex.getPrims());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
