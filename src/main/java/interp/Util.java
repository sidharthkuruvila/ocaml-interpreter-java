package interp;

import java.nio.ByteBuffer;

public class Util {
    public static long getUint32(ByteBuffer bf, int index) {
        return (((long)bf.getChar(index+0))<<16) + (long)bf.getShort(index+2);
    }
}
