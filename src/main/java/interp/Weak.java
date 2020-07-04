package interp;

import interp.value.BaseArrayValue;
import interp.value.ObjectValue;
import interp.value.Value;

import java.lang.ref.WeakReference;

import static interp.Interpreter.valUnit;
import static interp.ValueTag.Some_tag;

public class Weak implements Value {
    private final WeakReference<Value>[] references;
    public Weak(int size){
        references = new WeakReference[size];
    }

    public void setField(int field, Value value) {
        references[field] = new WeakReference<>(value);
    }

    public Value getField(int field) {
        WeakReference<Value> ref = references[field];
        if(ref == null){
            return valUnit;
        }
        Value value = ref.get();
        if(value == null) {
            return valUnit;
        }
        return some(value);
    }

    public BaseArrayValue duplicate() {
        return null;
    }

    public void blitTo(int offset, Weak dest, int destOffset, int length) {
        System.arraycopy(references, offset, dest.references, destOffset, length);
    }

    public static Weak create(LongValue size){
        return new Weak(LongValue.unwrapInt(size));
    }

    public static Value getValue(Weak weak, LongValue field){
        return weak.getField(LongValue.unwrapInt(field));
    }
    public static Value setValue(Weak weak, LongValue field, Value value){
        weak.setField(LongValue.unwrapInt(field), value);
        return valUnit;
    }

    public static Value unsetValue(Weak weak, LongValue field){
        weak.references[LongValue.unwrapInt(field)] = null;
        return valUnit;
    }

    static Value blit(Weak src, LongValue offset, Weak dest, LongValue destOffset, LongValue length) {
        src.blitTo(LongValue.unwrapInt(offset), dest, LongValue.unwrapInt(destOffset), LongValue.unwrapInt(length));
        return valUnit;
    }

    public int getSize() {
        return references.length;
    }

    private static Value some(Value v){
        ObjectValue objectValue = new ObjectValue(Some_tag, 1);
        objectValue.setField(0, v);
        return objectValue;
    }
}
