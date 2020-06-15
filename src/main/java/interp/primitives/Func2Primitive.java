package interp.primitives;

import interp.value.Value;

import java.util.function.BiFunction;

public class Func2Primitive implements Primitive {
    private final String name;
    private final BiFunction<Value, Value, Value> fn;

    public Func2Primitive(String name, BiFunction<Value, Value, Value> fn) {
        this.name = name;
        this.fn = fn;
    }

    @Override
    public Value call(Value[] values) {
        return fn.apply(values[0], values[1]);
    }

    @Override
    public String getName() {
        return name;
    }
}
