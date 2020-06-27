package interp.customoperations;

import interp.ints.Int32CustomOperations;
import interp.ints.Int64CustomOperations;
import interp.ints.NativeIntCustomOperations;

import java.util.ArrayList;
import java.util.List;

public class CustomOperationsList {
    private final List<CustomOperations> customOperationsList = new ArrayList<>();



    public CustomOperationsList() {

        customOperationsList.add(Int64CustomOperations.getInstance());
        customOperationsList.add(Int32CustomOperations.getInstance());
        customOperationsList.add(NativeIntCustomOperations.getInstance());
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
