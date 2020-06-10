package interp.primitives;

import java.util.List;

public class Primitives {
    private final List<Primitive> primitives;

    public Primitives(List<Primitive> primitives) {
        this.primitives =primitives;
    }

    public Primitive get(int i) {
        return primitives.get(i);
    }
}
