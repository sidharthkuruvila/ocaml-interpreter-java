package interp.stack;

import interp.CodePointer;
import interp.value.Value;

import java.util.ArrayList;
import java.util.List;

public class ValueStack {


    final List<Value> stack = new ArrayList<>();

    public void push(Value value) {
        assert value != null;
        stack.add(value);
    }

    public Value get(int i) {
        return stack.get(indexAt(i));
    }

    public void popNIgnore(int n) {
        int sizeBefore = stack.size();
        int end = stack.size() - 1;
        for (int i = 0; i < n; i++) {
            stack.remove(end - i);
        }
        assert stack.size() == sizeBefore - n;
    }

    public void set(int i, Value value) {
        stack.set(indexAt(i), value);
    }

    private int indexAt(int i) {
        return stack.size() - 1 - i;
    }

    public StackPointer pointer() {
        return new StackPointer(this, stack.size());
    }

    public void reset(StackPointer externSp) {
        int oldLen = stack.size();
        int newLen = externSp.getSize();
        assert newLen <= oldLen;
        popNIgnore(oldLen - newLen);
        assert stack.size() == newLen;
    }

    public Value pop() {
        return stack.remove(indexAt(0));
    }

    public int size() {
        return stack.size();
    }
}
