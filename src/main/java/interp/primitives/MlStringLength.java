package interp.primitives;

import interp.LongValue;
import interp.value.StringValue;
import interp.value.Value;

public class MlStringLength implements Primitive {
    @Override
    public Value call(Value[] values) {
        StringValue stringValue = (StringValue)values[0];
        return new LongValue(stringValue.length());
    }

    @Override
    public String getName() {
        return "caml_ml_string_length";
    }
}
