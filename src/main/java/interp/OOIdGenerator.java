package interp;

import interp.value.ObjectValue;
import interp.value.Value;

public class OOIdGenerator {
    private int id = 0;

    public int nextId() {
        id += 1;
        return id;
    }

    public Value setOOId(ObjectValue t) {
        t.setField(1, LongValue.wrap(nextId()));
        return t;
    }
}
