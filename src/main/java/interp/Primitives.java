package interp;

import java.util.Arrays;
import java.util.List;

interface Primitive {
    Value call(Value[] values);

    String getName();
}

class UnimplementedPrimitive implements Primitive {
    String name;
    public UnimplementedPrimitive(String name) {
        this.name = name;
    }

    public String toString() {
        return String.format("Unimplemented Primitive: %s()", name);
    }

    @Override
    public Value call(Value[] values) {
        throw new RuntimeException(String.format("Not yet implemented: %s(%s)", name, Arrays.asList(values)));
    }

    @Override
    public String getName() {
        return name;
    }
}
public class Primitives {


    private final List<Primitive> primitives;

    public Primitives(List<Primitive> primitives) {
        this.primitives =primitives;
    }

    public Primitive get(int i) {
        return primitives.get(i);
    }
}
