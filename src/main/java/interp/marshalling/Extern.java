package interp.marshalling;


import interp.*;
import interp.customoperations.CustomOperationsValue;
import interp.value.*;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class Extern {
    //    #define PREFIX_SMALL_BLOCK 0x80
    public static long PREFIX_SMALL_INT = 0x40;
    private final CodeFragmentTable codeFragmentTable;

    public Extern(CodeFragmentTable codeFragmentTable) {
        this.codeFragmentTable = codeFragmentTable;
    }

    private static class Field {
        private final ObjectValue obj;
        private final int field;

        public Field(ObjectValue obj, int field) {

            this.obj = obj;
            this.field = field;
        }

        Field next() {
            return new Field(obj, field + 1);
        }

        boolean isLastField() {
            return obj.getSize() == field + 1;
        }
    }

    public void outputValue(OutputStream os, Value value) {
        try {
            DataOutputStream dos = new DataOutputStream(os);
            ByteArrayOutputStream bosExt = new ByteArrayOutputStream();
            DataOutputStream dosExt = new DataOutputStream(bosExt);
            SizeInfo sizeInfo = extern_rec(dosExt, value);
            byte[] bytes = bosExt.toByteArray();
            dos.writeInt(Intern.magic_number_small);
            dos.writeInt(bytes.length);
            dos.writeInt(sizeInfo.objectCount);
            dos.writeInt(sizeInfo.size32);
            dos.writeInt(sizeInfo.size64);
            dos.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class SizeInfo {
        private final int objectCount;
        private final int size32;
        private final int size64;

        public SizeInfo(int objectCount, int size32, int size64) {

            this.objectCount = objectCount;
            this.size32 = size32;
            this.size64 = size64;
        }


    }

    public SizeInfo extern_rec(DataOutputStream dos, Value v) {
        try {
            Deque<Field> objectStack = new ArrayDeque<>();

            List<Value> externObjectTable = new ArrayList<>();
            int objectCount = 0;
            int size_32 = 0;
            int size_64 = 0;
            boolean nextItem = false;

            while (true) {
                if (v instanceof LongValue) {
                    long n = LongValue.unwrap((LongValue) v);
                    if (n >= 0 && n < 0x40) {
                        dos.writeByte((int) (PREFIX_SMALL_INT + n));
                    } else if (n >= -(1 << 7) && n < (1 << 7)) {
                        dos.writeByte(Intern.CODE_INT8);
                        dos.writeByte((int) n);
                    } else if (n >= -(1 << 15) && n < (1 << 15)) {
                        dos.writeByte(Intern.CODE_INT16);
                        dos.writeShort((short) n);
                    } else if (n < -(1L << 30) || n >= (1L << 30)) {
                        dos.writeByte(Intern.CODE_INT64);
                        dos.writeLong(n);
                    } else {
                        dos.writeByte(Intern.CODE_INT32);
                        dos.writeInt((int) n);
                    }
                } else if (externObjectTable.contains(v)) {
                    int d = externObjectTable.size() - externObjectTable.indexOf(v);
                    if (d < 0x100) {
                        dos.writeByte(Intern.CODE_SHARED8);
                        dos.writeByte(d);
                    } else if (d < 0x10000) {
                        dos.writeByte(Intern.CODE_SHARED16);
                        dos.writeShort(d);
                    } else {
                        dos.writeByte(Intern.CODE_SHARED32);
                        dos.writeInt(d);
                    }
                } else if (v instanceof StringValue) {
                    StringValue str = (StringValue) v;
                    int len = str.length();
                    if (len < 0x20) {
                        dos.writeByte(Intern.PREFIX_SMALL_STRING + len);
                    } else if (len < 0x100) {
                        dos.writeByte(Intern.CODE_STRING8);
                        dos.writeByte(len);
                    } else {
                        dos.writeByte(Intern.CODE_STRING32);
                        dos.writeInt(len);
                    }
                    dos.write(str.getBytes());
                    size_32 += 1 + (len + 4) / 4;
                    size_64 += 1 + (len + 8) / 8;
                    externObjectTable.add(v);
                    objectCount += 1;
                } else if (v instanceof DoubleValue) {
                    double d = DoubleValue.unwrap((DoubleValue) v);
                    dos.writeByte(Intern.CODE_DOUBLE_BIG);
                    dos.writeDouble(d);
                    size_32 += 1 + 2;
                    size_64 += 1 + 1;
                    externObjectTable.add(v);
                    objectCount += 1;

                } else if (v instanceof DoubleArray) {
                    DoubleArray arr = (DoubleArray) v;
                    int len = arr.getSize();
                    if (len < 0x100) {
                        dos.writeByte(Intern.CODE_DOUBLE_ARRAY8_BIG);
                        dos.writeByte(len);
                    } else {
                        dos.writeByte(Intern.CODE_DOUBLE_ARRAY32_BIG);
                        dos.writeInt(len);
                    }
                    for (double d : DoubleArray.unwrap(arr)) {
                        dos.writeDouble(d);
                    }
                    size_32 += 1 + len * 2;
                    size_64 += 1 + len;
                    externObjectTable.add(v);
                    objectCount += 1;
                } else if (v instanceof CustomOperationsValue) {
                    CustomOperationsValue<?> cov = (CustomOperationsValue<?>) v;
                    String identifier = cov.ops().identifier;

                    Long fixedLength = cov.ops().customFixedLength;
                    if (cov.ops().serialize == null) {
                        Fail.caml_invalid_argument("output_value: abstract value (Custom)");
                    }
                    byte[] bytes;
                    if (fixedLength == null) {
                        dos.write(Intern.CODE_CUSTOM_LEN);
                        dos.write(identifier.getBytes());
                        dos.write(0);
                        bytes = cov.serialize();
                        dos.writeInt(bytes.length);
                        dos.writeLong(bytes.length);
                        dos.write(bytes);
                    } else {
                        dos.write(Intern.CODE_CUSTOM_FIXED);
                        dos.write(identifier.getBytes());
                        dos.write(0);
                        bytes = cov.serialize();
                        dos.write(bytes);

                    }
                    size_32 = 2 + ((bytes.length + 3) >> 2);
                    size_64 += 2 + ((bytes.length + 7) >> 3);
                    externObjectTable.add(v);
                    objectCount += 1;
//                    if(bytes.length != fixedLength) {
//                        throw new RuntimeException("output_value: incorrect fixed sizes specified by " + identifier);
//                    }

                } else if (v instanceof ObjectValue) {
                    ObjectValue obj = (ObjectValue) v;
//                int header = v.getHeader();
                    int tag = v.getTag();
                    int sz = obj.getSize();
                    int header = (sz << 10) + tag;
//                int size = v();
//                header_t hd = Hd_val(v);
//                tag_t tag = Tag_hd(hd);

                    if (tag == ValueTag.Forward_tag) {
                        Value f = ((ObjectValue) v).getField(0);
                        if (f instanceof ObjectValue
                                && (f.getTag() == ValueTag.Forward_tag
                                || f.getTag() == ValueTag.Lazy_tag
                                || f.getTag() == ValueTag.Double_tag
                        )) {
                            /* Do not short-circuit the pointer. */
                        } else {
                            v = f;
                            continue;
                        }
                    }

                /* Atoms are treated specially for two reasons: they are not allocated
                in the externed block, and they are automatically shared. */
                    else if (sz == 0) {
                        if (tag < 16) {
                            dos.writeByte(Intern.PREFIX_SMALL_BLOCK + tag);
                        } else {
                            dos.writeByte(Intern.CODE_BLOCK32);
                            dos.writeInt(header);
                        }
                        nextItem = true;
                    } else {
                        /* Output the contents of the object */
                        switch (tag) {
                            case ValueTag.Abstract_tag:
                                Fail.caml_invalid_argument("output_value: abstract value (Abstract)");
                                break;
                            case ValueTag.Infix_tag: {
                                throw new RuntimeException("Not implemented yet");
//                                writecode32(CODE_INFIXPOINTER, Infix_offset_hd(hd));
//                                v = v - Infix_offset_hd(hd); /* PR#5772 */
//                                continue;
                            }

                            default: {
                                Value field0;
                                if (tag < 16 && sz < 8) {
                                    dos.writeByte(Intern.PREFIX_SMALL_BLOCK + tag + (sz << 4));
                                } else {
                                    dos.writeByte(Intern.CODE_BLOCK32);
                                    dos.writeInt(header);
                                }
                                size_32 += 1 + sz;
                                size_64 += 1 + sz;
                                field0 = obj.getField(0);
                                externObjectTable.add(v);
                                objectCount += 1;
                                /* Remember that we still have to serialize fields 1 ... sz - 1 */
                                if (sz > 1) {
                                    objectStack.push(new Field(obj, 1));
                                }
                                /* Continue serialization with the first field */
                                v = field0;
                                continue;
                            }
                        }
                    }
                } else if (v instanceof CodePointer) {
//                caml_find_code_fragment(( char*)v, NULL, &cf)
                    CodePointer codePointer = (CodePointer) v;
                    CodeFragment cf = codeFragmentTable.codeFragmentForCodePointer(codePointer).get();
                    dos.writeByte(Intern.CODE_CODEPOINTER);
                    dos.writeInt(codePointer.index);
                    dos.write(cf.getDigest());

                } else {
                    Fail.caml_invalid_argument("output_value: cannot marshal");
                }
                if (objectStack.isEmpty()) {
                    return new SizeInfo(objectCount, size_32, size_64);
                }
                Field f = objectStack.pop();
                if (!f.isLastField()) {
                    objectStack.push(f.next());
                }
                v = f.obj.getField(f.field);

            }
            /* Never reached as function leaves with return */
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
