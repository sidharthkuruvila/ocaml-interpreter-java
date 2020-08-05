package interp.value;

import interp.LongValue;
import interp.ValueTag;
import interp.customoperations.CustomOperationsValue;

import static interp.Interpreter.valFalse;
import static interp.Interpreter.valTrue;

public interface Value {
    Value Val_emptylist = new LongValue(0);

    static Value booleanValue(boolean bool) {
        if(bool) {
            return valTrue;
        } else {
            return valFalse;
        }
    }

    static Value identity(Value value) {
        return value;
    }

    default boolean isLongValue() {
        return this instanceof LongValue;
    }
    default LongValue asLongValue() {
        return (LongValue) this;
    }
    default int getTag() {
        return ValueTag.PAIR_TAG;
    }

    default ObjectValue asObjectValue(){
        return (ObjectValue) this;
    }

    default boolean isCustomOperationsValue() {
        return this instanceof CustomOperationsValue;
    }

    default CustomOperationsValue<Value> asCustomOperationsValue(){
        return (CustomOperationsValue<Value>) this;
    }

    default StringValue asStringValue() {
        return (StringValue) this;
    }

    default DoubleValue asDoubleValue() {
        return (DoubleValue) this;
    }

    default DoubleArray asDoubleArrayValue(){
        return (DoubleArray) this;
    }

    default int getHeader() {
        throw new RuntimeException();
    }
}
