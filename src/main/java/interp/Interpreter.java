package interp;

import interp.exceptions.DivideByZeroError;
import interp.primitives.Primitives;
import interp.stack.StackPointer;
import interp.stack.ValueStack;
import interp.value.DoubleValue;
import interp.value.ObjectValue;
import interp.value.StringValue;
import interp.value.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static interp.value.Value.booleanValue;


class ClosInfoValue implements Value {
    private final int arity;
    private final int delta;

    public ClosInfoValue(int arity, int delta) {
        this.arity = arity;
        this.delta = delta;
    }
}

class InfixOffsetValue implements Value {
    private final int size;

    public InfixOffsetValue(int size) {
        this.size = size;
    }
}

class Code {
    final int[] code;

    public Code(byte[] codeBytes) {
        assert codeBytes.length % 4 == 0;
        int[] code = new int[codeBytes.length / 4];
        for (int i = 0; i < codeBytes.length; i += 4) {
            int ch4 = 0xFF & codeBytes[i];
            int ch3 = 0xFF & codeBytes[i + 1];
            int ch2 = 0xFF & codeBytes[i + 2];
            int ch1 = 0xFF & codeBytes[i + 3];
            code[i / 4] = (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
        }
        this.code = code;
    }
}


public class Interpreter {
    private static final Logger logger = Logger.getLogger(Interpreter.class.getName());

    static {
        logger.setLevel(Level.FINE);
    }

    public static final Value valUnit = new LongValue(0);
    public static final LongValue valFalse = new LongValue(0);
    public static final LongValue valTrue = new LongValue(1);
    private final ObjectValue globalData;
    private final Primitives primitives;
    private final CamlState camlState = new CamlState();
    private boolean somethingToDo;

    public boolean getSomethingToDo() {
        return somethingToDo;
    }

    public void setSomethingToDo(boolean somethingToDo) {
        this.somethingToDo = somethingToDo;
    }

    public static enum Instructions {
        ACC0, ACC1, ACC2, ACC3, ACC4, ACC5, ACC6, ACC7,
        ACC, PUSH,
        PUSHACC0, PUSHACC1, PUSHACC2, PUSHACC3,
        PUSHACC4, PUSHACC5, PUSHACC6, PUSHACC7,
        PUSHACC, POP, ASSIGN,
        ENVACC1, ENVACC2, ENVACC3, ENVACC4, ENVACC,
        PUSHENVACC1, PUSHENVACC2, PUSHENVACC3, PUSHENVACC4, PUSHENVACC,
        PUSH_RETADDR, APPLY, APPLY1, APPLY2, APPLY3,
        APPTERM, APPTERM1, APPTERM2, APPTERM3,
        RETURN, RESTART, GRAB,
        CLOSURE, CLOSUREREC,
        OFFSETCLOSUREM2, OFFSETCLOSURE0, OFFSETCLOSURE2, OFFSETCLOSURE,
        PUSHOFFSETCLOSUREM2, PUSHOFFSETCLOSURE0,
        PUSHOFFSETCLOSURE2, PUSHOFFSETCLOSURE,
        GETGLOBAL, PUSHGETGLOBAL, GETGLOBALFIELD, PUSHGETGLOBALFIELD, SETGLOBAL,
        ATOM0, ATOM, PUSHATOM0, PUSHATOM,
        MAKEBLOCK, MAKEBLOCK1, MAKEBLOCK2, MAKEBLOCK3, MAKEFLOATBLOCK,
        GETFIELD0, GETFIELD1, GETFIELD2, GETFIELD3, GETFIELD, GETFLOATFIELD,
        SETFIELD0, SETFIELD1, SETFIELD2, SETFIELD3, SETFIELD, SETFLOATFIELD,
        VECTLENGTH, GETVECTITEM, SETVECTITEM,
        GETBYTESCHAR, SETBYTESCHAR,
        BRANCH, BRANCHIF, BRANCHIFNOT, SWITCH, BOOLNOT,
        PUSHTRAP, POPTRAP, RAISE,
        CHECK_SIGNALS,
        C_CALL1, C_CALL2, C_CALL3, C_CALL4, C_CALL5, C_CALLN,
        CONST0, CONST1, CONST2, CONST3, CONSTINT,
        PUSHCONST0, PUSHCONST1, PUSHCONST2, PUSHCONST3, PUSHCONSTINT,
        NEGINT, ADDINT, SUBINT, MULINT, DIVINT, MODINT,
        ANDINT, ORINT, XORINT, LSLINT, LSRINT, ASRINT,
        EQ, NEQ, LTINT, LEINT, GTINT, GEINT,
        OFFSETINT, OFFSETREF, ISINT,
        GETMETHOD,
        BEQ, BNEQ, BLTINT, BLEINT, BGTINT, BGEINT,
        ULTINT, UGEINT,
        BULTINT, BUGEINT,
        GETPUBMET, GETDYNMET,
        STOP,
        EVENT, BREAK,
        RERAISE, RAISE_NOTRACE,
        GETSTRINGCHAR,
        FIRST_UNIMPLEMENTED_OP
    }

