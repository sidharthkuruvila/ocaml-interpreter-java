package interp.stack;

import interp.Value;

public class StackPointer implements Value {

    private final ValueStack stack;
    private final int size;

    public StackPointer(ValueStack stack, int size) {
        this.stack = stack;

        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public StackPointer incN(int i) {
        return new StackPointer(stack,size + i);
    }

    public Value get() {
        return stack.stack.get(size);
    }
}
