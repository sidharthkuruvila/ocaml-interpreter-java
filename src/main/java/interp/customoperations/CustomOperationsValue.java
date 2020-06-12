package interp.customoperations;

import interp.value.Value;

public class CustomOperationsValue implements Value {
    private final CustomOperations customOperations;
    private final Object data;

    public CustomOperationsValue(CustomOperations customOperations, Object data) {
        this.customOperations = customOperations;
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
