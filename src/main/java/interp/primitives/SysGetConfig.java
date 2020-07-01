package interp.primitives;

import interp.LongValue;
import interp.ValueTag;
import interp.value.ObjectValue;
import interp.value.StringValue;
import interp.value.Value;

import static interp.Interpreter.valTrue;

public class SysGetConfig implements Primitive {
    @Override
    public Value call(Value[] values) {
        ObjectValue o = new ObjectValue(ValueTag.PAIR_TAG, 3);

        o.setField(0, StringValue.ofString("Unix"));
        o.setField(1, new LongValue(8*4));
        o.setField(2, valTrue);
        return o;
    }

    @Override
    public String getName() {
        return "caml_sys_get_config";
    }
}
