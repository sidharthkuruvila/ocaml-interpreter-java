package interp;

import interp.customoperations.CustomOperations;
import interp.customoperations.CustomOperationsList;
import interp.customoperations.CustomOperationsValue;
import interp.value.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static interp.ValueTag.Object_tag;

class CodePointerValue implements Value {
    private final CodeFragment codeFragment;
    private final int offset;

    public CodePointerValue(CodeFragment codeFragment, int offset) {
        this.codeFragment = codeFragment;
        this.offset = offset;
    }
}

;

class Header {
    private boolean isBig;
    private long dataLength;
    private long numObjects;
    private long sizeInWords;

    public Header(boolean isBig, long dataLength, long numObjects, long sizeInWords) {
        this.isBig = isBig;
        this.dataLength = dataLength;
        this.numObjects = numObjects;
        this.sizeInWords = sizeInWords;
    }

}

class InternItem {
    static enum Op {
        OReadItems, OFreshOID, OShift
    }

    public Value dest;
    public int arg;
    public Op op;
}

class InternStackEntry {
    private final ObjectValue o;
    private final int currentField;

    InternStackEntry(ObjectValue o, int currentField) {
        this.o = o;
        this.currentField = currentField;
    }

    public int getCurrentField() {
        return currentField;
    }

    public ObjectValue getO() {
        return o;
    }
}

public class Intern {
    static final int sizeOfValue = 8;
    public static final int magic_number_small = 0x8495A6BE;
    public static final int magic_number_big = 0x8495A6BF;

    public static final int PREFIX_SMALL_BLOCK = 0x80;
    public static final int PREFIX_SMALL_INT = 0x40;
    public static final int PREFIX_SMALL_STRING = 0x20;
    public static final int CODE_INT8 = 0x0;
    public static final int CODE_INT16 = 0x1;
    public static final int CODE_INT32 = 0x2;
    public static final int CODE_INT64 = 0x3;
    public static final int CODE_SHARED8 = 0x4;
    public static final int CODE_SHARED16 = 0x5;
    public static final int CODE_SHARED32 = 0x6;
    public static final int CODE_SHARED64 = 0x14;
    public static final int CODE_BLOCK32 = 0x8;
    public static final int CODE_BLOCK64 = 0x13;
    public static final int CODE_STRING8 = 0x9;
    public static final int CODE_STRING32 = 0xA;
    public static final int CODE_STRING64 = 0x15;
    public static final int CODE_DOUBLE_BIG = 0xB;
    public static final int CODE_DOUBLE_LITTLE = 0xC;
    public static final int CODE_DOUBLE_ARRAY8_BIG = 0xD;
    public static final int CODE_DOUBLE_ARRAY8_LITTLE = 0xE;
    public static final int CODE_DOUBLE_ARRAY32_BIG = 0xF;
    public static final int CODE_DOUBLE_ARRAY32_LITTLE = 0x7;
    public static final int CODE_DOUBLE_ARRAY64_BIG = 0x16;
    public static final int CODE_DOUBLE_ARRAY64_LITTLE = 0x17;
    public static final int CODE_CODEPOINTER = 0x10;
    public static final int CODE_INFIXPOINTER = 0x11;
    public static final int CODE_CUSTOM = 0x12;
    public static final int CODE_CUSTOM_LEN = 0x18;
    public static final int CODE_CUSTOM_FIXED = 0x19;




    private final CodeFragmentTable codeFragmentTable;
    private final OOIdGenerator ooIdGenerator;
    private final CustomOperationsList customOperationsList;
    public boolean useOriginalOOId = false;

    public Intern(CustomOperationsList customOperationsList, CodeFragmentTable codeFragmentTable, OOIdGenerator ooIdGenerator) {
        this.customOperationsList = customOperationsList;
        this.codeFragmentTable = codeFragmentTable;
        this.ooIdGenerator = ooIdGenerator;
    }


