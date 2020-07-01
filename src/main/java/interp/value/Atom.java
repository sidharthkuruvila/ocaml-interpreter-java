package interp.value;

import interp.ValueTag;

public class Atom implements Value {
    private final int tag;

    public Atom(int tag) {
        this.tag = tag;
    }

    public int getTag(){
        return tag;
    }
}
