package interp.value;

import interp.LongValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static interp.Interpreter.valUnit;

public interface BaseArrayValue<T extends BaseArrayValue<T>> extends Value{

    static Value blit(BaseArrayValue src, LongValue offset, BaseArrayValue dest, LongValue destOffset, LongValue length) {
        src.blitTo(LongValue.unwrapInt(offset), dest, LongValue.unwrapInt(destOffset), LongValue.unwrapInt(length));
        return valUnit;
    }

    void setField(int field, Value value);

    Value getField(int field);

    T duplicate();

    void blitTo(int offset, T dest, int destOffset, int length);

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

    static Value arrayConcat(Value value) {
        if(value == valUnit) {
            return new Atom(0);
        }

        ObjectValue list = (ObjectValue)value;

        if(list.getField(0) instanceof DoubleArray) {
            int length = 0;
            List<DoubleArray> arrs = new ArrayList<>();
            for(Value next = value; !next.equals(valUnit); next = ((ObjectValue)next).getField(1)) {
                length += ((DoubleArray)((ObjectValue)next).getField(0)).getSize();
                arrs.add((DoubleArray)((ObjectValue)next).getField(0));
            }
            DoubleArray doubleArray = new DoubleArray(new double[length]);

            for(int i = 0, n = 0; i < arrs.size(); n += arrs.get(i).getSize(), i++){
                arrs.get(i).blitTo(0, doubleArray, n, arrs.get(i).getSize());
            }
            return doubleArray;

        } else {
            int length = 0;
            List<ObjectValue> arrs = new ArrayList<>();
            for(ObjectValue next = (ObjectValue)value; !next.equals(valUnit); next = (ObjectValue)next.getField(1)) {
                length += ((ObjectValue)next.getField(0)).getSize();
                arrs.add((ObjectValue)next.getField(0));
            }
            ObjectValue objectValue = new ObjectValue(0, length);

            for(int i = 0, n = 0; i < arrs.size(); i++, n += arrs.get(i).getSize()){
                arrs.get(i).blitTo(0, objectValue, n, arrs.get(i).getSize());
            }
            return objectValue;
        }

    }

    Value subArray(int offset, int length);

    static Value sub(Value arrayValue, Value offset, Value length) {
        return ((BaseArrayValue)arrayValue).subArray((LongValue.unwrapInt((LongValue)offset)), LongValue.unwrapInt((LongValue)length));
    }

}
