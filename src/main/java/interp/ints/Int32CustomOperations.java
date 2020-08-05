package interp.ints;

import interp.customoperations.CustomOperations;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Int32CustomOperations extends CustomOperations<Integer> {
    private static final Int32CustomOperations INSTANCE = new Int32CustomOperations();

    static Integer deserialize(DataInputStream dis) {
        try {
            return dis.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private Int32CustomOperations() {
        identifier = "_i";
        compare = Integer::compare;
        hash = (Integer a) -> a.hashCode();
        deserialize = Int32CustomOperations::deserialize;
        serialize = Int32CustomOperations::serialize;
        customFixedLength = 4l;
    }

    static byte[] serialize(Integer integer) {
        ByteBuffer buf = ByteBuffer.allocate(Integer.BYTES);
        buf.putInt(integer);
        return buf.array();
    }

    public static Int32CustomOperations getInstance() {
        return INSTANCE;
    }
}
