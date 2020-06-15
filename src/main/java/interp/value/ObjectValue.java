package interp.value;

import java.util.Arrays;
import java.util.List;

public class ObjectValue implements Value {
    public static final int Pair_tag = 0;


    private final int prefix;
    private final int tag;
    private final List<Value> fields;

    public ObjectValue(int tag, int size) {
        prefix = 0;
        this.tag = tag;
        fields = Arrays.asList(new Value[size]);
    }

    public ObjectValue(ObjectValue objectValue, int prefix) {
        this.prefix = objectValue.prefix + prefix;
        this.fields = objectValue.fields;
        this.tag = objectValue.tag;
    }

    public void setField(int field, Value value) {
        if(field == 172) {
            System.out.println("what happenin!");
        }
        fields.set(field, value);
    }

    public Value getField(int field) {
        return fields.get(prefix + field);
    }

    public int getSize() {
        return fields.size() - prefix;
    }

    public int getTag() {
        return tag;
    }

    public String toString() {
        return String.format("Block{%s}", fields);
    }

    public ObjectValue atFieldId(int fieldId) {
        return new ObjectValue(this, fieldId);
    }
}
