package interp.primitives;

import interp.value.Value;

import java.util.function.Function;

public class Func1Primitive<T extends Value> implements Primitive{
    private final String name;
    private final Function<T, Value> fn;

    public Func1Primitive(String name, Function<T, Value> fn) {
        this.name = name;
        this.fn = fn;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Value call(Value[] values) {
        return fn.apply((T)values[0]);
    }

    @Override
    public String getName() {
        return name;
    }
}
