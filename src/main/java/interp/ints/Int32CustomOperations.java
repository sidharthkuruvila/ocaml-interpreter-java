package interp.ints;

import interp.customoperations.CustomOperations;

import java.io.DataInputStream;
import java.io.IOException;

public class Int32CustomOperations extends CustomOperations<Integer> {
    static Integer deserialize(DataInputStream dis) {
        try {
            return dis.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Int32CustomOperations() {
        identifier = "_i";
        compare = Integer::compare;
        hash = (Integer a) -> a.hashCode();
        deserialize = Int32CustomOperations::deserialize;
        customFixedLength = 4l;
    }
}
