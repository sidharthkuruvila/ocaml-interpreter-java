package interp.debugger;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum DebuggerCommand {
    REQ_SET_EVENT('e'),          /* uint32_t pos */
    /* Set an event on the instruction at position pos */
    REQ_SET_BREAKPOINT('B'),     /* uint32_t pos, (char k) */
    /* Set a breakpoint at position pos */
    /* In profiling mode, the breakpoint kind is set to k */
    REQ_RESET_INSTR('i'),        /* uint32_t pos */
    /* Clear an event or breapoint at position pos, restores initial instr. */
    REQ_CHECKPOINT('c'),         /* no args */
    /* Checkpoint the runtime system by forking a child process.
       Reply is pid of child process or -1 if checkpoint failed. */
    REQ_GO('g'),                 /* uint32_t n */
    /* Run the program for n events.
       Reply is one of debugger_reply described below. */
    REQ_STOP('s'),               /* no args */
    /* Terminate the runtime system */
    REQ_WAIT('w'),               /* no args */
    /* Reap one dead child (a discarded checkpoint). */
    REQ_INITIAL_FRAME('0'),      /* no args */
    /* Set current frame to bottom frame (the one currently executing).
       Reply is stack offset and current pc. */
    REQ_GET_FRAME('f'),          /* no args */
    /* Return current frame location (stack offset + current pc). */
    REQ_SET_FRAME('S'),          /* uint32_t stack_offset */
    /* Set current frame to given stack offset. No reply. */
    REQ_UP_FRAME('U'),           /* uint32_t n */
    /* Move one frame up. Argument n is size of current frame (in words).
       Reply is stack offset and current pc, or -1 if top of stack reached. */
    REQ_SET_TRAP_BARRIER('b'),   /* uint32_t offset */
    /* Set the trap barrier at the given offset. */
    REQ_GET_LOCAL('L'),          /* uint32_t slot_number */
    /* Return the local variable at the given slot in the current frame.
       Reply is one value. */
    REQ_GET_ENVIRONMENT('E'),    /* uint32_t slot_number */
    /* Return the local variable at the given slot in the heap environment
       of the current frame. Reply is one value. */
    REQ_GET_GLOBAL('G'),         /* uint32_t global_number */
    /* Return the specified global variable. Reply is one value. */
    REQ_GET_ACCU('A'),           /* no args */
    /* Return the current contents of the accumulator. Reply is one value. */
    REQ_GET_HEADER('H'),         /* mlvalue v */
    /* As REQ_GET_OBJ, but sends only the header. */
    REQ_GET_FIELD('F'),          /* mlvalue v, uint32_t fieldnum */
    /* As REQ_GET_OBJ, but sends only one field. */
    REQ_MARSHAL_OBJ('M'),        /* mlvalue v */
    /* Send a copy of the data structure rooted at v, using the same
       format as [caml_output_value]. */
    REQ_GET_CLOSURE_CODE('C'),   /* mlvalue v */
    /* Send the code address of the given closure.
       Reply is one uint32_t. */
    REQ_SET_FORK_MODE('K');       /* uint32_t m */
    /* Set whether to follow the child (m=0) or the parent on fork. */


    int code;

    int getCode() {
        return code;
    }

    static Map<Integer, DebuggerCommand> commandMap = Arrays.stream(values())
            .collect(Collectors.toMap(DebuggerCommand::getCode, Function.identity()));


    public static DebuggerCommand getCommand(int cmd) {
        return commandMap.get(cmd);
    }

    DebuggerCommand(char code) {
        this.code = code;
    }
}
