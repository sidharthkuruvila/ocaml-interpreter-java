package interp.primitives;

import interp.InterpreterContext;
import interp.LongValue;
import interp.OOIdGenerator;
import interp.value.Value;

public class FreshOOIdPrimitive implements Primitive {
    private final OOIdGenerator ooIdGenerator;

    public FreshOOIdPrimitive(OOIdGenerator ooIdGenerator) {
        this.ooIdGenerator = ooIdGenerator;
    }

    @Override
    public Value call(InterpreterContext context, Value[] values) {
        return new LongValue(ooIdGenerator.nextId());
    }

    @Override
    public String getName() {
        return "caml_fresh_oo_id";

    }
}
