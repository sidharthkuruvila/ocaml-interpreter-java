package interp.io;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamChannel implements Channel {

    OutputStream os;
    public OutputStreamChannel(OutputStream os){
        this.os = os;
    }

    @Override
    public int readByte() {
        throw new RuntimeException("Operation no supported");

    }

    @Override
    public void writeByte(int b) {
        try {
            os.write(b);
        } catch (IOException e)  {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flush() {
        try {
            os.flush();
        } catch (IOException e)  {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeBytes(byte[] bytes, int offset, int length) {
        try {
            os.write(bytes, offset, length);
        } catch (IOException e)  {
            throw new RuntimeException(e);
        }
    }
}
