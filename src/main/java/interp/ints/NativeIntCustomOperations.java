package interp.ints;

import interp.LongValue;
import interp.customoperations.CustomOperations;
import interp.customoperations.CustomOperationsValue;
import interp.value.DoubleValue;
import interp.value.StringValue;
import interp.value.Value;

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
        deserialize = NativeIntCustomOperations::deserialize;
        customFixedLength = 8l;
    }

    public static CustomOperationsValue<Long> sub(CustomOperationsValue<Long> v1, CustomOperationsValue<Long> v2){
        return wrap(v1.ops(), unwrap(v1) - unwrap(v2));
    }
    public static CustomOperationsValue<Long> lsl(CustomOperationsValue<Long> v1, LongValue v2){
        return wrap(v1.ops(), unwrap(v1) >> LongValue.unwrap(v2));
    }

    public static CustomOperationsValue<Long> ofInt(LongValue longValue){
        return new CustomOperationsValue<>(new NativeIntCustomOperations(), LongValue.unwrap(longValue));
    }

    public static CustomOperationsValue<Long> ofFloat(DoubleValue t) {
        return new CustomOperationsValue<>(new NativeIntCustomOperations(), (long)DoubleValue.unwrap(t));
    }

    public static CustomOperationsValue<Long> ofString(StringValue stringValue) {
        return new CustomOperationsValue<>(new NativeIntCustomOperations(), Long.parseLong(stringValue.toString()));
    }
}
