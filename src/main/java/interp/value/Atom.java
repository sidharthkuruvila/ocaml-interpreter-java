package interp.value;

import interp.ValueTag;

public class Atom implements Value {
    private final ValueTag tag;

    public Atom(ValueTag tag) {
        this.tag = tag;
    }

    public ValueTag getTag(){
        return tag;
    }
}
