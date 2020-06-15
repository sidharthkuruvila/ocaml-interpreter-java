package interp;

import interp.customoperations.CustomOperations;
import interp.customoperations.CustomOperationsList;
import interp.customoperations.CustomOperationsValue;
import interp.value.DoubleValue;
import interp.value.ObjectValue;
import interp.value.StringValue;
import interp.value.Value;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@NotThreadSafe
class DataInStream {
    private int index = 0;
    private int prevIndex = 0;

    private final ByteBuffer bf;

    public DataInStream(ByteBuffer bf){
        this.bf = bf;
    }

    public int readUnsignedByte() throws IOException {
        prevIndex = index;
        index+=Byte.BYTES;
        return 0xFF & bf.get(prevIndex);
    }

    public int readInt() {
        prevIndex = index;
        index+= Integer.BYTES;
        return bf.getInt(prevIndex);
    }

    public long readLong() {
        prevIndex = index;
        index+=Long.BYTES;
        return bf.getInt(prevIndex);
    }

    public byte readByte() {
        prevIndex = index;
        index+=Byte.BYTES;
        return bf.get(prevIndex);
    }

    public short readShort() {
        prevIndex = index;
        index+=Short.BYTES;
        return bf.getShort(prevIndex);
    }

    public int readChar() {
        prevIndex = index;
        index+= Character.BYTES;
        return bf.getChar(prevIndex);
    }

    public void read(byte[] bytes) {
        prevIndex = index;
        bf.position(prevIndex);
        index=bytes.length;
        bf.get(bytes, 0, bytes.length);
    }

    public float readFloat() {
        prevIndex = index;
        index+=Float.BYTES;
        return bf.getFloat(prevIndex);
    }

    public double readDouble() {
        prevIndex = index;
        index+=Double.BYTES;
        return bf.getDouble(prevIndex);
    }
}

class CodeFragment {
    final byte[] code;
    final byte[] digest;

    public CodeFragment(byte[] code) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(code);
            this.code = code;
            this.digest = digest;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

class CodePointerValue implements Value {
    private final CodeFragment codeFragment;
    private final int offset;

    public CodePointerValue(CodeFragment codeFragment, int offset) {
        this.codeFragment = codeFragment;
        this.offset = offset;
    }
}

class CodeFragmentTable {
    private final List<CodeFragment> codeFragments = new ArrayList<>();

    public CodePointerValue codePointerValue(byte[] digest, int offset) {
        for(CodeFragment codeFragment : codeFragments) {

            if(Arrays.equals(digest, codeFragment.digest)) {
                if(offset >= codeFragment.code.length) {
                    throw new RuntimeException("Not enough code to point to");
                }
                return new CodePointerValue(codeFragment, offset);
            }
        }
        throw new RuntimeException("Didn't find a matching code fragment");
    }

    public void add(CodeFragment codeFragment) {
        codeFragments.add(codeFragment);
    }
}

;

class Atom implements Value {
    private final int tag;

    public Atom(int tag) {
        this.tag = tag;
    }
}

class DoubleArray implements Value {
    private final double[] values;

    public DoubleArray(double[] values){
        this.values = values;
    }

    public double getField(int i) {
        return values[i];
    }

    public void setField(int i, double value) {
        values[i] = value;
    }

    public int getSize() {
        return values.length;
    }
}

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

public class Intern {
    int magic_number_small = 0x8495A6BE;
    int magic_number_big = 0x8495A6BF;

