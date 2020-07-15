package interp;

import interp.value.ObjectValue;
import interp.value.StringValue;
import interp.value.Value;

class LexBuf {
    private final ObjectValue objectValue;

    public LexBuf(ObjectValue objectValue) {
        this.objectValue = objectValue;
    }

    Value refill_buff() {
        return objectValue.getField(0);
    }

    byte[] lex_buffer() {
        return ((StringValue) objectValue.getField(1)).getBytes();
    }

    int getBufferLen() {
        return objectValue.getIntField(2);
    }

    Value lex_abs_pos() {
        return objectValue.getField(3);
    }

    Value startPos() {
        return objectValue.getField(4);
    }

    void setStartPos(int v) {
        objectValue.setField(4, LongValue.wrap(v));
    }

    ;

    int getCurrPos() {
        return objectValue.getIntField(5);
    }

    void setCurPos(int v) {
        objectValue.setField(5, LongValue.wrap(v));
    }

    ;

    int getLastPos() {
        return objectValue.getIntField(6);
    }

    ;

    void setLastPos(long v) {
        objectValue.setField(6, LongValue.wrap(v));
    }

    ;

    int getLastAction() {
        return objectValue.getIntField(7);
    }

    ;

    void setLexLastAction(Value v) {
        objectValue.setField(7, v);
    }

    ;

    Value getEofReached() {
        return objectValue.getField(8);
    }

    public void setEofReached(Value value) {
        objectValue.setField(8, value);
    }


    ObjectValue getLexMem() {
        return (ObjectValue) objectValue.getField(9);
    }

    ;

    Value lex_start_p() {
        return objectValue.getField(10);
    }

    ;

    Value lex_curr_p() {
        return objectValue.getField(11);
    }


    ;
}

class LexTable {
    private final ObjectValue objectValue;

    public LexTable(ObjectValue objectValue) {
        this.objectValue = objectValue;
    }

    short lookup(int tableIndex, int n) {
        byte[] table = ((StringValue) objectValue.getField(tableIndex)).getBytes();
        int i = 2 * n;
        int b2 = 0xFF & table[i];
        int b1 = 0xFF & table[i + 1];
        return (short) ((b1 << 8) | b2);
    }

    short lookupBase(int n) {
        return lookup(0, n);
    }

    short backtrack(int n) {
        return lookup(1, n);
    }

    short lex_default(int n) {
        return lookup(2, n);
    }

    short lex_trans(int n) {
        return lookup(3, n);
    }

    short lex_check(int n) {
        return lookup(4, n);
    }

    short lex_base_code(int n) {
        return lookup(5, n);
    }

    short lex_backtrk_code(int n) {
        return lookup(6, n);
    }

    short lex_default_code(int n) {
        return lookup(7, n);
    }

    short lex_trans_code(int n) {
        return lookup(8, n);
    }

    short lex_check_code(int n) {
        return lookup(9, n);
    }

    //    short lex_code(int n) { return lookup(10, n); }
    byte[] lex_code() {
        return ((StringValue) objectValue.getField(10)).getBytes();
    }
}

public class Lexer {

    public static LongValue engine(ObjectValue lexingTableValue, LongValue startStateValue,
                               ObjectValue lexbufValue) {
//  int state, base, backtrk, c;
//
        int state = LongValue.unwrapInt(startStateValue);
        LexBuf lexBuf = new LexBuf(lexbufValue);
        LexTable lexTable = new LexTable(lexingTableValue);
        if (state >= 0) {
            /* First entry */
            lexBuf.setLastPos(lexBuf.getCurrPos());
            lexBuf.setStartPos(lexBuf.getCurrPos());
            lexBuf.setLexLastAction(LongValue.wrap(-1));
        } else {
            state = -state - 1;
        }
        int c;
        while (true) {
            /* Lookup base address or action number for current state */
            short base = lexTable.lookupBase(state);
            if (base < 0) return LongValue.wrap(-base - 1);
            /* See if it's a backtrack point */
            //    backtrk = Short(tbl->lex_backtrk, state);
            //    if (backtrk >= 0) {
            //      lexbuf->lex_last_pos = lexbuf->lex_curr_pos;
            //      lexbuf->lex_last_action = Val_int(backtrk);
            //    }
            short backtrk = lexTable.backtrack(state);
            if (backtrk >= 0) {
                lexBuf.setLastPos(lexBuf.getCurrPos());
                lexBuf.setLexLastAction(LongValue.wrap(backtrk));
            }
            /* See if we need a refill */
            //    if (lexbuf->lex_curr_pos >= lexbuf->lex_buffer_len){
            //      if (lexbuf->lex_eof_reached == Val_bool (0)){
            //        return Val_int(-state - 1);
            //      }else{
            //        c = 256;
            //      }
            //    }else{
            //      /* Read next input char */
            //      c = Byte_u(lexbuf->lex_buffer, Long_val(lexbuf->lex_curr_pos));
            //      lexbuf->lex_curr_pos += 2;
            //    }
            /* See if we need a refill */
            if (lexBuf.getCurrPos() >= lexBuf.getBufferLen()) {
                if (lexBuf.getEofReached().equals(Value.booleanValue(false))) {
                    return LongValue.wrap(-state - 1);
                } else {
                    c = 256;
                }
            } else {
                /* Read next input char */
                c = lexBuf.lex_buffer()[lexBuf.getCurrPos()];
                lexBuf.setCurPos(lexBuf.getCurrPos() + 1);
            }
            /* Determine next state */
            if (lexTable.lex_check(base + c) == state)
                state = lexTable.lex_trans(base + c);
            else
                state = lexTable.lex_default(state);
//    /* If no transition on this char, return to last backtrack point */
//    if (state < 0) {
//      lexbuf->lex_curr_pos = lexbuf->lex_last_pos;
//      if (lexbuf->lex_last_action == Val_int(-1)) {
//        caml_failwith("lexing: empty token");
//      } else {
//        return lexbuf->lex_last_action;
//      }
//    }else{
//      /* Erase the EOF condition only if the EOF pseudo-character was
//         consumed by the automaton (i.e. there was no backtrack above)
//       */
//      if (c == 256) lexbuf->lex_eof_reached = Val_bool (0);
//    }
            /* If no transition on this char, return to last backtrack point */
            if (state < 0) {
                lexBuf.setCurPos(lexBuf.getLastPos());
                if (lexBuf.getLastAction() == -1) {
                    throw Fail.failWithException("lexing: empty token");
                } else {
                    return LongValue.wrap(lexBuf.getLastAction());
                }
            } else {
      /* Erase the EOF condition only if the EOF pseudo-character was
         consumed by the automaton (i.e. there was no backtrack above)
       */
                if (c == 256) lexBuf.setEofReached(Value.booleanValue(false));
            }
        }
    }

