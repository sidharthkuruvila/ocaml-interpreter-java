package interp.primitives;

import interp.value.StringValue;
import interp.value.Value;

public class SysExecutableName  implements Primitive {
    @Override
    public Value call(Value[] values) {
        return new StringValue("ocamlc".getBytes());
    }

    @Override
    public String getName() {
        return "caml_sys_executable_name";
    }
}
