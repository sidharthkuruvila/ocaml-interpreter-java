package interp.marshalling;

import interp.*;
import interp.customoperations.CustomOperationsList;
import interp.customoperations.CustomOperationsValue;
import interp.ints.Int32CustomOperations;
import interp.ints.Int64CustomOperations;
import interp.ints.NativeIntCustomOperations;
import interp.value.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestMarshalling {
    Compare compare = new Compare(new CamlState());

    @Test
    void testMarshalling() throws IOException {
        marshalUnmarshalTest(LongValue.wrap(37));
        marshalUnmarshalTest(LongValue.wrap(1024));
        marshalUnmarshalTest(LongValue.wrap(1 << 20));
        marshalUnmarshalTest(LongValue.wrap(1L << 34));
        marshalUnmarshalTest(DoubleValue.wrap(3.75));
        marshalUnmarshalTest(DoubleArray.wrap(new double[]{1.5, 2.5, 3.5}));
        marshalUnmarshalTest(StringValue.ofString("hello world"));
        ObjectValue objectValue = new ObjectValue(0, 6);
//        objectValue.setField(0, LongValue.wrap(37));
//        objectValue.setField(1, LongValue.wrap(1024));
//        objectValue.setField(2, DoubleValue.wrap(3.75));
//        objectValue.setField(3, DoubleArray.wrap(new double[]{1.5, 2.5, 3.5}));
//        objectValue.setField(4, StringValue.ofString("hello world"));
//        objectValue.setField(5, objectValue);
//        marshalUnmarshalTest(objectValue);
        marshalUnmarshalTest(new CustomOperationsValue<Integer>(Int32CustomOperations.getInstance(), 23));
        marshalUnmarshalTest(new CustomOperationsValue<Long>(Int64CustomOperations.getInstance(), 23L));
        marshalUnmarshalTest(new CustomOperationsValue<Long>(NativeIntCustomOperations.getInstance(), 23L));
        marshalUnmarshalTest(new CustomOperationsValue<Long>(NativeIntCustomOperations.getInstance(), 1L << 34));
    }

    private void marshalUnmarshalTest(Value lv) throws IOException {
        CodeFragmentTable codeFragmentTable = new CodeFragmentTable();
        CustomOperationsList customOperationsList = new CustomOperationsList();
        Extern extern = new Extern(codeFragmentTable);
        Intern intern = new Intern(customOperationsList, null, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        extern.outputValue(bos, lv);
        byte[] bytes = bos.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        Value olv = intern.inputValue(new DataInputStream(bis));
        assertTrue(compare.compare(lv, olv, true) == 0);
    }

    @Test
    void testMarshallingDataInScript() throws IOException {
        Path path = resourcePath("test1.dump");
        CodeFragmentTable codeFragments = new CodeFragmentTable();
        byte[] bytes = Files.readAllBytes(path);
        Extern extern = new Extern(codeFragments);
        OOIdGenerator ooIdGenerator = new OOIdGenerator();
        CustomOperationsList customOperationsList = new CustomOperationsList();
        Intern intern = new Intern(customOperationsList, codeFragments, ooIdGenerator);
        intern.useOriginalOOId = true;
        Value v = intern.inputValue(new DataInputStream(new ByteArrayInputStream(bytes)));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        extern.outputValue(bos, v);
        byte[] externedBytes = bos.toByteArray();
        assert externedBytes.length == bytes.length;

        int i = 19;
        for (; i < externedBytes.length; i++) {
            if (bytes[i] == 0x0C && externedBytes[i] == 0x0B) {
                i += 8;
                continue;
            }
            if (bytes[i] == Intern.CODE_DOUBLE_ARRAY8_LITTLE && externedBytes[i] == Intern.CODE_DOUBLE_ARRAY8_BIG) {
                int ignoreCount = bytes[i + 1];
                i += ignoreCount * 8 + 1;
                continue;
            }
            if (bytes[i] == Intern.CODE_DOUBLE_ARRAY32_LITTLE && externedBytes[i] == Intern.CODE_DOUBLE_ARRAY32_BIG) {
                int ignoreCount = ByteBuffer.wrap(bytes).getInt(i + 1);
                i += ignoreCount * 8 + 1;
                continue;
            }


            if (externedBytes[i] != bytes[i]) {
                break;
            }
        }

//        System.out.println("Bad index = " + i);
        assertEquals(i, bytes.length);
//        for(int n = i-10; n < i+100; n++) {
//            System.out.println(String.format("%1$02d: %2$02X %3$02X", n, bytes[n], externedBytes[n]));
//        }
//        System.out.flush();
        ByteArrayInputStream bid = new ByteArrayInputStream(externedBytes);
        DataInputStream dis = new DataInputStream(bid);
        Value v2 = intern.inputValue(dis);
        assertTrue(compare.compare(v, v2, true) == 0);

        System.out.println("Done");
    }

    public static Path resourcePath(String name) {
        System.out.println("/interp/marshalling/" + name);
        return new File(ExecutableInterpreterTest.class.getResource("/interp/marshalling/" + name).getFile()).toPath();
    }

}
