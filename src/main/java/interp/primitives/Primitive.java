package interp.primitives;

import interp.value.Value;

public interface Primitive {
    Value call(Value[] values);

    String getName();
}
