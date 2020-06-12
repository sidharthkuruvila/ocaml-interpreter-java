package interp.value;

import java.util.Arrays;
import java.util.List;

public class ObjectValue implements Value {
    public static final int Pair_tag = 0;


    private final int tag;
    private final List<Value> fields;

    public ObjectValue(int tag, int size) {
        this.tag = tag;
        fields = Arrays.asList(new Value[size]);
    }

    public void setField(int field, Value value) {
        fields.set(field, value);
    }

    public Value getField(int field) {
        return fields.get(field);
    }

    public int getSize() {
        return fields.size();
    }

    public int getTag() {
        return tag;
    }

    public String toString() {
        return String.format("Block{%s}", fields);
    }
}
