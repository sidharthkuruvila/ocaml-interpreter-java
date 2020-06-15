package interp;

public class OOIdGenerator {
    private int id = 0;

    public int nextId() {
        id += 1;
        return id;
    }
}
