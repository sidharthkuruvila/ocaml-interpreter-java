package interp.debugger;

import interp.*;
import interp.stack.ValueStack;
import interp.value.ObjectValue;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DebuggingTest {

    @Test
    void testByteBuffer(){
        ByteBuffer buf = ByteBuffer.allocate(4);
        byte[] bytes = buf.array();
        bytes[0] = 1;
        int n = buf.getInt();
        assertEquals(16777216, n);
    }

    @Test
    void testDebugging() throws IOException {

        int instr = Interpreter.Instructions.STOP.ordinal();
        ByteBuffer buf = ByteBuffer.allocate(16);

        for(int i = 0; i < 4; i++) {
            buf.putInt(i<<2, instr);
        }
        CodeFragment codeFragment = new CodeFragment(buf.array());
        CodeFragmentTable codeFragmentTable = new CodeFragmentTable();
        codeFragmentTable.add(codeFragment);
        Code code = codeFragment.getCode();
        CodePointer pc = new CodePointer(code, 3);

        ObjectValue obj = new ObjectValue(ValueTag.Object_tag, 4);
        obj.setField(0, LongValue.wrap(21));
        obj.setField(1, LongValue.wrap(22));
        obj.setField(2, LongValue.wrap(23));
        obj.setField(3, LongValue.wrap(24));



        ObjectValue env = new ObjectValue(0, 4);
        env.setField(0, LongValue.wrap(1));
        env.setField(1, obj);
        env.setField(2, LongValue.wrap(3));
        env.setField(3, LongValue.wrap(4));

        ValueStack stack = new ValueStack();
        stack.push(LongValue.wrap(11));
        stack.push(LongValue.wrap(10));
        stack.push(LongValue.wrap(1));
        stack.push(env);
        stack.push(pc);


        ObjectValue globalData = new ObjectValue(0, 4);
        globalData.setField(0, LongValue.wrap(5));
        globalData.setField(1, LongValue.wrap(6));
        globalData.setField(2, LongValue.wrap(7));
        globalData.setField(3, LongValue.wrap(8));

        List<Executable.DebugEvent> debugEvents = new ArrayList<>();

        InterpreterContext context = new InterpreterContext(globalData, stack, debugEvents);

        ByteArrayOutputStream bis = new ByteArrayOutputStream();
        DataOutputStream dis = new DataOutputStream(bis);
        dis.writeByte(DebuggerCommand.REQ_SET_BREAKPOINT.code);
        dis.writeInt(0);
        dis.writeInt(3);
        dis.writeByte(DebuggerCommand.REQ_SET_EVENT.code);
        dis.writeInt(0);
        dis.writeInt(2);
        dis.writeByte(DebuggerCommand.REQ_SET_BREAKPOINT.code);
        dis.writeInt(0);
        dis.writeInt(1);
        dis.writeByte(DebuggerCommand.REQ_RESET_INSTR.code);
        dis.writeInt(0);
        dis.writeInt(1);
        dis.writeByte(DebuggerCommand.REQ_INITIAL_FRAME.code);
        dis.writeByte(DebuggerCommand.REQ_GET_FRAME.code);
        dis.writeByte(DebuggerCommand.REQ_SET_FRAME.code);
        dis.writeInt(1);
        dis.writeByte(DebuggerCommand.REQ_INITIAL_FRAME.code);
        dis.writeByte(DebuggerCommand.REQ_UP_FRAME.code);
        dis.writeInt(3);
        dis.writeByte(DebuggerCommand.REQ_SET_TRAP_BARRIER.code);
        dis.writeInt(0);
        dis.writeByte(DebuggerCommand.REQ_GET_LOCAL.code);
        dis.writeInt(0);
        dis.writeByte(DebuggerCommand.REQ_GET_ENVIRONMENT.code);
        dis.writeInt(1);
        dis.writeByte(DebuggerCommand.REQ_GET_ACCU.code);
        dis.writeByte(DebuggerCommand.REQ_GET_GLOBAL.code);
        dis.writeInt(1);
        dis.writeByte(DebuggerCommand.REQ_GO.code);
        dis.writeInt(10);

//        dis.writeByte(DebuggerCommand.REQ_GET_HEADER.code);
//        dis.writeLong(3);
        dis.writeByte(DebuggerCommand.REQ_INITIAL_FRAME.code);
        dis.writeByte(DebuggerCommand.REQ_GET_ENVIRONMENT.code);
        dis.writeInt(1);
        dis.writeByte(DebuggerCommand.REQ_GET_FIELD.code);
        dis.writeLong(6);
        dis.writeInt(3);
        dis.writeByte(DebuggerCommand.REQ_MARSHAL_OBJ.code);
        dis.writeLong(6);
        dis.writeByte(DebuggerCommand.REQ_GO.code);
        dis.writeInt(5);




//        assertEquals(1, stack.size());



        InputStream is = new ByteArrayInputStream(bis.toByteArray());
        ByteArrayOutputStream os = new ByteArrayOutputStream();


        CamlState camlState = new CamlState(null);

        Debugging debugging = new Debugging(camlState, context, codeFragmentTable, is, os);

        debugging.debugBreakpoint();
        System.out.println("done once");
        debugging.debugBreakpoint();
        System.out.println("done");
        HexPrinter.printBytes(os.toByteArray());

    }
}
