package interp.value;

public class StringValue implements Value {
    private final byte[] bytes;

    public StringValue(byte[] bytes) {
        this.bytes = bytes;
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
}
