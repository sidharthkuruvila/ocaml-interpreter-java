package interp;

import interp.exceptions.DivideByZeroError;
import interp.value.StringValue;
import interp.value.Value;

import java.util.function.BiFunction;
import java.util.regex.Pattern;

import static interp.Interpreter.valFalse;
import static interp.Interpreter.valTrue;

public class LongValue implements Value {
    private long value;

    public LongValue(long value) {
        this.value = value;
    }

    long getValue() {
        return value;
    }

    void setValue(long value) {
        this.value = value;
    }

    public boolean equals(Object b) {
        if(b != null &&b instanceof  LongValue) {
            return ((LongValue)b).value == value;
        }
        return false;
    }

    public LongValue negate() {
        return new LongValue(-1 * value);
    }

    public LongValue add(LongValue b) {
        return new LongValue(value + b.value);
    }

    public LongValue sub(LongValue b) {
        return new LongValue(value - b.value);
    }

    public LongValue mul(LongValue b) {
        return new LongValue(value * b.value);
    }

    public LongValue div(LongValue b) {
        long divisor = (b).getValue();
        if (divisor == 0) {
            throw new DivideByZeroError();
        }
        return new LongValue(value / divisor);
    }

    public LongValue mod(LongValue b) {
        long divisor = (b).getValue();
        if (divisor == 0) {
            throw new DivideByZeroError();
        }
        return new LongValue(value % divisor);
    }

    public LongValue and(LongValue b) {
        return new LongValue(value & b.value);
    }

    public LongValue or(LongValue b) {
        return new LongValue(value | b.value);
    }

    public LongValue xor(LongValue b) {
        return new LongValue(value ^ b.value);
    }

    public LongValue lsl(LongValue b) {
        return new LongValue(value << b.value);
    }

    public LongValue lsr(LongValue b) {
        return new LongValue(value >> b.value);
    }

    public LongValue ulsr(LongValue b) {
        return new LongValue(value >> b.value);
    }

    public LongValue eq(LongValue b) {
        return value == b.value? valTrue: valFalse;
    }
    public LongValue neq(LongValue b) {
        return value != b.value? valTrue: valFalse;
    }
    public LongValue lt(LongValue b) {
        return value < b.value? valTrue: valFalse;
    }

    public LongValue le(LongValue b) {
        return value <= b.value? valTrue: valFalse;
    }

    public LongValue gt(LongValue b) {
        return value > b.value? valTrue: valFalse;
    }

    public LongValue ge(LongValue b) {
        return value >= b.value? valTrue: valFalse;
    }

    public LongValue ult(LongValue b) {
        return Long.compareUnsigned(value, b.value) < 0 ? valTrue: valFalse;
    }

    public LongValue uge(LongValue b) {
        return Long.compareUnsigned(value, b.value) >= 0 ? valTrue: valFalse;
    }

    public String toString() {
        return Long.toString(value);
    }

    //Can loose information
    public int getIntValue() {
        return (int)value;
    }

    public static LongValue parseString(Value stringValue) {
        String string = ((StringValue)stringValue).toString();
        int i = 0;
        int sign = 1;
        if(string.charAt(i) == '-' ) {
            i+=1;
            sign = -1;
        } else if(string.charAt(i) == '+') {
            i+=1;
        }
        int base = 10;
        boolean isSigned = true;
        if(string.charAt(i) == '0' && string.length() > i + 1) {
            switch(string.charAt(i+1)) {
                case 'x': case 'X':
                    base = 16; isSigned = false; i+=2;
                case 'o': case 'O':
                    base = 8; isSigned = false; i+=2;
                case 'b': case 'B':
                    base = 2; isSigned = false; i+=2;
                case 'u': case 'U':
                    isSigned = false; i += 2;
            }
        }
        String cleaned = string.substring(i).replace("_", "");
        long parsedValue = Long.parseLong(cleaned, base);
        if(isSigned) {
            parsedValue = sign * parsedValue;
        }
        return new LongValue(parsedValue);
    }

    public static LongValue ofInt(Value value) {
        return (LongValue)value;
    }

    public static Value lsl2(Value value, Value value1) {
        return ((LongValue) value).lsl((LongValue)value1);
    }
}
