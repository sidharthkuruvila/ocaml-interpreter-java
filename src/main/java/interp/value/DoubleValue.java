package interp.value;

public class DoubleValue implements Value {
    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public static DoubleValue add(DoubleValue value, DoubleValue value1) {
        return new DoubleValue(value.getValue() + value1.getValue());
    }

    public static StringValue format(StringValue formatStringValue, DoubleValue nValue) {
        String formatString = formatStringValue.getString();
        double n = unwrap(nValue);
        String formatted = String.format(formatString, n);
        return StringValue.ofString(formatted);
    }

    public static DoubleValue mul(DoubleValue value1, DoubleValue value2) {
        return wrap(unwrap(value1) * unwrap(value2));
    }

    public static DoubleValue abs(DoubleValue value) {
        return wrap(Math.abs(unwrap(value)));
    }

    private static DoubleValue wrap(double d) {
        return new DoubleValue(d);
    }
    private static double unwrap(DoubleValue value){
        return value.getValue();
    }

    public static DoubleValue acos(DoubleValue value) {
        return wrap(Math.acos(unwrap(value)));
    }

}
