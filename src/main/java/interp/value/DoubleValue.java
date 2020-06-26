package interp.value;

import interp.ValueTag;

public class DoubleValue implements Value {
    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public ValueTag getTag(){
        return ValueTag.Double_tag;
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

    public static DoubleValue div(DoubleValue value1, DoubleValue value2) {
        return wrap(unwrap(value1) / unwrap(value2));
    }

    public static DoubleValue abs(DoubleValue value) {
        return wrap(Math.abs(unwrap(value)));
    }

    private static DoubleValue wrap(double d) {
        return new DoubleValue(d);
    }
    public static double unwrap(DoubleValue value){
        return value.getValue();
    }

    public static DoubleValue sin(DoubleValue t) {
        return wrap(Math.sin(unwrap(t)));
    }

    public static DoubleValue asin(DoubleValue t) {
        return wrap(Math.asin(unwrap(t)));
    }

    public static DoubleValue cos(DoubleValue t) {
        return wrap(Math.cos(unwrap(t)));
    }

    public static DoubleValue acos(DoubleValue t) {
        return wrap(Math.acos(unwrap(t)));
    }

    public static DoubleValue tan(DoubleValue t) {
        return wrap(Math.tan(unwrap(t)));
    }

    public static DoubleValue atan(DoubleValue t) {
        return wrap(Math.atan(unwrap(t)));
    }

    public static DoubleValue atan2(DoubleValue t, DoubleValue u) {
        return wrap(Math.atan2(unwrap(t), unwrap(u)));
    }

    public static DoubleValue ceil(DoubleValue t) {
        return wrap(Math.ceil(unwrap(t)));
    }

    public static DoubleValue floor(DoubleValue t) {
        return wrap(Math.floor(unwrap(t)));
    }

    public static DoubleValue neg(DoubleValue t) {
        return wrap(-1 * unwrap(t));
    }

    public static DoubleValue cosh(DoubleValue t) {
        return wrap(Math.cosh(unwrap(t)));
    }

    public static DoubleValue sinh(DoubleValue t) {
        return wrap(Math.sinh(unwrap(t)));
    }

    public static DoubleValue tanh(DoubleValue t) {
        return wrap(Math.tanh(unwrap(t)));
    }

    public static DoubleValue fmod(DoubleValue t, DoubleValue u) {
        return wrap(unwrap(t) % unwrap(u));
    }
    public static DoubleValue fma(DoubleValue t, DoubleValue u, DoubleValue v) {
        return wrap(Math.fma(unwrap(t), unwrap(u), unwrap(v)));
    }


}
