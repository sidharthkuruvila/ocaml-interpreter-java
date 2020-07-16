package interp.primitives;

import interp.InterpreterContext;
import interp.value.Value;

import java.util.function.BiFunction;

public class Func1CtxPrimitive<T extends Value> implements Primitive {
    private final String name;
    private final BiFunction<InterpreterContext, T, Value> fn;

    public Func1CtxPrimitive(String name, BiFunction<InterpreterContext, T, Value> fn) {
        this.name = name;
        this.fn = fn;
    }

    @Override
    public Value call(InterpreterContext context, Value[] values) {
        return fn.apply(context, (T)values[0]);
    }

    @Override
    public String getName() {
        return name;
    }
}
