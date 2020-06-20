package interp.primitives;

import interp.functions.Func3;
import interp.value.Value;

public class Func3Primitive implements Primitive {
    private final String name;
    private final Func3<Value, Value, Value, Value> fn;

    public Func3Primitive(String name, Func3<Value, Value, Value, Value> fn) {
        this.name = name;
        this.fn = fn;
    }

    @Override
    public Value call(Value[] values) {
        return fn.apply(values[0], values[1], values[2]);
    }

    @Override
    public String getName() {
        return name;
    }
}