    public Interpreter(ObjectValue globalData, Primitives primitives) {

        this.globalData = globalData;
        this.primitives = primitives;
    }

    public Value interpret(byte[] codeBytes) {
        List<Instructions> instructionTrace = new ArrayList<>();
        ValueStack stack = new ValueStack();
        Value accu = null;
        int extraArgs = 0;

        Code code = new Code(codeBytes);
        CodePointer pc = new CodePointer(code, 0);

        Instructions[] instructions = Instructions.values();

        Value env = new Atom(0);

        while (true) {
            Instructions currInstr = instructions[pc.get()];
            pc = pc.inc();
            System.out.println(currInstr);
            instructionTrace.add(currInstr);
            switch (currInstr) {
                case ACC0:
                    accu = stack.get(0);
                    continue;
                case ACC1:
                    accu = stack.get(1);
                    continue;
                case ACC2:
                    accu = stack.get(2);
                    continue;
                case ACC3:
                    accu = stack.get(3);
                    continue;
                case ACC4:
                    accu = stack.get(4);
                    continue;
                case ACC5:
                    accu = stack.get(5);
                    continue;
                case ACC6:
                    accu = stack.get(6);
                    continue;
                case ACC7:
                    accu = stack.get(7);
                    continue;
                case PUSH:
                case PUSHACC0:
                    stack.push(accu);
                    continue;
                case PUSHACC1:
                    stack.push(accu);
                    accu = stack.get(1);
                    continue;
                case PUSHACC2:
                    stack.push(accu);
                    accu = stack.get(2);
                    continue;
                case PUSHACC3:
                    stack.push(accu);
                    accu = stack.get(3);
                    continue;
                case PUSHACC4:
                    stack.push(accu);
                    accu = stack.get(4);
                    continue;
                case PUSHACC5:
                    stack.push(accu);
                    accu = stack.get(5);
                    continue;
                case PUSHACC6:
                    stack.push(accu);
                    accu = stack.get(6);
                    continue;
                case PUSHACC7:
                    stack.push(accu);
                    accu = stack.get(7);
                    continue;
                case PUSHACC:
                    stack.push(accu);
                    /* Fallthrough */
                case ACC:
                    accu = stack.get(pc.get());
                    pc = pc.inc();
                    continue;
                case POP:
                    stack.popNIgnore(pc.get());
                    pc = pc.inc();
                    continue;
                case ASSIGN:
                    stack.set(pc.get(), accu);
                    accu = valUnit;
                    continue;
                    /* Access in heap-allocated environment */
                case ENVACC1:
                    accu = getField(env, 1);
                    continue;
                case ENVACC2:
                    accu = getField(env, 2);
                    continue;
                case ENVACC3:
                    accu = getField(env, 3);
                    continue;
                case ENVACC4:
                    accu = getField(env, 4);
                    continue;
                case PUSHENVACC1:
                    stack.push(accu);
                    accu = getField(env, 1);
                    continue;
                case PUSHENVACC2:
                    stack.push(accu);
                    accu = getField(env, 2);
                    continue;
                case PUSHENVACC3:
                    stack.push(accu);
                    accu = getField(env, 3);
                    continue;
                case PUSHENVACC4:
                    stack.push(accu);
                    accu = getField(env, 4);
                    continue;
                case PUSHENVACC:
                    stack.push(accu);
                case ENVACC:
                    accu = getField(env, pc.get());
                    pc = pc.inc();
                    continue;
                    /* Function application */
                case PUSH_RETADDR:
                    stack.push(pc.incN(pc.get()));
                    stack.push(new LongValue(extraArgs));
                    stack.push(env);
                    pc = pc.inc();
                    continue;
                case APPLY: {
                    extraArgs = pc.get() - 1;
                    pc = (CodePointer) ((ObjectValue) accu).getField(0);
                    env = accu;
                    continue;
                }
                case APPLY1: {
                    Value arg1 = stack.pop();
                    stack.push(new LongValue(extraArgs));
                    stack.push(env);
                    stack.push(pc);
                    stack.push(arg1);
                    pc = (CodePointer) ((ObjectValue) accu).getField(0);
                    env = accu; //What's happening here? why is the env the programme counter?
                    extraArgs = 0;
                    checkStacks();
                    continue;
                }
                case APPLY2: {
                    Value arg1 = stack.pop();
                    Value arg2 = stack.pop();
                    stack.push(new LongValue(extraArgs));
                    stack.push(env);
                    stack.push(pc);
                    stack.push(arg2);
                    stack.push(arg1);
                    pc = (CodePointer) ((ObjectValue) accu).getField(0);
                    env = accu; //What's happening here? why is the env the programme counter?
                    extraArgs = 1;
                    checkStacks();
                    continue;
                }
                case APPLY3: {
                    Value arg1 = stack.pop();
                    Value arg2 = stack.pop();
                    Value arg3 = stack.pop();
                    stack.push(new LongValue(extraArgs));
                    stack.push(env);
                    stack.push(pc);
                    stack.push(arg3);
                    stack.push(arg2);
                    stack.push(arg1);


                    pc = (CodePointer) ((ObjectValue) accu).getField(0);
                    env = accu; //What's happening here? why is the env the programme counter?
                    extraArgs = 2;
                    checkStacks();
                    continue;
                }

                case APPTERM: {
                    int nargs = pc.get();
                    pc = pc.inc();
                    int slotSize = pc.get();
                    List<Value> args = new ArrayList<>();
                    /* Slide the nargs bottom words of the current frame to the top
         of the frame, and discard the remainder of the frame */
                    for (int i = nargs - 1; i >= 0; i--) {
                        args.add(stack.get(i));
                    }
                    stack.popNIgnore(slotSize);
                    for (Value arg : args) {
                        stack.push(arg);
                    }
                    pc = (CodePointer) ((ObjectValue) accu).getField(0);
                    env = accu;
                    extraArgs += nargs - 1;
                    checkStacks();
                    continue;
                }
                case APPTERM1: {
                    Value arg1 = stack.get(0);
                    stack.popNIgnore(pc.get());
                    stack.push(arg1);
                    pc = (CodePointer) ((ObjectValue) accu).getField(0);
                    env = accu;
                    checkStacks();
                    continue;
                }
                case APPTERM2: {
                    Value arg1 = stack.get(0);
                    Value arg2 = stack.get(1);
                    stack.popNIgnore(pc.get());
                    stack.push(arg2);
                    stack.push(arg1);
                    pc = (CodePointer) ((ObjectValue) accu).getField(0);
                    env = accu;
                    extraArgs += 1;
                    checkStacks();
                    continue;
                }
                case APPTERM3: {
                    Value arg1 = stack.get(0);
                    Value arg2 = stack.get(1);
                    Value arg3 = stack.get(2);
                    stack.popNIgnore(pc.get());
                    stack.push(arg3);
                    stack.push(arg2);
                    stack.push(arg1);
                    pc = (CodePointer) ((ObjectValue) accu).getField(0);
                    env = accu;
                    extraArgs += 2;
                    checkStacks();
                    continue;
                }

                case RETURN: {
                    stack.popNIgnore(pc.get());
                    if (extraArgs > 0) {
                        extraArgs -= 1;
                        pc = (CodePointer) accu;
                        env = accu;
                    } else {
                        pc = (CodePointer) (stack.get(0));
                        env = stack.get(1);
                        extraArgs = (int) ((LongValue) stack.get(2)).getValue();
                        stack.popNIgnore(3);
                    }
                    continue;
                }

                case RESTART: {
                    ObjectValue obj = (ObjectValue) env;
                    int numArgs = obj.getSize() - 3;
                    for (int i = 0; i < numArgs; i++) {
                        stack.push(getField(env, obj.getSize() - i - 1));
                    }
                    env = getField(env, 2);
                    extraArgs += numArgs;
                    continue;
                }

                case GRAB: {
                    int required = pc.get();
                    pc = pc.inc();
                    if (extraArgs >= required) {
                        extraArgs -= required;
                    } else {
                        int numArgs = 1 + extraArgs;
                        ObjectValue o = new ObjectValue(Intern.Closure_tag, numArgs + 3);
                        accu = o;
                        o.setField(2, env);
                        for (int i = 0; i < numArgs; i++) {
                            o.setField(i + 3, stack.get(i));
                        }
                        o.setField(0, pc.incN(-3));
                        o.setField(1, new ClosInfoValue(0, 2));
                        stack.popNIgnore(numArgs);
                        pc = (CodePointer) stack.get(0);
                        env = stack.get(1);
                        extraArgs = (int) ((LongValue) stack.get(2)).getValue();
                        stack.popNIgnore(3);
                    }
                    continue;
                }

                case CLOSURE: {
                    int nVars = pc.get();
                    pc = pc.inc();

                    if (nVars > 0) {
                        stack.push(accu);
                    }

                    ObjectValue o = new ObjectValue(Intern.Closure_tag, 2 + nVars);
                    accu = o;
                    for (int i = 0; i < nVars; i++) {
                        o.setField(i + 2, stack.get(i));
                    }

                    o.setField(0, pc.incN(pc.get()));
                    o.setField(1, new ClosInfoValue(0, 2));

                    pc = pc.inc();
                    stack.popNIgnore(nVars);
                    continue;
                }

                case CLOSUREREC: {
                    int nFuncs = pc.get();
                    pc = pc.inc();
                    int nVars = pc.get();
                    pc = pc.inc();
                    int envOffset = nFuncs * 3 - 1;
                    int blkSize = envOffset + nVars;
                    if (nVars > 0) {
                        stack.push(accu);
                    }
                    ObjectValue o = new ObjectValue(Intern.Closure_tag, blkSize);
                    accu = o;
                    for (int i = 0; i < nVars; i++) {
                        o.setField(i + envOffset, stack.get(i));
                    }
                    stack.popNIgnore(nVars);
                    stack.push(accu);
                    o.setField(0, pc.incN(pc.get()));
                    o.setField(1, new ClosInfoValue(0, envOffset));
                    for (int i = 1, j = 2; i < nFuncs; i++, j++) {
                        o.setField(j, new InfixOffsetValue(i * 3));
                        j += 1;
                        stack.push(o.atFieldId(j));
                        o.setField(j, pc.incN(pc.incN(i).get()));
                        j += 1;
                        envOffset -= 3;
                        o.setField(j, new ClosInfoValue(0, envOffset));
                    }
                    pc = pc.incN(nFuncs);
                    continue;
                }

                case PUSHOFFSETCLOSURE:
                    stack.push(accu);
                case OFFSETCLOSURE: {
                    int offset = pc.get();
                    pc = pc.inc();
                    accu = ((StackPointer) env).incN(-1 * offset);
                    pc = pc.inc();
                    continue;
                }

                case PUSHOFFSETCLOSUREM2:
                    stack.push(accu);
                case OFFSETCLOSUREM2: {
                    accu = ((StackPointer) env).incN(2);
                    continue;
                }

                case PUSHOFFSETCLOSURE0:
                    stack.push(accu);
                case OFFSETCLOSURE0:
                    accu = env;
                    continue;

                case PUSHOFFSETCLOSURE2:
                    stack.push(accu);
                case OFFSETCLOSURE2:
                    accu = ((StackPointer) env).incN(-2);
                    continue;


                    /* Access to global variables */


                case PUSHGETGLOBAL:
                    stack.push(accu);
                    /* Fallthrough */
                case GETGLOBAL:
                    accu = globalData.getField(pc.get());
                    pc = pc.inc();
                    continue;

                case PUSHGETGLOBALFIELD:
                    stack.push(accu);
                    /* Fallthrough */
                case GETGLOBALFIELD: {
                    accu = globalData.getField(pc.get());
                    pc = pc.inc();
                    accu = ((ObjectValue) accu).getField(pc.get());
                    pc = pc.inc();
                    continue;
                }

                case SETGLOBAL:
                    globalData.setField(pc.get(), accu);
                    pc = pc.inc();
                    accu = valUnit;
                    continue;

                    /* Allocation of blocks */

                case PUSHATOM0:
                    stack.push(accu);
                    /* Fallthrough */

                case ATOM0:
                    accu = new Atom(0);
                    continue;

                case PUSHATOM:
                    stack.push(accu);
                    /* Fallthrough */
                case ATOM:
                    accu = new Atom(pc.get());
                    pc = pc.inc();
                    continue;

                case MAKEBLOCK: {
                    int woSize = pc.get();
                    pc = pc.inc();
                    int tag = pc.get();
                    pc = pc.inc();
                    ObjectValue o = new ObjectValue(tag, woSize);
                    o.setField(0, accu);
                    for (int i = 1; i < woSize; i++) {
                        o.setField(i, stack.pop());
                    }
//                    stack.popNIgnore(woSize - 1);
                    accu = o;
                    continue;
                }

                case MAKEBLOCK1: {
                    int tag = pc.get();
                    pc = pc.inc();
                    ObjectValue o = new ObjectValue(tag, 1);
                    o.setField(0, accu);
                    accu = o;
                    continue;
                }
                case MAKEBLOCK2: {
                    int tag = pc.get();
                    pc = pc.inc();
                    ObjectValue o = new ObjectValue(tag, 2);
                    o.setField(0, accu);
                    o.setField(1, stack.get(0));
                    stack.popNIgnore(1);
                    accu = o;
                    continue;
                }

                case MAKEBLOCK3: {
                    int tag = pc.get();
                    pc = pc.inc();
                    ObjectValue o = new ObjectValue(tag, 3);
                    o.setField(0, accu);
                    o.setField(1, stack.get(0));
                    o.setField(2, stack.get(1));
                    stack.popNIgnore(2);
                    accu = o;
                    continue;
                }

                case MAKEFLOATBLOCK: {
                    int size = pc.get();
                    pc = pc.inc();
                    double[] arr = new double[size];
                    for (int i = 0; i < size; i++) {
                        arr[i] = ((DoubleValue) stack.get(i)).getValue();
                    }
                    stack.popNIgnore(size);
                    accu = new DoubleArray(arr);
                }

                /* Access to components of blocks */

                case GETFIELD0:
                    accu = ((ObjectValue) accu).getField(0);
                    continue;
                case GETFIELD1:
                    accu = ((ObjectValue) accu).getField(1);
                    continue;
                case GETFIELD2:
                    accu = ((ObjectValue) accu).getField(2);
                    continue;
                case GETFIELD3:
                    accu = ((ObjectValue) accu).getField(3);
                    continue;
                case GETFIELD:
                    accu = ((ObjectValue) accu).getField(pc.get());
                    pc = pc.inc();
                    continue;

                case GETFLOATFIELD: {
                    double d = ((DoubleArray) accu).getField(pc.get());
                    pc = pc.inc();
                    accu = new DoubleValue(d);
                    continue;
                }

                case SETFIELD0:
                    ((ObjectValue) accu).setField(0, stack.get(0));
                    stack.popNIgnore(1);
                    accu = valUnit;
                    continue;
                case SETFIELD1:
                    ((ObjectValue) accu).setField(1, stack.get(0));
                    stack.popNIgnore(1);
                    accu = valUnit;
                case SETFIELD2:
                    ((ObjectValue) accu).setField(2, stack.get(0));
                    stack.popNIgnore(1);
                    accu = valUnit;
                case SETFIELD3:
                    ((ObjectValue) accu).setField(3, stack.get(0));
                    stack.popNIgnore(1);
                    accu = valUnit;
                case SETFIELD:
                    ((ObjectValue) accu).setField(pc.get(), stack.get(0));
                    stack.popNIgnore(1);
                    pc = pc.inc();
                    continue;
                case SETFLOATFIELD:
                    ((DoubleArray) accu).setField(pc.get(), ((DoubleValue) stack.get(0)).getValue());
                    pc = pc.inc();
                    stack.popNIgnore(1);
                    continue;

                    /* Array operations */

                case VECTLENGTH: {
      /* Todo: when FLAT_FLOAT_ARRAY is false, this instruction should
         be split into VECTLENGTH and FLOATVECTLENGTH because we know
         statically which one it is. */
                    int size;
                    if (accu instanceof ObjectValue) {
                        size = ((ObjectValue) accu).getSize();
                    } else {
                        size = ((DoubleArray) accu).getSize();
                    }
                    accu = new DoubleValue(size);
                    continue;
                }
                case GETVECTITEM:
                    accu = ((ObjectValue) accu).getField((int) ((LongValue) stack.get(0)).getValue());
                    stack.popNIgnore(1);
                    continue;
                case SETVECTITEM:
                    ((ObjectValue) accu).setField((int) ((LongValue) stack.get(0)).getValue(), stack.get(1));
                    accu = valUnit;
                    stack.popNIgnore(2);
                    continue;

                    /* Bytes/String operations */
                case GETSTRINGCHAR:
                case GETBYTESCHAR:
                    accu = new LongValue(((StringValue) accu).get((int) ((LongValue) stack.get(0)).getValue()));
                    stack.popNIgnore(1);
                    continue;
                case SETBYTESCHAR:
                    ((StringValue) accu).set((int) ((LongValue) stack.get(0)).getValue(), (int) ((LongValue) stack.get(0)).getValue());
                    stack.popNIgnore(2);
                    accu = valUnit;
                    continue;

                    /* Branches and conditional branches */

                case BRANCH:
                    pc = pc.incN(pc.get());
                    continue;
                case BRANCHIF:
                    if (!accu.equals(valFalse)) {
                        pc = pc.incN(pc.get());
                    } else {
                        pc = pc.inc();
                    }
                    continue;
                case BRANCHIFNOT:
                    if (accu.equals(valFalse)) {
                        pc = pc.incN(pc.get());
                    } else {
                        pc = pc.inc();
                    }
                    continue;
                case SWITCH: {
                    int sizes = pc.get();
                    pc = pc.inc();
                    if (accu instanceof ObjectValue) {
                        int index = ((ObjectValue) accu).getTag();
                        //        CAMLassert ((uintnat) index < (sizes >> 16));
                        pc = pc.incN(pc.getN((sizes & 0xFFFF) + index));
                    } else {
                        int index = (int) ((LongValue) accu).getValue();
                        pc = pc.incN(pc.getN(index));
                    }
                    continue;
                }
                case BOOLNOT:
                    accu = valNot(accu);
                    continue;

                    /* Exceptions */
                case PUSHTRAP: {
                    stack.push(new LongValue(extraArgs));
                    stack.push(env);
                    stack.push(camlState.getTrapSp());
                    stack.push(pc.incN(pc.get()));
                    pc = pc.inc();
                    camlState.setTrapSp(stack.pointer());
                    continue;
                }

                case POPTRAP: {
                    if (getSomethingToDo()) {
        /* We must check here so that if a signal is pending and its
           handler triggers an exception, the exception is trapped
           by the current try...with, not the enclosing one. */
                        pc = pc.dec(); /* restart the POPTRAP after processing the signal */
                        processActions();
                    } else {
                        camlState.setTrapSp((StackPointer) stack.get(1));
                        stack.popNIgnore(4);
                    }
                    continue;
                }

                case RAISE_NOTRACE: {
                    checkTrapBarrier();
                    raiseNoTrace();
                    continue;
                }

                case RERAISE: {
                    checkTrapBarrier();
                    if (camlState.getBackTraceActive()) {
                        stack.push(pc.dec());
                        stashBacktrace(accu, stack.pointer(), 1);
                    }
                    raiseNoTrace();
                    continue;
                }

                case RAISE: {
                    checkTrapBarrier();
                    if (camlState.getBackTraceActive()) {
                        stack.push(pc.dec());
                        stashBacktrace(accu, stack.pointer(), 0);
                    }
                    raiseNoTrace();
                    continue;
                }

                /* Signal handling */

                case CHECK_SIGNALS: {   /* accu not preserved */
                    if (getSomethingToDo()) {
                        processActions();
                    }
                    continue;
                }

                /* Calling C functions */

                case C_CALL1: {
                    //Setup_for_c_call
                    stack.push(pc);
                    stack.push(env);
                    camlState.setExternSp(stack.pointer());

                    accu = primitives.get(pc.get()).call(new Value[]{accu});

                    //Restore_after_c_call
                    stack.reset(camlState.getExternSp());
                    env = stack.get(0);
                    stack.popNIgnore(2);
                    pc = pc.inc();
                    continue;
                }

                case C_CALL2: {
                    //Setup_for_c_call
                    stack.push(pc);
                    stack.push(env);
                    camlState.setExternSp(stack.pointer());

                    accu = primitives.get(pc.get()).call(new Value[]{accu, stack.get(2)});

                    //Restore_after_c_call
                    stack.reset(camlState.getExternSp());
                    env = stack.get(0);
                    stack.popNIgnore(3);
                    pc = pc.inc();
                    continue;
                }

                case C_CALL3: {
                    //Setup_for_c_call
                    stack.push(pc);
                    stack.push(env);
                    camlState.setExternSp(stack.pointer());

                    accu = primitives.get(pc.get()).call(new Value[]{accu, stack.get(2), stack.get(3)});

                    //Restore_after_c_call
                    stack.reset(camlState.getExternSp());
                    env = stack.get(0);
                    stack.popNIgnore(4);
                    pc = pc.inc();
                    continue;
                }

                case C_CALL4: {
                    //Setup_for_c_call
                    stack.push(pc);
                    stack.push(env);
                    camlState.setExternSp(stack.pointer());

                    accu = primitives.get(pc.get()).call(new Value[]{accu, stack.get(2), stack.get(3), stack.get(4)});

                    //Restore_after_c_call
                    stack.reset(camlState.getExternSp());
                    env = stack.get(0);
                    stack.popNIgnore(5);
                    pc = pc.inc();
                    continue;
                }

                case C_CALL5: {
                    //Setup_for_c_call
                    stack.push(pc);
                    stack.push(env);
                    camlState.setExternSp(stack.pointer());

                    accu = primitives.get(pc.get()).call(new Value[]{accu, stack.get(2), stack.get(3), stack.get(4), stack.get(5)});

                    //Restore_after_c_call
                    stack.reset(camlState.getExternSp());
                    env = stack.get(0);
                    stack.popNIgnore(6);
                    pc = pc.inc();
                    continue;
                }

                case C_CALLN: {
                    int nargs = pc.get();
                    pc = pc.inc();
                    //Setup_for_c_call
                    stack.push(pc);
                    stack.push(env);
                    camlState.setExternSp(stack.pointer());

                    Value[] args = new Value[nargs];
                    args[0] = accu;
                    for (int i = 0; i < nargs; i++) {
                        args[i + 1] = stack.get(2 + i);
                    }

                    accu = primitives.get(pc.get()).call(new Value[]{accu, stack.get(2), stack.get(3), stack.get(4), stack.get(5)});

                    //Restore_after_c_call
                    stack.reset(camlState.getExternSp());
                    env = stack.get(0);
                    stack.popNIgnore(2 + nargs - 1);
                    pc = pc.inc();
                    continue;
                }

                /* Integer constants */

                case CONST0:
                    accu = new LongValue(0);
                    continue;
                case CONST1:
                    accu = new LongValue(1);
                    continue;
                case CONST2:
                    accu = new LongValue(2);
                    continue;
                case CONST3:
                    accu = new LongValue(3);
                    continue;

                case PUSHCONST0:
                    stack.push(accu);
                    accu = new LongValue(0);
                    continue;
                case PUSHCONST1:
                    stack.push(accu);
                    accu = new LongValue(1);
                    continue;
                case PUSHCONST2:
                    stack.push(accu);
                    accu = new LongValue(2);
                    continue;
                case PUSHCONST3:
                    stack.push(accu);
                    accu = new LongValue(3);
                    continue;
                case PUSHCONSTINT:
                    stack.push(accu);
                    /* Fallthrough */
                case CONSTINT:
                    accu = pc.getLongValue();
                    pc = pc.inc();
                    continue;

                    /* Integer arithmetic */

                case NEGINT:
                    accu = ((LongValue) accu).negate();
                    continue;

                case ADDINT:
                    accu = ((LongValue) accu).add((LongValue) stack.pop());
                    continue;

                case SUBINT:
                    accu = ((LongValue) accu).sub((LongValue) stack.pop());
                    continue;

                case MULINT:
                    accu = ((LongValue) accu).mul((LongValue) stack.pop());
                    continue;

                case DIVINT: {
                    try {
                        accu = ((LongValue) accu).div((LongValue) stack.pop());
                    } catch (DivideByZeroError e) {
                        setupForCCall();
                        raiseZeroDivide();
                    }
                    continue;
                }
                case MODINT: {
                    try {
                        accu = ((LongValue) accu).mod((LongValue) stack.pop());
                    } catch (DivideByZeroError e) {
                        setupForCCall();
                        raiseZeroDivide();
                    }
                    continue;
                }
                case ANDINT:
                    accu = ((LongValue) accu).and((LongValue) stack.pop());
                    continue;
                case ORINT:
                    accu = ((LongValue) accu).or((LongValue) stack.pop());
                    continue;

                case XORINT:
                    accu = ((LongValue) accu).xor((LongValue) stack.pop());
                    continue;
                case LSLINT:
                    accu = ((LongValue) accu).lsl((LongValue) stack.pop());
                    continue;
                    //?? Should we be treating unsigned and signed differently?
                case LSRINT:
                    accu = ((LongValue) accu).ulsr((LongValue) stack.pop());
                    continue;
                case ASRINT:
                    accu = ((LongValue) accu).lsr((LongValue) stack.pop());
                    continue;

                case EQ:
                    accu = ((LongValue) accu).eq((LongValue) stack.pop());
                    continue;
                case NEQ:
                    accu = ((LongValue) accu).neq((LongValue) stack.pop());
                    continue;
                case LTINT:
                    accu = ((LongValue) accu).lt((LongValue) stack.pop());
                    continue;
                case LEINT:
                    accu = ((LongValue) accu).le((LongValue) stack.pop());
                    continue;
                case GTINT:
                    accu = ((LongValue) accu).gt((LongValue) stack.pop());
                    continue;
                case GEINT:
                    accu = ((LongValue) accu).ge((LongValue) stack.pop());
                    continue;
                case ULTINT:
                    accu = ((LongValue) accu).ult((LongValue) stack.pop());
                    continue;
                case UGEINT:
                    accu = ((LongValue) accu).uge((LongValue) stack.pop());
                    continue;

                case BEQ: {
                    if (pc.get() == ((LongValue) accu).getValue()) {
                        pc = pc.inc();
                        pc = pc.incN(pc.get());
                    } else {
                        pc = pc.incN(2);
                    }
                    continue;
                }
                case BNEQ: {
                    if (pc.get() != ((LongValue) accu).getValue()) {
                        pc = pc.inc();
                        pc = pc.incN(pc.get());
                    } else {
                        pc = pc.incN(2);
                    }
                    continue;
                }
                case BLTINT: {
                    if (pc.get() < ((LongValue) accu).getValue()) {
                        pc = pc.inc();
                        pc = pc.incN(pc.get());
                    } else {
                        pc = pc.incN(2);
                    }
                    continue;
                }
                case BLEINT: {
                    if (pc.get() <= ((LongValue) accu).getValue()) {
                        pc = pc.inc();
                        pc = pc.incN(pc.get());
                    } else {
                        pc = pc.incN(2);
                    }
                    continue;
                }
                case BGTINT: {
                    if (pc.get() > ((LongValue) accu).getValue()) {
                        pc = pc.inc();
                        pc = pc.incN(pc.get());
                    } else {
                        pc = pc.incN(2);
                    }
                    continue;
                }
                case BGEINT: {
                    if (pc.get() >= ((LongValue) accu).getValue()) {
                        pc = pc.inc();
                        pc = pc.incN(pc.get());
                    } else {
                        pc = pc.incN(2);
                    }
                    continue;
                }
                case BULTINT: {
                    if (Long.compareUnsigned(pc.get(), ((LongValue) accu).getValue()) < 0) {
                        pc = pc.inc();
                        pc = pc.incN(pc.get());
                    } else {
                        pc = pc.incN(2);
                    }
                    continue;
                }
                case BUGEINT: {
                    if (Long.compareUnsigned(pc.get(), ((LongValue) accu).getValue()) >= 0) {
                        pc = pc.inc();
                        pc = pc.incN(pc.get());
                    } else {
                        pc = pc.incN(2);
                    }
                    continue;
                }
                case OFFSETINT:
                    accu = ((LongValue)accu).add(pc.getLongValue());
                    pc = pc.inc();
                    continue;
//    Instruct(OFFSETREF):
//      Field(accu, 0) += *pc << 1;
//      accu = Val_unit;
//      pc++;
//      Next;
                case ISINT: {
                    accu = booleanValue(accu instanceof LongValue);
                    continue;
                }
                case STOP:
                    return accu;
                default: {
                    throw new RuntimeException(String.format("Instruction %s not implemented", currInstr));
                }
            }
        }
    }

