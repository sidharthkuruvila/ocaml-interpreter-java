package interp;

import java.io.File;
import java.nio.file.Path;

public class ExecutableInterpreterTest {
    public void test() {
        int testCount = 1;
        for(int i = 0; i < testCount; i++) {

        }

    }

    public Path resourcePath(String name) {
        return new File(this.getClass().getResource("/interp/interpreter/" + name).getFile()).toPath();
    }

}
