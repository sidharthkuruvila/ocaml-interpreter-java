package interp.value;

public class Atom implements Value {
    private final int tag;

    public Atom(int tag) {
        this.tag = tag;
    }
}
