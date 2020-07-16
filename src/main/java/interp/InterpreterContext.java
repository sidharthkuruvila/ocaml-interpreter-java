package interp;

import interp.stack.StackPointer;
import interp.stack.ValueStack;
import interp.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class InterpreterContext {
    private final ValueStack stack;
    private final List<Executable.DebugEvent> debugEvents;
    private Value lastException;
    private List<CodePointer> backtrace = new ArrayList<>();

    public InterpreterContext(ValueStack stack, List<Executable.DebugEvent> debugEvents) {

        this.stack = stack;
        this.debugEvents = debugEvents;
    }

    public ValueStack getStack() {
        return stack;
    }

    public List<Executable.DebugEvent> getDebugEvents() {
        return debugEvents;
    }

    public void setLastException(Value lastException) {
        this.lastException = lastException;
    }

    public Value getLastException() {
        return lastException;
    }

    public void setBacktrace(List<CodePointer> backtrace) {
        this.backtrace = backtrace;
    }

    public List<CodePointer> getBacktrace() {
        return backtrace;
    }

    public static class StackFrame {

        private final CodePointer framePointer;
        private final Executable.DebugEvent debugEvent;

        public StackFrame(CodePointer framePointer, Executable.DebugEvent debugEvent) {

            this.framePointer = framePointer;
            this.debugEvent = debugEvent;
        }

        public CodePointer getFramePointer() {
            return framePointer;
        }

        public Executable.DebugEvent getDebugEvent() {
            return debugEvent;
        }
    }


    public List<StackFrame> getStackFrames() {
        List<StackFrame> stackFrames = new ArrayList<>();
        List<CodePointer> framePointers = addFramePointers(new ArrayList<>());

        for(CodePointer framePointer : framePointers) {

            StackFrame stackFrame = getStackFrame(framePointer);

            if(stackFrame != null) {
                stackFrames.add(stackFrame);
            }

        }
        return stackFrames;
    }

    private StackFrame getStackFrame(CodePointer framePointer) {
        Executable.DebugEvent previousEvent = debugEvents.get(0);
        int previousIndex = debugEvents.get(0).getCodePointer().index;
        int needleIndex = framePointer.index;
        StackFrame stackFrame = null;
        for(Executable.DebugEvent debugEvent : debugEvents) {
            int nextIndex = debugEvent.getCodePointer().index;
            if(needleIndex<=nextIndex && previousIndex<=needleIndex){
                stackFrame = new StackFrame(framePointer, previousEvent);
            }
            previousEvent = debugEvent;
            previousIndex = nextIndex;
        } return stackFrame;
    }

    public List<CodePointer> addFramePointers(List<CodePointer> framePointers) {
        StackPointer sp = stack.pointer();
        while(sp.getSize() > 0) {
            Value v = sp.get();
            if(v instanceof CodePointer) {
                if(sp.getSize() > 1
                        && (sp.incN(-1).get() instanceof StackPointer)) {
                    break;
                }
                framePointers.add((CodePointer)v);
            }
            sp = sp.incN(-1);
        }
        return framePointers;
    }
}
