package interp.primitives;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PrimitiveRegistry {

    private final Map<String, Primitive> primitives = new HashMap<>();

    public Primitives getPrimitives(List<String> names) {
        return new Primitives(names.stream()
                .map((String name) -> primitives.getOrDefault(name, new UnimplementedPrimitive(name)))
                .collect(Collectors.toList()));
    }

    public void addPrimitive(Primitive primitive) {
        primitives.put(primitive.getName(), primitive);
    }
}
