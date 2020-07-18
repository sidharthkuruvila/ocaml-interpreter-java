package interp;

import interp.stack.ValueStack;
import interp.value.ObjectValue;
import interp.value.StringValue;
import interp.value.Value;

import java.util.List;

import static interp.Interpreter.valFalse;
import static interp.Interpreter.valUnit;
import static interp.value.Value.booleanValue;

public class Backtrace {

    public Backtrace() {
    }

    public Value recordBacktrace(LongValue value) {
        return valUnit;
    }

    public Value getExceptionRawBacktrace(InterpreterContext context) {
        List<CodePointer> backtrace = context.getBacktrace();
        ObjectValue objectValue = new ObjectValue(0, backtrace.size());
        for(int i = 0; i < backtrace.size(); i++) {
            objectValue.setField(i, backtrace.get(i));
        }
        return objectValue;
    }

    public Value convertRawBacktrace(InterpreterContext context, ObjectValue objectValue) {
        ObjectValue array = new ObjectValue(0, objectValue.getSize());
        for(int i = 0; i < objectValue.getSize(); i++) {
            boolean isRaise = i == 0;
            CodePointer codePointer = (CodePointer)objectValue.getField(i);
            ObjectValue debugInfo = getBacktraceItem(context, codePointer, isRaise);
            array.setField(i, debugInfo);
        }
        return array;
    }

    private ObjectValue getBacktraceItem(InterpreterContext context, CodePointer codePointer, boolean isRaise) {
        InterpreterContext.StackFrame stackFrame = getStackFrame(context, codePointer);
        Executable.DebugEvent debugEvent = stackFrame.getDebugEvent();
        ObjectValue debugInfo = new ObjectValue(0, 7);
        debugInfo.setField(0, booleanValue(isRaise));
        debugInfo.setField(1, StringValue.ofString(debugEvent.getFilename()));
        debugInfo.setField(2, LongValue.wrap(debugEvent.getLineNumber()));
        debugInfo.setField(3, LongValue.wrap(debugEvent.getStartChar()));
        debugInfo.setField(4, LongValue.wrap(debugEvent.getEndChar()));
        debugInfo.setField(5, valFalse);
        debugInfo.setField(6, StringValue.ofString(debugEvent.getDefname()));
        return debugInfo;
    }

    private InterpreterContext.StackFrame getStackFrame(InterpreterContext context, CodePointer framePointer) {
        List<Executable.DebugEvent> debugEvents  = context.getDebugEvents();
        Executable.DebugEvent previousEvent = debugEvents.get(0);
        int previousIndex = debugEvents.get(0).getCodePointer().index;
        int needleIndex = framePointer.index;
        InterpreterContext.StackFrame stackFrame = null;

        for(Executable.DebugEvent debugEvent : debugEvents) {
            int nextIndex = debugEvent.getCodePointer().index;
            if(needleIndex<=nextIndex && previousIndex<=needleIndex){
                stackFrame = new InterpreterContext.StackFrame(framePointer, previousEvent);
            }
            previousEvent = debugEvent;
            previousIndex = nextIndex;
        }
        return stackFrame;
    }

    public Value rawBacktraceSlot(ObjectValue objectValue, LongValue indexValue) {
        int index = LongValue.unwrapInt(indexValue);
        if(index >= objectValue.getSize()){
            Fail.caml_invalid_argument("Printexc.get_raw_backtrace_slot: \n" +
                    "index out of bounds");
        }
        CodePointer stackFrame = (CodePointer)objectValue.getField(index);
        return  stackFrame;
    }

    public Value convertRawBacktraceSlot(InterpreterContext context, CodePointer codePointer) {
        return getBacktraceItem(context, codePointer, true);
    }
}