    static final int PREFIX_SMALL_BLOCK = 0x80;
    static final int PREFIX_SMALL_INT = 0x40;
    static final int PREFIX_SMALL_STRING = 0x20;
    static final int CODE_INT8 = 0x0;
    static final int CODE_INT16 = 0x1;
    static final int CODE_INT32 = 0x2;
    static final int CODE_INT64 = 0x3;
    static final int CODE_SHARED8 = 0x4;
    static final int CODE_SHARED16 = 0x5;
    static final int CODE_SHARED32 = 0x6;
    static final int CODE_SHARED64 = 0x14;
    static final int CODE_BLOCK32 = 0x8;
    static final int CODE_BLOCK64 = 0x13;
    static final int CODE_STRING8 = 0x9;
    static final int CODE_STRING32 = 0xA;
    static final int CODE_STRING64 = 0x15;
    static final int CODE_DOUBLE_BIG = 0xB;
    static final int CODE_DOUBLE_LITTLE = 0xC;
    static final int CODE_DOUBLE_ARRAY8_BIG = 0xD;
    static final int CODE_DOUBLE_ARRAY8_LITTLE = 0xE;
    static final int CODE_DOUBLE_ARRAY32_BIG = 0xF;
    static final int CODE_DOUBLE_ARRAY32_LITTLE = 0x7;
    static final int CODE_DOUBLE_ARRAY64_BIG = 0x16;
    static final int CODE_DOUBLE_ARRAY64_LITTLE = 0x17;
    static final int CODE_CODEPOINTER = 0x10;
    static final int CODE_INFIXPOINTER = 0x11;
    static final int CODE_CUSTOM = 0x12;
    static final int CODE_CUSTOM_LEN = 0x18;
    static final int CODE_CUSTOM_FIXED = 0x19;

    static final int Object_tag = 248;
    static final int Closure_tag = 247;


    private final CodeFragmentTable codeFragmentTable;
    private final OOIdGenerator ooIdGenerator;
    private final CustomOperationsList customOperationsList;

    public Intern(CustomOperationsList customOperationsList, CodeFragmentTable codeFragmentTable, OOIdGenerator ooIdGenerator) {
        this.customOperationsList = customOperationsList;
        this.codeFragmentTable = codeFragmentTable;
        this.ooIdGenerator = ooIdGenerator;
    }

    public Value inputValue(ByteBuffer bf) throws IOException {
        DataInStream dis = new DataInStream(bf);

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

        return internRec(new ArrayList<>(), dis);
    }


    Value internRec(List<Value> internObjectTable, DataInStream dis) throws IOException {
        int code;
        try {
            code = dis.readUnsignedByte();
        } catch (IOException e) {

            throw e;
        }
        if (code >= PREFIX_SMALL_INT) {
            if (code >= PREFIX_SMALL_BLOCK) {
                int tag = code & 0xF;
                int size = (code >> 4) & 0x7;
                return readBlock(internObjectTable, dis, tag, size);
            } else {
                return new LongValue(code & 0x3f);
            }
        } else {
            if (code >= PREFIX_SMALL_STRING) {
                int len = (code & 0x1F);
                return readString(internObjectTable, dis, len);

            } else {
                switch (code) {
                    case CODE_INT8:
                        return new LongValue(dis.readByte());
                    case CODE_INT16:
                        return new LongValue(dis.readShort());
                    case CODE_INT32:
                        return new LongValue(dis.readInt());
                    case CODE_INT64:
                        return new LongValue(dis.readLong());
                    case CODE_SHARED8: {
                        int offset = dis.readUnsignedByte();
                        return internObjectTable.get(internObjectTable.size() - offset);
                    }
                    case CODE_SHARED16: {
                        int offset = dis.readChar();
                        return internObjectTable.get(internObjectTable.size() - offset);
                    }
                    case CODE_SHARED32: {
                        int offset = dis.readInt();
                        return internObjectTable.get(internObjectTable.size() - offset);
                    }
                    case CODE_SHARED64: {
                        throw new RuntimeException("Long pointers are not supported right now");
                    }
                    case CODE_BLOCK32: {
                        int header = dis.readInt();
                        int tag = header & 0xFF;
                        int size = header >>> 10;
                        return readBlock(internObjectTable, dis, tag, size);
                    }
                    case CODE_BLOCK64: {
                        throw new RuntimeException("Long blocks are not supported right now");
                    }
                    case CODE_STRING8: {
                        int len = dis.readUnsignedByte();
                        return readString(internObjectTable, dis, len);
                    }
                    case CODE_STRING32: {
                        int len = dis.readInt();
                        return readString(internObjectTable, dis, len);
                    }
                    case CODE_STRING64: {
                        throw new RuntimeException("Long strings are not supported right now");
                    }
                    case CODE_DOUBLE_LITTLE: {
                        Value v = new DoubleValue(dis.readFloat());
                        internObjectTable.add(v);
                        return v;
                    }
                    case CODE_DOUBLE_BIG: {
                        Value v = new DoubleValue(dis.readDouble());
                        internObjectTable.add(v);
                        return v;
                    }
                    case CODE_DOUBLE_ARRAY8_LITTLE: {
                        int len = dis.readUnsignedByte();
                        return readFloatArray(internObjectTable, dis, len);
                    }
                    case CODE_DOUBLE_ARRAY8_BIG: {
                        int len = dis.readUnsignedByte();
                        return readDoubleArray(internObjectTable, dis, len);
                    }
                    case CODE_DOUBLE_ARRAY32_LITTLE: {
                        int len = dis.readInt();
                        return readFloatArray(internObjectTable, dis, len);
                    }
                    case CODE_DOUBLE_ARRAY32_BIG: {
                        int len = dis.readInt();
                        return readDoubleArray(internObjectTable, dis, len);
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
                        return codeFragmentTable.codePointerValue(digest, ofs);
                        //Missing logic to point to Debugger.function_placeholder when
                        //the code pointer is not found.
                    }
                    case CODE_INFIXPOINTER:
                        int offset = dis.readInt();
                        return internObjectTable.get(offset);
                    case CODE_CUSTOM:
                    case CODE_CUSTOM_LEN:
                    case CODE_CUSTOM_FIXED:
                        String ident = readCString(dis);
                        CustomOperations ops = customOperationsList.findCustomOperations(ident);

                        if(code == CODE_CUSTOM_FIXED && ops.customFixedLength == null) {
                            throw new RuntimeException("input_value: expected a fixed-size custom block");
                        }
                        long expectedSize;
                        if (code == CODE_CUSTOM) {
                            throw new RuntimeException("Custom operations with CODE_CUSTOM have not been implemented yet");
                        } else{

                           if(code == CODE_CUSTOM_FIXED) {
                               expectedSize = ops.customFixedLength;
                           } else {
                               dis.readInt();
                               expectedSize = dis.readLong();
                           }
                        }
                        return new CustomOperationsValue(ops, ops.deserialize.apply(dis));
                    default:
                        throw new RuntimeException("input_value: ill-formed message");
                }
            }
        }


    }

