package interp.value;

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

    public static Value mul(Value value1, Value value2) {
        return wrap(unwrap(value1) * unwrap(value2));
    }

    public static DoubleValue abs(Value value) {
        return wrap(Math.abs(unwrap(value)));
    }

    private static DoubleValue wrap(double d) {
        return new DoubleValue(d);
    }
    private static double unwrap(Value value){
        return ((DoubleValue)value).getValue();
    }

    public static DoubleValue acos(Value value) {
        return wrap(Math.acos(unwrap(value)));
    }

}
