package interp.ints;

import interp.customoperations.CustomOperations;

import java.io.DataInputStream;
import java.io.IOException;

public class Int64CustomOperations extends CustomOperations<Long> {

    private static final Int64CustomOperations INSTANCE = new Int64CustomOperations();

    static Long deserialize(DataInputStream dis) {
        try {
            return dis.readLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private Int64CustomOperations() {
        identifier = "_j";
        compare = Long::compare;
        hash = (Long a) -> a.hashCode();
        deserialize = Int64CustomOperations::deserialize;
        customFixedLength = 8l;
    }

    public static Int64CustomOperations getInstance() {
        return INSTANCE;
    }
}