    public Value inputValue(ByteBuffer bf) throws IOException {

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bf.array()));
        return inputValue(dis);
    }

    public Value inputValue(DataInputStream dis) throws IOException {
        int magic = dis.readInt();
        /*
        /* Header format for the "small" model: 20 bytes
       0   "small" magic number
       4   length of marshaled data, in bytes
       8   number of shared blocks
      12   size in words when read on a 32-bit platform
      16   size in words when read on a 64-bit platform
   The 4 numbers are 32 bits each, in big endian.

   Header format for the "big" model: 32 bytes
       0   "big" magic number
       4   four reserved bytes, currently set to 0
       8   length of marshaled data, in bytes
      16   number of shared blocks
      24   size in words when read on a 64-bit platform
   The 3 numbers are 64 bits each, in big endian.
         */

        Header header;
        if (magic == magic_number_small) {
            int dataLength = dis.readInt();
            int numObjects = dis.readInt();
            dis.readInt();
            int sizeInWords = dis.readInt();
            header = new Header(false, dataLength, numObjects, sizeInWords);
        } else if (magic == magic_number_big) {
            dis.readInt();
            long dataLength = dis.readLong();
            long numObjects = dis.readLong();
            long sizeInWords = dis.readLong();
            header = new Header(true, dataLength, numObjects, sizeInWords);
        } else {
            throw new RuntimeException(String.format("Unrecogonized magic number. %08X", magic));
        }

        List<Value> internObjectTable = new ArrayList<>();
        Value v = internRec(internObjectTable, dis);
        return v;
    }




    Value internRec(List<Value> internObjectTable, DataInputStream dis) throws IOException {
        Deque<InternStackEntry> internStack = new ArrayDeque<>();
        ObjectValue bottom = new ObjectValue(ValueTag.PAIR_TAG, 1);
        int field = 0;

        while(true) {
            while(bottom.getSize() == field){
                if(internStack.isEmpty()){
                    return bottom.getField(0);
                }
                InternStackEntry entry = internStack.pop();
                bottom = entry.getO();
                field = entry.getCurrentField();
            }
            int code;
            Value next;
            code = dis.readUnsignedByte();
            if (code >= PREFIX_SMALL_INT) {
                if (code >= PREFIX_SMALL_BLOCK) {
                    int tag = ValueTag.of(code & 0xF);
                    int size = (code >> 4) & 0x7;
                    if(size  == 0) {
                        next = new Atom(tag);
                    } else {
                        ObjectValue block = new ObjectValue(tag, size);
                        bottom.setField(field, block);
                        field += 1;
                        internStack.push(new InternStackEntry(bottom, field));
                        bottom = block;
                        internObjectTable.add(block);
                        if(tag == Object_tag) {
                            assert size >= 2;
                            Value v1 = internRec(internObjectTable, dis);
                            Value v2 = internRec(internObjectTable, dis);
                            assert v2 instanceof LongValue;
                            block.setField(0, v1);
                            block.setField(1, new LongValue(getNextOOId(v2)));
                            field = 2;
                        } else {
                            field = 0;
                        }
                        continue;
                    }
                } else {
                    next = new LongValue(code & 0x3f);
                }
            } else {
                if (code >= PREFIX_SMALL_STRING) {
                    int len = (code & 0x1F);
                    next = readString(internObjectTable, dis, len);
                } else {
                    switch (code) {
                        case CODE_INT8:
                            next = new LongValue(dis.readByte());
                            break;
                        case CODE_INT16:
                            next = new LongValue(dis.readShort());
                            break;
                        case CODE_INT32:
                            next = new LongValue(dis.readInt());
                            break;
                        case CODE_INT64:
                            next = new LongValue(dis.readLong());
                            break;
                        case CODE_SHARED8: {
                            int offset = dis.readUnsignedByte();
                            next = internObjectTable.get(internObjectTable.size() - offset);
                            break;
                        }
                        case CODE_SHARED16: {
                            int offset = dis.readChar();
                            next = internObjectTable.get(internObjectTable.size() - offset);
                            break;
                        }
                        case CODE_SHARED32: {
                            int offset = dis.readInt();
                            next = internObjectTable.get(internObjectTable.size() - offset);
                            break;
                        }
                        case CODE_SHARED64: {
                            throw new RuntimeException("Long pointers are not supported right now");
                        }
                        case CODE_BLOCK32: {
                            int header = dis.readInt();
                            int tag = ValueTag.of(header & 0xFF);
                            int size = header >>> 10;
                            ObjectValue block = new ObjectValue(tag, size);
                            bottom.setField(field, block);
                            field += 1;
                            internStack.push(new InternStackEntry(bottom, field));
                            bottom = block;
                            internObjectTable.add(block);
                            if(tag == Object_tag) {
                                assert size >= 2;
                                Value v1 = internRec(internObjectTable, dis);
                                Value v2 = internRec(internObjectTable, dis);
                                assert v2 instanceof LongValue;
                                block.setField(0, v1);
                                block.setField(1, new LongValue(getNextOOId(v2)));
                                field = 2;
                            } else {
                                field = 0;
                            }
                            continue;
                        }
                        case CODE_BLOCK64: {
                            throw new RuntimeException("Long blocks are not supported right now");
                        }
                        case CODE_STRING8: {
                            int len = dis.readUnsignedByte();
                            next = readString(internObjectTable, dis, len);
                            break;
                        }
                        case CODE_STRING32: {
                            int len = dis.readInt();
                            next = readString(internObjectTable, dis, len);
                            break;
                        }
                        case CODE_STRING64: {
                            throw new RuntimeException("Long strings are not supported right now");
                        }
                        case CODE_DOUBLE_LITTLE:
                        case CODE_DOUBLE_BIG: {
                            boolean isLittle = code == CODE_DOUBLE_LITTLE;
                            Value v = new DoubleValue(readDouble(dis, isLittle));
                            internObjectTable.add(v);
                            next = v;
                            break;
                        }
                        case CODE_DOUBLE_ARRAY8_LITTLE:
                        case CODE_DOUBLE_ARRAY8_BIG: {
                            int len = dis.readUnsignedByte();
                            next = readDoubleArray(internObjectTable, dis, len, code == CODE_DOUBLE_ARRAY8_LITTLE);
                            internObjectTable.add(next);
                            break;
                        }
                        case CODE_DOUBLE_ARRAY32_LITTLE:
                        case CODE_DOUBLE_ARRAY32_BIG: {
                            int len = dis.readInt();
                            next = readDoubleArray(internObjectTable, dis, len, code == CODE_DOUBLE_ARRAY32_LITTLE);
                            internObjectTable.add(next);
                            break;
                        }
                        case CODE_DOUBLE_ARRAY64_LITTLE: {
                            throw new RuntimeException("Long float arrays are not supported right now");
                        }
                        case CODE_DOUBLE_ARRAY64_BIG: {
                            throw new RuntimeException("Long double arrays are not supported right now");
                        }
                        case CODE_CODEPOINTER: {
                            int ofs = dis.readInt();
                            byte[] digest = new byte[16];
                            dis.read(digest);
                            next = codeFragmentTable.codePointerValue(digest, ofs);
                            break;
                            //Missing logic to point to Debugger.function_placeholder when
                            //the code pointer is not found.
                        }
                        case CODE_INFIXPOINTER:
                            int offset = dis.readInt();
                            next = internObjectTable.get(offset);
                            break;
                        case CODE_CUSTOM:
                        case CODE_CUSTOM_LEN:
                        case CODE_CUSTOM_FIXED:
                            String ident = readCString(dis);
                            CustomOperations ops = customOperationsList.findCustomOperations(ident);

                            if (code == CODE_CUSTOM_FIXED && ops.customFixedLength == null) {
                                throw new RuntimeException("input_value: expected a fixed-size custom block");
                            }
                            long expectedSize;
                            if (code == CODE_CUSTOM) {
                                throw new RuntimeException("Custom operations with CODE_CUSTOM have not been implemented yet");
                            } else {

                                if (code == CODE_CUSTOM_FIXED) {
                                    expectedSize = ops.customFixedLength;
                                } else {
                                    dis.readInt();
                                    expectedSize = dis.readLong();
                                }
                            }

                            next = new CustomOperationsValue(ops, ops.deserialize.apply(dis));
                            internObjectTable.add(next);

                            //TODO add finalizer logic


                            break;
                        default:
                            throw new RuntimeException("input_value: ill-formed message");
                    }
                }
            }

            bottom.setField(field, next);
            field += 1;
        }
    }

    private int getNextOOId(Value originalValue) {
        if(useOriginalOOId) {
            return LongValue.unwrapInt((LongValue) originalValue);
        } else {
            return ooIdGenerator.nextId();
        }
    }

    private double readDouble(DataInputStream dis, boolean isLittle) throws IOException {
        byte[] bytes = new byte[Double.BYTES];
        dis.read(bytes);
        ByteBuffer bf = ByteBuffer.wrap(bytes);
        if(isLittle) {
            bf.order(ByteOrder.LITTLE_ENDIAN);
        }
        return bf.getDouble();
    }

    String readCString(DataInputStream is) throws IOException {
        int ch;
        StringBuilder sb = new StringBuilder();
        while((ch = is.readUnsignedByte()) != 0) {
            sb.append((char)ch);
        }
        return sb.toString();
    }


    private Value readDoubleArray(List<Value> internObjectTable, DataInputStream dis, int len, boolean littleEndian) throws IOException {
        double[] arr = new double[len];
        for(int i = 0; i < len; i++) {
            arr[i] = readDouble(dis, littleEndian);
        }
        Value v = new DoubleArray(arr);
        internObjectTable.add(v);
        return v;
    }

    private Value readString(List<Value> internObjectTable, DataInputStream dis, int len) throws IOException {
        byte[] bytes = new byte[len];
        dis.read(bytes);
        Value v = new StringValue(bytes);
        internObjectTable.add(v);
        return v;
    }

//    private Value readBlock(List<Value> internObjectTable, DataInputStream dis, int tag, int size) throws IOException {
//        if (size == 0) {
//            return new Atom(tag);
//        } else {
//            ObjectValue o = new ObjectValue(tag, size);
//            internObjectTable.add(o);
//            for (int i = 0; i < size; i++) {
//                o.setField(i, internRec(internObjectTable, dis));
//            }
//            if (tag == Object_tag) {
//                o.setField(1, new LongValue(getNextOOId()));
//            }
//            return o;
//        }
//    }

}
