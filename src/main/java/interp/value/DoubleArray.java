package interp.value;

import java.util.Arrays;

public class DoubleArray implements BaseArrayValue<DoubleArray>, Value {
    private final double[] values;

    public DoubleArray(double[] values){
        this.values = values;
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
    public BaseArrayValue<?> append(BaseArrayValue<?> value1) {
        double[] a1 = values;
        double[] a2 = ((DoubleArray)value1).values;
        double[] arr = new double[a1.length + a2.length];
        System.arraycopy(a1, 0, arr, 0, a1.length);
        System.arraycopy(a2, 0, arr, a1.length, a2.length);
        return new DoubleArray(arr);
    }
}
