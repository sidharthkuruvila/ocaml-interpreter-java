package interp.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public interface Channel {
    int readByte();
    void writeByte(int b);
    void flush();
}

