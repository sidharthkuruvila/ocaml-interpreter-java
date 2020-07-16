package interp.primitives;

import interp.InterpreterContext;
import interp.value.Value;

public interface Primitive {
    Value call(InterpreterContext context, Value[] values);

    String getName();
}
