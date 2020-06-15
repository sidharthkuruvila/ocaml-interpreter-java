package interp;

import interp.value.Value;

import java.util.HashMap;
import java.util.Map;

public class NamedValues {
    Map<String, Value> map = new HashMap<>();
    public void add(String name, Value value) {
        map.put(name, value);
    }
}
