package interp.debugger;

import interp.*;
import interp.marshalling.Extern;
import interp.stack.StackPointer;
import interp.value.DoubleArray;
import interp.value.ObjectValue;
import interp.value.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static interp.Interpreter.Instructions.BREAK;
import static interp.Interpreter.Instructions.EVENT;


/**
 * Long representations of values
 * <p>
 * this is a bit leaky and should be cleared regularly
 */
class ValuePointers {
    HashMap<Value, Long> toMap = new HashMap<>();
    Map<Long, Value> fromMap = new HashMap<>();
    long counter = 0;

    long pointerFor(Value v) {
        if (v instanceof LongValue) {
            return (LongValue.unwrap((LongValue) v) << 1 + 1);
        } else {
            if (toMap.containsKey(v)) {
                return toMap.get(v);
            } else {
                counter += 2;
                fromMap.put(counter, v);
                toMap.put(v, counter);
                return counter;
            }

        }
    }

    Value valueFor(long ptr) {
        if ((ptr & 1) == 1) {
            return LongValue.wrap((ptr >> 1));
        } else {
            return fromMap.get(ptr);
        }
    }

    public void clear() {
        toMap.clear();
        fromMap.clear();
    }
}


/* Requests from the debugger to the runtime system */

;

/* Replies to a REQ_GO request. All replies are followed by three uint32_t:
   - the value of the event counter
   - the position of the stack
   - the current pc.
   The REP_CODE_DEBUG_INFO reply is also followed by:
   - the newly added debug information.
   The REP_CODE_{UN,}LOADED reply is also followed by:
   - the code fragment index. */

enum DebuggerReply {
    REP_EVENT('e'),
    /* Event counter reached 0. */
    REP_BREAKPOINT('b'),
    /* Breakpoint hit. */
    REP_EXITED('x'),
    /* Program exited by calling exit or reaching the end of the source. */
    REP_TRAP('s'),
    /* Trap barrier crossed. */
    REP_UNCAUGHT_EXC('u'),
    /* Program exited due to a stray exception. */
    REP_CODE_DEBUG_INFO('D'),
    /* Additional debug info loaded. */
    REP_CODE_LOADED('L'),
    /* Additional code loaded. */
    REP_CODE_UNLOADED('U');
    /* Additional code unloaded. */

    int code;

    DebuggerReply(char code) {
        this.code = code;
    }
};

public class Debugging {
    private final CamlState camlState;
    private final CodeFragmentTable codeFragmentTable;
    private final InterpreterContext context;
    private final Extern extern;
    private InputStream is;
    private OutputStream os;
    private java.nio.channels.ReadableByteChannel rc;
    private java.nio.channels.WritableByteChannel wc;
    private final ValuePointers valuePointers = new ValuePointers();

    public Debugging(CamlState camlState, InterpreterContext context, CodeFragmentTable codeFragmentTable, InputStream is, OutputStream os) {
        this.camlState = camlState;
        this.codeFragmentTable = codeFragmentTable;
        this.context = context;
        this.is = is;
        this.os = os;
        this.extern = new Extern(codeFragmentTable);
    }

    public void debugBreakpoint() {
        writeReply(DebuggerReply.REP_BREAKPOINT);
        readCommands();
    }

