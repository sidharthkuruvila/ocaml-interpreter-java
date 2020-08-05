package interp;

import interp.value.Value;

public class CodePointer implements Value {
    private final Code code;
    public final int index;

    public CodePointer(Code code, int index) {
        this.code = code;
        this.index = index;
    }

    public CodePointer inc() {
        assert index < code.code.length;
        return new CodePointer(code, index + 1);
    }

    public CodePointer dec() {
        assert index - 1 >= 0;
        return new CodePointer(code, index - 1);
    }

    public int get() {
        return code.code[index];
    }

    public LongValue getLongValue() {
        return new LongValue(get());
    }

    public CodePointer incN(int i) {
        assert index + i < code.code.length && index + i >= 0;
        return new CodePointer(code, index + i);
    }

    public int getN(int n) {
        return code.code[n + index];
    }

    public int switchInstruction(int newInstr) {
        int oldInstr = code.code[index];
        code.code[index] = newInstr;
        return oldInstr;
    }

    public Code getCode(){
        return code;
    }

    @Override
    public String toString() {
        return "CodePointer{" +
                "code=" + "..." +
                ", index=" + index +
                '}';
    }
}
