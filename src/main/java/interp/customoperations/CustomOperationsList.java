package interp.customoperations;

import interp.ints.Int32CustomOperations;
import interp.ints.Int64CustomOperations;
import interp.ints.NativeIntCustomOperations;

import java.util.ArrayList;
import java.util.List;

public class CustomOperationsList {
    private final List<CustomOperations> customOperationsList = new ArrayList<>();



    public CustomOperationsList() {

        customOperationsList.add(new Int64CustomOperations());
        customOperationsList.add(new Int32CustomOperations());
        customOperationsList.add(new NativeIntCustomOperations());
    }

    public CustomOperations findCustomOperations(String ident) {
        for(CustomOperations customOperations : customOperationsList) {
            if(ident.equals(customOperations.identifier)){
                return customOperations;
            }
        }
        throw new RuntimeException("No custom operations found for: " + ident);
    }
}
