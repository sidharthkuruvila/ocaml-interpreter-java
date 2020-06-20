package interp.value;

import interp.LongValue;

public class DoubleValue implements Value {
    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public static Value add(Value value, Value value1) {
        return new DoubleValue(((DoubleValue)value).getValue() + ((DoubleValue)value1).getValue());
    }

    public static StringValue format(Value formatStringValue, Value nValue) {
        String formatString = ((StringValue)formatStringValue).getString();
        double n = ((DoubleValue)nValue).getValue();
        String formatted = String.format(formatString, n);
        return StringValue.ofString(formatted);
    }

    public static Value mul(Value value, Value value1) {
        return new DoubleValue(((DoubleValue)value).getValue() * ((DoubleValue)value1).getValue());
    }
}
