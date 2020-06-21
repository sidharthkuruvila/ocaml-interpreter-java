package interp;

import interp.io.Channel;
import interp.io.ChannelRegistry;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ExecutableInterpreterTest {
    @Test
    public void test() throws IOException {
        int testCount = 1;
        for (int i = 0; i < testCount; i++) {
            ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
            Path exePath = resourcePath("test" + testCount + ".exe");
            Path resultPath = resourcePath("test" + testCount + ".out");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            ChannelRegistry channelRegistry = new ChannelRegistry(in, out, err);
            ExecutableFileInterpreter interpreter = new ExecutableFileInterpreter(channelRegistry);
            try {
                interpreter.execute(exePath);
            } catch (Throwable t) {
                t.printStackTrace();
            }


            System.out.println(new String(out.toByteArray()));
        }

    }

    public Path resourcePath(String name) {
        System.out.println("/interp/interpreter/" + name);
        return new File(this.getClass().getResource("/interp/interpreter/" + name).getFile()).toPath();
    }

}
