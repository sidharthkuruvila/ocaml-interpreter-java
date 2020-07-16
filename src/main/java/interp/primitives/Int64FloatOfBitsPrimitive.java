package interp.primitives;

import interp.InterpreterContext;
import interp.value.DoubleValue;
import interp.customoperations.CustomOperationsValue;
import interp.value.Value;

public class Int64FloatOfBitsPrimitive implements Primitive{

    @Override
    public Value call(InterpreterContext context, Value[] values) {
        CustomOperationsValue customOperationsValue = (CustomOperationsValue)values[0];
        return new DoubleValue((long)customOperationsValue.getData());
    }

    @Override
    public String getName() {
        return "caml_int64_float_of_bits";
    }
}