    private void readCommands() {
        StackPointer frame = null;
        while (true) {
            DebuggerCommand command = readCommand();
            switch (command) {
                case REQ_SET_EVENT: {
                    int frag = readWord();
                    int pos = readWord();
                    context.setBreakpoint(codeFragmentTable.getCodePointer(frag, pos), EVENT);
                    break;
                }
                case REQ_SET_BREAKPOINT: {
                    int frag = readWord();
                    int pos = readWord();
                    context.setBreakpoint(codeFragmentTable.getCodePointer(frag, pos), BREAK);
                    break;
                }
                case REQ_RESET_INSTR: {
                    int frag = readWord();
                    int pos = readWord();
                    context.unsetBreakpoint(codeFragmentTable.getCodePointer(frag, pos));
                    break;
                }
                case REQ_CHECKPOINT:
                    throw new RuntimeException("Checkpointing not supported");
                case REQ_GO:
                    context.setEventCount(readWord());
                    valuePointers.clear();
                    return;
                case REQ_STOP:
                    System.exit(0);
                    break;
                case REQ_WAIT:
                    throw new RuntimeException("REQ_WAIT command");
                case REQ_INITIAL_FRAME:
                    frame = context.getStack().pointer();
                    /* Fall through */
                case REQ_GET_FRAME:
                    writeWord(frame.getSize());
                    if (frame.getSize() > 0) {
                        CodePointer cp = (CodePointer) frame.get();
                        Optional<Integer> fragO = codeFragmentTable.fragmentIndexForCode(cp.getCode());
                        if (fragO.isPresent()) {
                            writeWord(fragO.get());
                            writeWord(cp.index);
                        } else {
                            writeWord(0);
                            writeWord(0);
                        }
                    } else {
                        writeWord(0);
                        writeWord(0);
                    }
                    flush();
                    break;
                case REQ_SET_FRAME: {
                    int i = readWord();
                    frame = context.getStack().pointerAt(i);
                    break;
                }
                case REQ_UP_FRAME: {
                    int i = readWord();
                    int extraArgs = LongValue.unwrapInt((LongValue) frame.incN(-2).get());
                    StackPointer newframe = frame.incN(-1 * (extraArgs + i + 3));
                    if (newframe.getSize() > 0) {
                        CodePointer cp = (CodePointer) frame.get();
                        Optional<Integer> fragO = codeFragmentTable.fragmentIndexForCode(cp.getCode());
                        if (fragO.isPresent()) {
                            frame = newframe;
                            writeWord(frame.getSize());
                            writeWord(fragO.get());
                            writeWord(cp.index);
                        } else {
                            writeWord(-1);
                        }
                    } else {
                        writeWord(-1);
                    }
                    flush();
                    break;
                }
                case REQ_SET_TRAP_BARRIER: {
                    int i = readWord();
                    context.setTrapBarrier(context.getStack().pointerAt(i));
                    break;
                }
                case REQ_GET_LOCAL: {
                    int i = readWord();
                    writeValue(frame.getLocal(i));
                    flush();
                    break;
                }
                case REQ_GET_ENVIRONMENT: {
                    int i = readWord();

                    writeValue(frame.getEnv().getField(i));
                    flush();
                    break;
                }
                case REQ_GET_GLOBAL: {
                    int i = readWord();
                    writeValue(context.getGlobalData().getField(i));
                    flush();
                    break;
                }
                case REQ_GET_ACCU: {
                    writeValue(context.getStack().get(0));
                    flush();
                    break;
                }
                case REQ_GET_HEADER: {
                    Value value = readValue();
                    writeWord(value.getHeader());
                    flush();
                    break;
                }
                case REQ_GET_FIELD: {
                    Value value = readValue();
                    int i = readWord();
                    if (value instanceof ObjectValue) {
                        ObjectValue objectValue = (ObjectValue) value;
                        writeByte(0);
                        writeValue(objectValue.getField(i));
                    } else {
                        DoubleArray doubleArray = (DoubleArray) value;
                        double d = doubleArray.getDoubleField(i);
                        writeDouble(d);
                    }
                    flush();
                    break;
                }
                case REQ_MARSHAL_OBJ: {
                    Value value = readValue();
                    writeMarshalledValue(value);
                    flush();
                    break;
                }
                case REQ_GET_CLOSURE_CODE: {
                    throw new RuntimeException();
//                    Value value = readValue();
//                    found = caml_find_code_fragment(( char*)Code_val(val), &frag, &cf);
//                    CAMLassert(found);
//                    caml_putword(dbg_out, frag);
//                    caml_putword(dbg_out, ( char*)Code_val(val) - cf -> code_start);
//                    caml_flush(dbg_out);
//                    break;
                }
                case REQ_SET_FORK_MODE: {
                    throw new RuntimeException("Not implemented");
//        caml_debugger_fork_mode = caml_getword(dbg_in);
//        break;
                }
//    }
            }
        }
    }

    private void writeBytes(byte[] bytes) {
        try {
            os.write(bytes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeDouble(double d) {
        try {
            ByteBuffer buf = ByteBuffer.allocate(Double.BYTES);
            buf.putDouble(d);
            os.write(buf.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeByte(int i) {
        try {
            os.write(i);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeMarshalledValue(Value value) {
        extern.outputValue(os, value);
    }

    private Value readValue() {
        long ptr = readLong();
        return valuePointers.valueFor(ptr);
    }

    private long readLong() {
        try {
            ByteBuffer bf = ByteBuffer.allocate(Long.BYTES);
            is.read(bf.array());
            return bf.getLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeValue(Value value) {
        long ptr = valuePointers.pointerFor(value);
        writeLong(ptr);
    }

    private void writeLong(long l) {
        try {
            ByteBuffer buf = ByteBuffer.allocate(Long.BYTES);
            buf.putLong(l);
            os.write(buf.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    int readWord() {
        try {
            ByteBuffer bf = ByteBuffer.allocate(Integer.BYTES);
            is.read(bf.array());
            return bf.getInt();
//        is.read(bf.array())

//            int res = 0;
//            for (int i = 0; i < 4; i++) {
//                res = (res << 8) + is.read();
//            }
//            return res;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeWord(int word) {
        try {
            ByteBuffer bf = ByteBuffer.allocate(Integer.BYTES);
            bf.putInt(word);
            os.write(bf.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    DebuggerCommand readCommand() {
        try {
            int n = is.read();
            System.out.println(n);
            System.out.println(DebuggerCommand.getCommand(n));
            return DebuggerCommand.getCommand(n);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void writeReply(DebuggerReply debuggerReply) {
        try {
            os.write(debuggerReply.code);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void flush() {
        try {
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
