package interp.primitives;

import interp.LongValue;
import interp.OOIdGenerator;
import interp.value.Value;

public class FreshOOIdPrimitive implements Primitive {
    private final OOIdGenerator ooIdGenerator;

    public FreshOOIdPrimitive(OOIdGenerator ooIdGenerator) {
        this.ooIdGenerator = ooIdGenerator;
    }

    @Override
    public Value call(Value[] values) {
        return new LongValue(ooIdGenerator.nextId());
    }

    @Override
    public String getName() {
        return "caml_fresh_oo_id";

    }
}
