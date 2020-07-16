package interp.primitives;

import interp.InterpreterContext;
import interp.value.Value;

import java.util.Arrays;

public class UnimplementedPrimitive implements Primitive {
    String name;
    public UnimplementedPrimitive(String name) {
        this.name = name;
    }

    public String toString() {
        return String.format("Unimplemented Primitive: %s()", name);
    }

    @Override
    public Value call(InterpreterContext context, Value[] values) {
        throw new RuntimeException(String.format("Not yet implemented: %s(%s)", name, Arrays.asList(values)));
    }

    @Override
    public String getName() {
        return name;
    }
}