    private void raiseZeroDivide() {
        throw new RuntimeException("Not implemented yet");
    }

    private void setupForCCall() {
        //Not implemented yet
    }

    private void stashBacktrace(Value accu, StackPointer pointer, int i) {
        // Not implemented yet
    }

    private void raiseNoTrace() {
        throw new RuntimeException("Not implemented yet");

//        if ((char *) Caml_state->trapsp
//          >= (char *) Caml_state->stack_high - initial_sp_offset) {
//        Caml_state->external_raise = initial_external_raise;
//        Caml_state->extern_sp = (value *) ((char *) Caml_state->stack_high
//                                    - initial_sp_offset);
//        caml_callback_depth--;
//        return Make_exception_result(accu);
//      }
//      sp = Caml_state->trapsp;
//      pc = Trap_pc(sp);
//      Caml_state->trapsp = Trap_link(sp);
//      env = sp[2];
//      extra_args = Long_val(sp[3]);
//      sp += 4;
    }

    private void checkTrapBarrier() {
        //Not implemented yet
    }

    private void processActions() {
        throw new RuntimeException("Not implemented yet");
//              Setup_for_event;
//      caml_process_pending_actions();
//      Restore_after_event;
//      Next;
    }


    private Value valNot(Value accu) {
        //This might not cover all the cases
        LongValue v = (LongValue) accu;
        if (v.getValue() == 1) {
            return valFalse;
        } else {
            return valTrue;
        }
    }

    private void checkStacks() {
        //Does nothing.
        //Might need to handle signal handling code here at some point.
//        if (sp < Caml_state->stack_threshold) {
//            Caml_state->extern_sp = sp;
//            caml_realloc_stack(Stack_threshold / sizeof(value));
//            sp = Caml_state->extern_sp;
//        }
    }


    private void debug(String message, Object... inserts) {
        if (logger.getLevel() == Level.FINE) {
            logger.fine(String.format(message, inserts));
        }
    }

    private Value getField(Value env, int i) {
        return ((ObjectValue) env).getField(i);
    }
}
