package interp.value;

import interp.LongValue;

import java.util.Arrays;

public class DoubleArray implements BaseArrayValue<DoubleArray>, Value {
    private final double[] values;

    public DoubleArray(double[] values){
        this.values = values;
    }

    public static double[] unwrap(DoubleArray doubleArray) {
        return doubleArray.values;
    }

    public double getDoubleField(int i) {
        return values[i];
    }

    public void setDoubleField(int i, double value) {
        values[i] = value;
    }

    public int getSize() {
        return values.length;
    }

    @Override
    public void setField(int field, Value value) {
        values[field] = ((DoubleValue)value).getValue();
    }

    @Override
    public Value getField(int field) {
        return new DoubleValue(values[field]);
    }

    @Override
    public DoubleArray duplicate() {
        return new DoubleArray(Arrays.copyOf(values, values.length));
    }

    @Override
    public void blitTo(int offset, DoubleArray dest, int destOffset, int length) {
        System.arraycopy(values, offset, dest.values, destOffset, length);
    }

    @Override
    public BaseArrayValue<?> append(BaseArrayValue<?> value1) {
        DoubleArray other = (DoubleArray)value1;
        double[] arr = new double[getSize() + other.getSize()];
        DoubleArray out = new DoubleArray(arr);
        this.blitTo(0, out, 0, getSize());
        other.blitTo(0, out, getSize(), other.getSize());
        return new DoubleArray(arr);
    }

    @Override
    public Value subArray(int offset, int length) {
        return new DoubleArray(Arrays.copyOfRange(values, offset, offset + length));
    }

    public static Value makeVect(Value size) {
        return new DoubleArray(new double[LongValue.unwrapInt((LongValue) size)]);
    }


}
