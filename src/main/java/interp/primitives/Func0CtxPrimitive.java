package interp.primitives;

import interp.InterpreterContext;
import interp.value.Value;

import java.util.function.Function;

public class Func0CtxPrimitive implements Primitive {
    private final String name;
    private final Function<InterpreterContext, Value> fn;

    public Func0CtxPrimitive(String name, Function<InterpreterContext, Value> fn) {
        this.name = name;
        this.fn = fn;
    }

    @Override
    public Value call(InterpreterContext context, Value[] values) {
        return fn.apply(context);
    }

    @Override
    public String getName() {
        return name;
    }
}
