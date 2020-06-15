package interp.primitives;

import interp.value.StringValue;
import interp.value.Value;

import static interp.Interpreter.valTrue;

public class SysConstBigEndian implements Primitive {
    @Override
    public Value call(Value[] values) {
        return valTrue;
    }

    @Override
    public String getName() {
        return "caml_sys_const_big_endian";
    }
}
