package interp.stack;

import interp.LongValue;
import interp.value.ObjectValue;
import interp.value.Value;

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
        return stack.stack.get(size-1);
    }

    /* To be used when the stack pointer is uses as a frame pointer
    TODO Should the frame pointer be an separate type?
     */

    public Value getLocal(int i) {
        int extraArgs = LongValue.unwrapInt((LongValue) peekN(2));
        return peekN(i + 3 + extraArgs);
    }

    private Value peekN(int i ) {
        return stack.stack.get(size - 1 - i);
    }

    public ObjectValue getEnv() {
        return (ObjectValue) peekN(1);
    }
}
