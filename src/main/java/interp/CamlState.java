package interp;

import interp.stack.StackPointer;

public class CamlState {
    private StackPointer trapSp = null;
    private boolean backTraceActive;
    private StackPointer externSp;

    public StackPointer getTrapSp() {
        return  trapSp;
    }

    public void setTrapSp(StackPointer trapSp) {
        this.trapSp = trapSp;
    }

    public boolean getBackTraceActive() {
        return backTraceActive;
    }

    public void setBackTraceActive(boolean backTraceActive) {
        this.backTraceActive = backTraceActive;
    }

    public void setExternSp(StackPointer externSp) {
        this.externSp = externSp;
    }

    public StackPointer getExternSp() {
        return externSp;
    }
}
