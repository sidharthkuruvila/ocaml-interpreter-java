package interp.io;

import java.nio.channels.FileChannel;

public class Channel {
    //Fake fd
    FileChannel ch;
    public Channel(FileChannel ch) {
        this.ch = ch;
    }

    public FileChannel getFileChannel() {
        return ch;
    }
}
