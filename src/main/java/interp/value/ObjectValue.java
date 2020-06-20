package interp.value;

import java.util.Arrays;

public class ObjectValue implements Value, BaseArrayValue<ObjectValue> {
    public static final int Pair_tag = 0;


    private final int prefix;
    private final int tag;
    private final Value[] fields;

    public ObjectValue(int tag, int size) {
        prefix = 0;
        this.tag = tag;
        fields = new Value[size];
    }

    public ObjectValue(ObjectValue objectValue, int prefix) {
        this.prefix = objectValue.prefix + prefix;
        this.fields = objectValue.fields;
        this.tag = objectValue.tag;
    }

    private ObjectValue(int tag, Value[] fields) {
        this.prefix = 0;
        this.tag = tag;
        this.fields = fields;
    }

    public static Value fromValueArray(int tag, Value[] arr) {
        return new ObjectValue(tag, arr);
    }

    public void setField(int field, Value value) {
        if(field == 172) {
            System.out.println("what happenin!");
        }
        fields[field] = value;
    }

    public Value getField(int field) {
        return fields[prefix + field];
    }

    public int getSize() {
        return fields.length - prefix;
    }

    public int getTag() {
        return tag;
    }

    public String toString() {
        return String.format("Block{%s}", Arrays.asList(fields));
    }

    public ObjectValue atFieldId(int fieldId) {
        return new ObjectValue(this, fieldId);
    }

    @Override
    public ObjectValue duplicate() {
        ObjectValue o = new ObjectValue(this.tag, this.getSize());
        for(int i = 0; i < fields.length; i++) {
            o.setField(i, fields[i]);
        }
        return o;
    }
}
