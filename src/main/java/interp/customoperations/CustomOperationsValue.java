package interp.customoperations;

import interp.ValueTag;
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

    public int getTag() {
        return ValueTag.Custom_tag;
    }

    public boolean hasHash() {
        return ops().hash != null;
    }

    public long hash() {
        return ops().hash.apply(getData());
    }

}
