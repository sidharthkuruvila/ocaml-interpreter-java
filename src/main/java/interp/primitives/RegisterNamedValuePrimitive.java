package interp.primitives;

import interp.NamedValues;
import interp.value.StringValue;
import interp.value.Value;

import static interp.Interpreter.valUnit;

public class RegisterNamedValuePrimitive implements Primitive {

    private final NamedValues namedValues;

    public RegisterNamedValuePrimitive(NamedValues namedValues) {
        this.namedValues = namedValues;
    }

    @Override
    public Value call(Value[] values) {
        String name = ((StringValue)values[0]).getString();
        Value value = values[1];

        return valUnit;
    }

    @Override
    public String getName() {
        return "caml_register_named_value";
    }
}
