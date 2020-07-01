package interp;

import interp.value.ObjectValue;
import interp.value.Value;

public class Obj {
    public static ObjectValue objBlock(Value tagValue, Value sizeValue) {
        int tag = ValueTag.of(LongValue.unwrapInt((LongValue) tagValue));
        int size = LongValue.unwrapInt((LongValue) sizeValue);
        if(tag == ValueTag.Custom_tag){
            Fail.caml_invalid_argument("Obj.new_block");
        }
        ObjectValue res = new ObjectValue(tag, size);
        return res;
    }
}