    String readCString(DataInStream is) throws IOException {
        int ch;
        StringBuilder sb = new StringBuilder();
        while((ch = is.readUnsignedByte()) != 0) {
            sb.append((char)ch);
        }
        return sb.toString();
    }

    private Value readFloatArray(List<Value> internObjectTable, DataInStream dis, int len) throws IOException {
        double[] arr = new double[len];
        for(int i = 0; i < len; i++) {
            arr[i] = dis.readFloat();
        }
        Value v = new DoubleArray(arr);
        internObjectTable.add(v);
        return v;
    }

    private Value readDoubleArray(List<Value> internObjectTable, DataInStream dis, int len) throws IOException {
        double[] arr = new double[len];
        for(int i = 0; i < len; i++) {
            arr[i] = dis.readDouble();
        }
        Value v = new DoubleArray(arr);
        internObjectTable.add(v);
        return v;
    }

    private Value readString(List<Value> internObjectTable, DataInStream dis, int len) throws IOException {
        byte[] bytes = new byte[len];
        dis.read(bytes);
        Value v = new StringValue(bytes);
        internObjectTable.add(v);
        return v;
    }

    private Value readBlock(List<Value> internObjectTable, DataInStream dis, int tag, int size) throws IOException {
        if (size == 0) {
            return new Atom(tag);
        } else {
            ObjectValue o = new ObjectValue(tag, size);
            internObjectTable.add(o);
            for (int i = 0; i < size; i++) {
                o.setField(i, internRec(internObjectTable, dis));
            }
            if (tag == Object_tag) {
                setOOId(o);
            }
            return o;
        }
    }

    private void setOOId(ObjectValue o) {
        Value v = o.getField(1);
        if (v instanceof LongValue) {
            ((LongValue) v).setValue(ooIdGenerator.nextId());
        }
    }
}
