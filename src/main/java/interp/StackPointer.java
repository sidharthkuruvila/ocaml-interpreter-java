package interp;

public class StackPointer implements Value {

    private final int size;

    public StackPointer(int size) {

        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
