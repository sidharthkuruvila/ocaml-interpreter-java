package interp.primitives;

import interp.value.Value;

import java.util.function.Function;

public class FuncNPrimitive implements Primitive{
    private final String name;
    private final Function<Value[], Value> fn;

    public FuncNPrimitive(String name, Function<Value[], Value> fn) {
        this.name = name;
        this.fn = fn;
    }

    @Override
    public Value call(Value[] values) {
        return fn.apply(values);
    }

    @Override
    public String getName() {
        return name;
    }
}
