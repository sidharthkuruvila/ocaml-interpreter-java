package interp.value;

import interp.LongValue;

import java.util.Arrays;

import static interp.Interpreter.valUnit;

public interface BaseArrayValue<T extends BaseArrayValue> extends Value{

    void setField(int field, Value value);

    Value getField(int field);

    T duplicate();

    static BaseArrayValue<?> duplicateArray(Value src) {
        return ((BaseArrayValue<?>)src).duplicate();
    }

    static Value unsafeGet(Value array, Value field) {
        return ((BaseArrayValue<?>)array).getField(((LongValue)field).getIntValue());
    }

    static Value makeVect(Value lenValue, Value initValue) {
        int len = ((LongValue)lenValue).getIntValue();
        if(initValue instanceof DoubleValue) {
            double[] arr = new double[len];
            Arrays.fill(arr, ((DoubleValue)initValue).getValue());
            return new DoubleArray(arr);
        } else {
            Value[] arr = new Value[len];
            Arrays.fill(arr, initValue);
            return ObjectValue.fromValueArray(0, arr);
        }
    }

    static Value unsafeSet(Value array, Value index, Value value) {
        ((BaseArrayValue<?>)array).setField(((LongValue)index).getIntValue(), value);
        return valUnit;

    }

    static BaseArrayValue<?> append(Value value, Value value1) {
        return ((BaseArrayValue<?>)value).append((BaseArrayValue<?>)value1);
    }

    BaseArrayValue<?> append(BaseArrayValue<?> value1);
}
