package interp.value;

import interp.LongValue;

import java.util.Arrays;

public class ObjectValue implements Value, BaseArrayValue<ObjectValue> {


    private final int prefix;
    private int tag;
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

    public ObjectValue(int tag, Value[] fields) {
        this.prefix = 0;
        this.tag = tag;
        this.fields = fields;
    }

    public static Value fromValueArray(int tag, Value[] arr) {
        return new ObjectValue(tag, arr);
    }

    public void setField(int field, Value value) {
        fields[field] = value;
    }

    public Value getField(int field) {
        return fields[prefix + field];
    }

    public ObjectValue getObjectValueField(int field) {
        return (ObjectValue)getField(field);
    }

    public int getIntField(int field) {
        return LongValue.unwrapInt((LongValue) getField(field));
    }

    public String getStringField(int field) {
        return ((StringValue) getField(field)).getString();
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

    @Override
    public void blitTo(int offset, ObjectValue dest, int destOffset, int length) {
        System.arraycopy(fields, prefix + offset, dest.fields, dest.prefix + destOffset, length);
    }

    @Override
    public BaseArrayValue<?> append(BaseArrayValue<?> value1) {
        assert this.tag == ((ObjectValue)value1).tag;
        Value[] a1 = this.fields;
        Value[] a2 = ((ObjectValue)value1).fields;
        Value[] arr = new Value[a1.length + a2.length];
        System.arraycopy(a1, 0, arr, 0, a1.length);
        System.arraycopy(a2, 0, arr, a1.length, a2.length);
        return new ObjectValue(this.tag, arr);
    }

    @Override
    public Value subArray(int offset, int length) {
        return new ObjectValue(tag, Arrays.copyOfRange(fields, offset, offset + length));
    }

    public void setTag(int tag) {
        this.tag = tag;
    }
}
