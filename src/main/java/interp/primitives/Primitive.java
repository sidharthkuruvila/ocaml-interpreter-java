package interp.primitives;

import interp.Value;

public interface Primitive {
    Value call(Value[] values);

    String getName();
}
