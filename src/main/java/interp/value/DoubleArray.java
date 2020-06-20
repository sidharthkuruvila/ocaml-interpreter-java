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
}
