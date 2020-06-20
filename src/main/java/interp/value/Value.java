package interp.value;

import interp.LongValue;

import static interp.Interpreter.valFalse;
import static interp.Interpreter.valTrue;

public interface Value {
    Value Val_emptylist = new LongValue(0);

    static Value booleanValue(boolean bool) {
        if(bool) {
            return valTrue;
        } else {
            return valFalse;
        }
    }

    static Value identity(Value value) {
        return value;
    }
}
