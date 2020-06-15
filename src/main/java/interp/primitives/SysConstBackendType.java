package interp.primitives;

import interp.LongValue;
import interp.value.Value;

public class SysConstBackendType implements Primitive{
    @Override
    public Value call(Value[] values) {
        return new LongValue(1);
    }

    @Override
    public String getName() {
        return "caml_sys_const_backend_type";
    }
}
