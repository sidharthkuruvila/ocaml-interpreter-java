package interp.customoperations;

import interp.value.Value;

public class CustomOperationsValue<V> implements Value {
    private final CustomOperations<V> customOperations;
    private final V data;

    public CustomOperationsValue(CustomOperations<V> customOperations, V data) {
        this.customOperations = customOperations;
        this.data = data;
    }

    public CustomOperations<V> ops() {
        return customOperations;
    }

    public V getData() {
        return data;
    }


}