    public static Value caml_new_lex_engine(ObjectValue lexingTableValue, LongValue startStateValue,
                              ObjectValue lexbufValue) {
        int state = LongValue.unwrapInt(startStateValue);
        LexBuf lexBuf = new LexBuf(lexbufValue);
        LexTable lexTable = new LexTable(lexingTableValue);
        if (state >= 0) {
            /* First entry */
            lexBuf.setLastPos(lexBuf.getCurrPos());
            lexBuf.setStartPos(lexBuf.getCurrPos());
            lexBuf.setLexLastAction(LongValue.wrap(-1));
        } else {
            state = -state - 1;
        }
        int c;
        while (true) {
            short base = lexTable.lookupBase(state);
            if (base < 0) {
                int pc_off = lexTable.lex_base_code(base);
                runTag(lexTable.lex_code(), pc_off, lexBuf.getLexMem());
                /*      fprintf(stderr,"Perform: %d\n",-base-1) ; */
                return LongValue.wrap(-base - 1);
            }
            /* See if it's a backtrack point */
            short backtrk = lexTable.backtrack(state);
            if (backtrk >= 0) {
                int pc_off = lexTable.lex_backtrk_code(state);
                runTag(lexTable.lex_code(), pc_off, lexBuf.getLexMem());
                lexBuf.setLastPos(lexBuf.getCurrPos());
                lexBuf.setLexLastAction(LongValue.wrap(backtrk));
            }

            /* See if we need a refill */
            if (lexBuf.getCurrPos() >= lexBuf.getBufferLen()) {
                if (lexBuf.getEofReached().equals(Value.booleanValue(false))) {
                    return LongValue.wrap(-state - 1);
                } else {
                    c = 256;
                }
            } else {
                /* Read next input char */
                c = lexBuf.lex_buffer()[lexBuf.getCurrPos()];
                lexBuf.setCurPos(lexBuf.getCurrPos() + 1);
            }
            /* Determine next state */
            int pstate = state;
            if (lexTable.lex_check(base + c) == state)
                state = lexTable.lex_trans(base + c);
            else
                state = lexTable.lex_default(state);
            /* If no transition on this char, return to last backtrack point */
            if (state < 0) {
                lexBuf.setCurPos(lexBuf.getLastPos());
                if (lexBuf.getLastAction() == -1) {
                    throw Fail.failWithException("lexing: empty token");
                } else {
                    return LongValue.wrap(lexBuf.getLastAction());
                }
            } else {
                /* If some transition, get and perform memory moves */
                int base_code = lexTable.lex_base_code(pstate);
                int pc_off;
                if (lexTable.lex_check_code(base_code + c) == pstate)
                    pc_off = lexTable.lex_trans_code(base_code + c);
                else
                    pc_off = lexTable.lex_default_code(pstate);
                if (pc_off > 0)
                    runMem(lexTable.lex_code(), pc_off, lexBuf.getLexMem(),
                            lexBuf.getCurrPos());
      /* Erase the EOF condition only if the EOF pseudo-character was
         consumed by the automaton (i.e. there was no backtrack above)
       */
                if (c == 256) lexBuf.setEofReached(Value.booleanValue(false));
            }
        }
    }


    static void runMem(byte[] bytes, int pc, ObjectValue mem, int curr_pos) {
        for (; ; ) {
            int dst, src;

            dst = charAt(bytes, pc++);
            if (dst == 0xff)
                return;
            src = charAt(bytes, pc++);
            if (src == 0xff) {
                /*      fprintf(stderr,"[%hhu] <- %d\n",dst,Int_val(curr_pos)) ;*/
                mem.setField(dst, LongValue.wrap(curr_pos));
            } else {
                /*      fprintf(stderr,"[%hhu] <- [%hhu]\n",dst,src) ; */
                mem.setField(dst, mem.getField(src));
            }
        }
    }

    static void runTag(byte[] bytes, int pc, ObjectValue mem) {
        for (; ; ) {
            int dst, src;

            dst = charAt(bytes, pc++);
            if (dst == 0xff)
                return;
            src = charAt(bytes, pc++);
            if (src == 0xff) {
                /*      fprintf(stderr,"[%hhu] <- -1\n",dst) ; */
                mem.setField(dst, LongValue.wrap(-1));
            } else {
                /*      fprintf(stderr,"[%hhu] <- [%hhu]\n",dst,src) ; */
                mem.setField(dst, mem.getField(src));
            }
        }
    }

    private static int charAt(byte[] bytes, int n) {
//        int i = 2 * n;
//        int b1 = 0xFF & bytes[i];
        return 0xFF & bytes[n];
//        int b2 = 0xFF & bytes[i + 1];
//        return (char) ((b1 << 8) & b2);
    }

}
