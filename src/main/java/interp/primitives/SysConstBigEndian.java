package interp.primitives;

import interp.InterpreterContext;
import interp.value.StringValue;
import interp.value.Value;

import static interp.Interpreter.valTrue;

public class SysConstBigEndian implements Primitive {
    @Override
    public Value call(InterpreterContext context, Value[] values) {
        return valTrue;
    }

    @Override
    public String getName() {
        return "caml_sys_const_big_endian";
    }
}
