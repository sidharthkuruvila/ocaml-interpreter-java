package interp;

import interp.customoperations.CustomOperationsList;
import interp.io.ChannelRegistry;
import interp.primitives.*;
import interp.value.BaseArrayValue;
import interp.value.DoubleValue;
import interp.value.StringValue;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class ExecutableFileInterpreter {
    private final ExecutableBuilder exb;
    private final PrimitiveRegistry primitiveRegistry;


    public ExecutableFileInterpreter() throws IOException {
        this(new ChannelRegistry());
    }

    public ExecutableFileInterpreter(ChannelRegistry channelRegistry) throws IOException {
        OOIdGenerator ooIdGenerator = new OOIdGenerator();
        CustomOperationsList customOperationsList = new CustomOperationsList();
        CodeFragmentTable codeFragmentTable = new CodeFragmentTable();
        Intern intern = new Intern(customOperationsList, codeFragmentTable, ooIdGenerator);
        exb = new ExecutableBuilder(codeFragmentTable, intern);
        primitiveRegistry = new PrimitiveRegistry();

        NamedValues namedValues = new NamedValues();

        primitiveRegistry.addPrimitive(new RegisterNamedValuePrimitive(namedValues));
        primitiveRegistry.addPrimitive(new FreshOOIdPrimitive(ooIdGenerator));
        primitiveRegistry.addPrimitive(new Int64FloatOfBitsPrimitive());
        primitiveRegistry.addPrimitive(new MlOpenDescriptorIn(channelRegistry));
        primitiveRegistry.addPrimitive(new MlOpenDescriptorOut(channelRegistry));
        primitiveRegistry.addPrimitive(new MlOutChannelsList(channelRegistry));
        primitiveRegistry.addPrimitive(new MlOutputCharPrimitive());
        primitiveRegistry.addPrimitive(new MlFlush());
        primitiveRegistry.addPrimitive(new MlStringLength());
        primitiveRegistry.addPrimitive(new MlOutput());
        primitiveRegistry.addPrimitive(new SysExecutableName());
        primitiveRegistry.addPrimitive(new SysGetConfig());
        primitiveRegistry.addPrimitive(new SysConstBackendType());
        primitiveRegistry.addPrimitive(new SysConstBigEndian());
        primitiveRegistry.addFunc0("caml_sys_const_word_size", Sys::sysConstWordSize);
        primitiveRegistry.addFunc0("caml_sys_const_int_size", Sys::constIntSize);
        primitiveRegistry.addFunc0("caml_sys_const_ostype_unix", Sys::constOsTypeUnix);
        primitiveRegistry.addFunc0("caml_sys_const_ostype_win32", Sys::constOsTypeWin32);
        primitiveRegistry.addFunc0("caml_sys_const_ostype_cygwin", Sys::constOsTypeCygwin);
        primitiveRegistry.addFunc0("caml_sys_const_max_wosize", Sys::constOsMaxWoSize);
        primitiveRegistry.addFunc1("caml_create_bytes", StringValue::createBytes);
        primitiveRegistry.addFunc1("caml_int_of_string", LongValue::parseString);
        primitiveRegistry.addFunc1("caml_int64_of_int", LongValue::ofInt);
        primitiveRegistry.addFunc2("caml_nativeint_shift_left", LongValue::lsl2);
        primitiveRegistry.addFunc2("caml_format_int", LongValue::format);
        primitiveRegistry.addFunc2("caml_add_float", DoubleValue::add);
        primitiveRegistry.addFunc2("caml_format_float", DoubleValue::format);
        primitiveRegistry.addFunc2("caml_string_get", StringValue::getByteValue);
        primitiveRegistry.addFunc1("caml_obj_dup", BaseArrayValue::duplicateArray);
        primitiveRegistry.addFunc2("caml_array_unsafe_get", BaseArrayValue::unsafeGet);
        primitiveRegistry.addFunc3("caml_array_unsafe_set", BaseArrayValue::unsafeSet);
        primitiveRegistry.addFunc2("caml_mul_float", DoubleValue::mul);
        primitiveRegistry.addFunc2("caml_make_vect", BaseArrayValue::makeVect);



    }

    public void execute(Path executable) throws IOException {
        Path path = executable; //Path.of("/Users/sidharthkuruvila/CLionProjects/ocaml/ocamlc");
        FileChannel fc = FileChannel.open(path);
        Executable e = exb.fromExe(fc);
        Primitives primitives = primitiveRegistry.getPrimitives(e.getPrims());
        Interpreter interpreter = new Interpreter(e.getGlobalData(), primitives);
//            HexPrinter.printBytes(e.getCodeFragment().code);

        interpreter.interpret(e.getCodeFragment().code);
    }

}
