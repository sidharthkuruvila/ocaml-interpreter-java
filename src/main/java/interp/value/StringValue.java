package interp.value;

import interp.LongValue;
import interp.Sys;
import interp.ValueTag;
import interp.exceptions.CamlInvalidArgument;
import interp.stack.ValueStack;

import static interp.Interpreter.valUnit;

public class StringValue implements Value {
    private final byte[] bytes;

    public StringValue(byte[] bytes) {
        this.bytes = bytes;
    }

    public static StringValue ofString(String java) {
        return new StringValue(java.getBytes());
    }

    public int get(int index) {
        return 0xff & bytes[0];
    }

    public void set(int index, int value) {
        bytes[index] = (byte)value;
    }

    public String toString() {
        return new String(bytes);
    }

    public String getString() {
        return new String(bytes);
    }

    public int length() {
        return bytes.length;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public static StringValue createBytes(LongValue value) {
        int size = LongValue.unwrapInt(value);
        if(size > Sys.getMaxWoSize() - 1) {
            throw new CamlInvalidArgument("Bytes.create");
        }
        return withLength(size);
    }

    private static StringValue withLength(int size) {
        byte[] bytes = new byte[size];
        return new StringValue(bytes);
    }

    public static Value getByteValue(StringValue value, LongValue value1) {
        byte[] bytes = unwrap(value);
        int index = LongValue.unwrapInt(value1);
        return LongValue.wrap(bytes[index]);
    }

    public static Value blit(StringValue src, LongValue srcOffset, StringValue dest, LongValue destOffset, LongValue length) {
        System.arraycopy(
                unwrap(src), LongValue.unwrapInt(srcOffset),
                unwrap(dest), LongValue.unwrapInt(destOffset),
                LongValue.unwrapInt(length));
        return valUnit;
    }

    public static byte[] unwrap(StringValue value) {
        return value.bytes;
    }
    public static StringValue wrap(byte[] bytes) {
        return new StringValue(bytes);
    }

    public ValueTag getTag() {
        return ValueTag.String_tag;
    }
}
