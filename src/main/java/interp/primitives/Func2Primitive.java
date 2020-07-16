package interp.primitives;

import interp.InterpreterContext;
import interp.value.Value;

import java.util.function.BiFunction;

public class Func2Primitive<T extends Value, U extends Value> implements Primitive {
    private final String name;
    private final BiFunction<T, U, Value> fn;

    public Func2Primitive(String name, BiFunction<T, U, Value> fn) {
        this.name = name;
        this.fn = fn;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Value call(InterpreterContext context, Value[] values) {
        return fn.apply((T)values[0], (U)values[1]);
    }

    @Override
    public String getName() {
        return name;
    }
}
