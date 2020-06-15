package interp.primitives;

import interp.LongValue;
import interp.value.ObjectValue;
import interp.value.StringValue;
import interp.value.Value;

import static interp.Interpreter.valTrue;

public class SysGetConfig implements Primitive {
    @Override
    public Value call(Value[] values) {
        ObjectValue o = new ObjectValue(0, 3);

        o.setField(0, StringValue.ofString("JAVA"));
        o.setField(1, new LongValue(8*4));
        o.setField(2, valTrue);
        return o;
    }

    @Override
    public String getName() {
        return "caml_sys_get_config";
    }
}
