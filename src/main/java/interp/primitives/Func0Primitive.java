package interp.primitives;

import interp.InterpreterContext;
import interp.value.Value;

import java.util.function.Supplier;

public class Func0Primitive implements Primitive {
    private final String name;
    private final Supplier<Value> fn;

    public Func0Primitive(String name, Supplier<Value> fn) {
        this.name = name;
        this.fn = fn;
    }
    @Override
    public Value call(InterpreterContext context, Value[] values) {
        return fn.get();
    }

    @Override
    public String getName() {
        return name;
    }
}
