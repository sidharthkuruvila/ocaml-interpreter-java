package interp.customoperations;

import interp.ints.IntCustomOperations;

import java.util.ArrayList;
import java.util.List;

public class CustomOperationsList {
    private final List<CustomOperations> customOperationsList = new ArrayList<>();



    public CustomOperationsList() {
        customOperationsList.add(new IntCustomOperations());
    }

    public CustomOperations findCustomOperations(String ident) {
        for(CustomOperations customOperations : customOperationsList) {
            if(ident.equals(customOperations.identifier)){
                return customOperations;
            }
        }
        throw new RuntimeException("No custom operations found");
    }
}
