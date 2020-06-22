package interp.primitives;

import interp.functions.Func3;
import interp.value.Value;

public class Func3Primitive<T extends Value, U extends Value, V extends Value> implements Primitive {
    private final String name;
    private final Func3<T, U, V, Value> fn;

    public Func3Primitive(String name, Func3<T, U, V, Value> fn) {
        this.name = name;
        this.fn = fn;
    }

    @Override
    public Value call(Value[] values) {
        return fn.apply((T)values[0], (U)values[1], (V)values[2]);
    }

    @Override
    public String getName() {
        return name;
    }
}

