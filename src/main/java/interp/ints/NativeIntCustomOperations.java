package interp.ints;

import interp.customoperations.CustomOperations;

import java.io.DataInputStream;
import java.io.IOException;

public class NativeIntCustomOperations extends CustomOperations<Long> {

    static Long deserialize(DataInputStream dis) {

        try {
            switch(dis.readByte()) {
                case 1:
                    return (long)dis.readInt();
                case 2:
                    return dis.readLong();
                default:
                    throw new RuntimeException("input_value: ill-formed native integer");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public NativeIntCustomOperations() {
        identifier = "_n";
        compare = Long::compare;
        hash = (Long a) -> a.hashCode();
        deserialize = Int64CustomOperations::deserialize;
        customFixedLength = 8l;
    }
}
