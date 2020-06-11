package interp.ints;

import interp.customoperations.CustomOperations;

public class IntCustomOperations extends CustomOperations<Long> {

    static Long deserialize(byte[] bytes) {
        long n = 0;
        for(int i=0; i < 8; i++) {
            n = n<<8 + bytes[i];
        }
        return n;
    }
    public IntCustomOperations() {
        identifier = "_j";
        compare = Long::compare;
        hash = (Long a) -> a.hashCode();
        deserialize = IntCustomOperations::deserialize;
        customFixedLength = 8l;
    }
}
