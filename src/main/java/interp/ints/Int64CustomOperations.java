package interp.ints;

import interp.Fail;
import interp.LongValue;
import interp.customoperations.CustomOperations;
import interp.customoperations.CustomOperationsValue;
import interp.value.DoubleValue;
import interp.value.StringValue;

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

    public static CustomOperationsValue<Long> add(CustomOperationsValue<Long> v1, CustomOperationsValue<Long> v2){
        return wrap(v1.ops(), unwrap(v1) + unwrap(v2));
    }

    public static CustomOperationsValue<Long> sub(CustomOperationsValue<Long> v1, CustomOperationsValue<Long> v2){
        return wrap(v1.ops(), unwrap(v1) - unwrap(v2));
    }

    public static CustomOperationsValue<Long> mul(CustomOperationsValue<Long> v1, CustomOperationsValue<Long> v2){
        return wrap(v1.ops(), unwrap(v1) * unwrap(v2));
    }

    public static CustomOperationsValue<Long> div(CustomOperationsValue<Long> v1, CustomOperationsValue<Long> v2){
        long dividend = unwrap(v1);
        long divisor = unwrap(v2);
        if (divisor == 0) Fail.caml_raise_zero_divide();
        return wrap(v1.ops(), dividend / divisor);
    }

    public static CustomOperationsValue<Long> mod(CustomOperationsValue<Long> v1, CustomOperationsValue<Long> v2){
        long dividend = unwrap(v1);
        long divisor = unwrap(v2);
        return wrap(v1.ops(), dividend % divisor);
    }

    public static CustomOperationsValue<Long> shift_left(CustomOperationsValue<Long> v1, LongValue v2){
        return wrap(v1.ops(), unwrap(v1) >> LongValue.unwrap(v2));
    }

    public static CustomOperationsValue<Long> shift_right(CustomOperationsValue<Long> v1, LongValue v2){
        return wrap(v1.ops(), unwrap(v1) >> LongValue.unwrap(v2));
    }

    public static CustomOperationsValue<Long> shift_right_unsigned(CustomOperationsValue<Long> v1, LongValue v2){
        return wrap(v1.ops(), unwrap(v1) >>> LongValue.unwrap(v2));
    }

    public static CustomOperationsValue<Long> and(CustomOperationsValue<Long> v1, CustomOperationsValue<Long> v2) {
        return wrap(v1.ops(), unwrap(v1) & unwrap(v2));
    }

    public static CustomOperationsValue<Long> or(CustomOperationsValue<Long> v1, CustomOperationsValue<Long> v2) {
        return wrap(v1.ops(), unwrap(v1) | unwrap(v2));
    }

    public static CustomOperationsValue<Long> xor(CustomOperationsValue<Long> v1, CustomOperationsValue<Long> v2) {
        return wrap(v1.ops(), unwrap(v1) ^ unwrap(v2));
    }

    public static LongValue compare(CustomOperationsValue<Long> v1, CustomOperationsValue<Long> v2) {
        return LongValue.wrap(Long.compare(unwrap(v1), unwrap(v2)));
    }


    public static CustomOperationsValue<Long> ofInt(LongValue longValue){
        return new CustomOperationsValue<>(getInstance(), LongValue.unwrap(longValue));
    }

    public static CustomOperationsValue<Long> ofFloat(DoubleValue t) {
        return new CustomOperationsValue<>(getInstance(), (long)DoubleValue.unwrap(t));
    }

    public static CustomOperationsValue<Long> ofString(StringValue stringValue) {
        try {
            return new CustomOperationsValue<>(getInstance(), Long.parseLong(stringValue.toString()));
        } catch(NumberFormatException e) {
            throw Fail.failWithException("Nativeint.of_string");
        }
    }

    public static CustomOperationsValue<Long> neg(CustomOperationsValue<Long> value) {
        return wrap(value.ops(), -1 * unwrap(value));
    }

    public static StringValue format(StringValue formatStringValue, CustomOperationsValue<Long> value) {
        String formatString = formatStringValue.getString();
        long n = unwrap(value);
        String formatted = String.format(formatString, n);
        return StringValue.ofString(formatted);
    }


    public static Int64CustomOperations getInstance() {
        return INSTANCE;
    }
}
