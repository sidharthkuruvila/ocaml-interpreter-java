package interp;

import static interp.Interpreter.valFalse;
import static interp.Interpreter.valTrue;

public class LongValue implements Value {
    private long value;

    public LongValue(long value) {
        this.value = value;
    }

    long getValue() {
        return value;
    }

    void setValue(long value) {
        this.value = value;
    }

    public boolean equals(Object b) {
        if(b != null &&b instanceof  LongValue) {
            return ((LongValue)b).value == value;
        }
        return false;
    }

    public LongValue negate() {
        return new LongValue(-1 * value);
    }

    public LongValue add(LongValue b) {
        return new LongValue(value + b.value);
    }

    public LongValue sub(LongValue b) {
        return new LongValue(value - b.value);
    }

    public LongValue mul(LongValue b) {
        return new LongValue(value * b.value);
    }

    public LongValue div(LongValue b) {
        return new LongValue(value / b.value);
    }

    public LongValue mod(LongValue b) {
        return new LongValue(value % b.value);
    }

    public LongValue and(LongValue b) {
        return new LongValue(value & b.value);
    }

    public LongValue or(LongValue b) {
        return new LongValue(value | b.value);
    }

    public LongValue xor(LongValue b) {
        return new LongValue(value ^ b.value);
    }

    public LongValue lsl(LongValue b) {
        return new LongValue(value << b.value);
    }

    public LongValue lsr(LongValue b) {
        return new LongValue(value >> b.value);
    }

    public LongValue eq(LongValue b) {
        return value == b.value? valTrue: valFalse;
    }
    public LongValue neq(LongValue b) {
        return value != b.value? valTrue: valFalse;
    }
    public LongValue lt(LongValue b) {
        return value < b.value? valTrue: valFalse;
    }

    public LongValue le(LongValue b) {
        return value <= b.value? valTrue: valFalse;
    }

    public LongValue gt(LongValue b) {
        return value > b.value? valTrue: valFalse;
    }

    public LongValue ge(LongValue b) {
        return value >= b.value? valTrue: valFalse;
    }

    public LongValue ult(LongValue b) {
        return Long.compareUnsigned(value, b.value) < 0 ? valTrue: valFalse;
    }

    public LongValue uge(LongValue b) {
        return Long.compareUnsigned(value, b.value) >= 0 ? valTrue: valFalse;
    }

    public String toString() {
        return Long.toString(value);
    }

    //Can loose information
    public int getIntValue() {
        return (int)value;
    }
}
