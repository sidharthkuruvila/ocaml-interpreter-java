package interp.io;

import java.io.IOException;
import java.io.InputStream;

public class InputStreamChannel implements Channel {

    InputStream is;
    public InputStreamChannel(InputStream is){
        this.is = is;
    }

    @Override
    public int readByte() {
        try {
            return is.read();
        } catch (IOException e)  {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeByte(int b) {
        throw new RuntimeException("Operation no supported");
    }

    @Override
    public void flush() {
        throw new RuntimeException("Operation no supported");
    }

    @Override
    public void writeBytes(byte[] bytes, int offset, int length) {
        throw new RuntimeException("Operation no supported");
    }
}
