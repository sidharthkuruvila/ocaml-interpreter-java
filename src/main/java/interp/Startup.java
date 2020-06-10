package interp;

import javax.naming.Name;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static interp.Interpreter.valUnit;

class OOIdGenerator {
    int id = 0;

    int nextId() {
        id += 1;
        return id;
    }
}

class NamedValues {
    Map<String, Value> map = new HashMap<>();
    public void add(String name, Value value) {
        map.put(name, value);
    }
}

class RegisterNamedValuePrimitive implements Primitive {

    private final NamedValues namedValues;

    public RegisterNamedValuePrimitive(NamedValues namedValues) {
        this.namedValues = namedValues;
    }

    @Override
    public Value call(Value[] values) {
        String name = ((StringValue)values[0]).getString();
        Value value = values[1];

        return valUnit;
    }

    @Override
    public String getName() {
        return "caml_register_named_value";
    }
}

class FreshOOIdPrimitive implements Primitive {
    private final OOIdGenerator ooIdGenerator;

    public FreshOOIdPrimitive(OOIdGenerator ooIdGenerator) {
        this.ooIdGenerator = ooIdGenerator;
    }

    @Override
    public Value call(Value[] values) {
        return new LongValue(ooIdGenerator.nextId());
    }

    @Override
    public String getName() {
        return "caml_fresh_oo_id";

    }
}

class Int64FloatOfBits implements Primitive{

    @Override
    public Value call(Value[] values) {
        CustomOperationsValue customOperationsValue = (CustomOperationsValue)values[0];
        return new DoubleValue((long)customOperationsValue.getData());
    }

    @Override
    public String getName() {
        return "caml_int64_float_of_bits";
    }
}

class MlOpenDescriptorIn implements Primitive {

    @Override
    public Value call(Value[] values) {
        int fd = ((LongValue)values[0]).getIntValue();
        return new CustomOperationsValue(new ChannelCustomOperations(), new Channel(fd));
    }

    @Override
    public String getName() {
        return "caml_ml_open_descriptor_in";
    }
}

class MlOpenDescriptorOut implements Primitive {

    @Override
    public Value call(Value[] values) {
        int fd = ((LongValue)values[0]).getIntValue();
        return new CustomOperationsValue(new ChannelCustomOperations(), new Channel(fd));
    }

    @Override
    public String getName() {
        return "caml_ml_open_descriptor_out";
    }
}




public class Startup {


    public static void main(String[] args) throws IOException {
        try {
            OOIdGenerator ooIdGenerator = new OOIdGenerator();
            CustomOperationsList customOperationsList = new CustomOperationsList();
            CodeFragmentTable codeFragmentTable = new CodeFragmentTable();
            Intern intern = new Intern(customOperationsList, codeFragmentTable, ooIdGenerator);
            ExecutableBuilder exb = new ExecutableBuilder(codeFragmentTable, intern);
            PrimitiveRegistry primitiveRegistry = new PrimitiveRegistry();

            Path path = Path.of("/Users/sidharthkuruvila/src/ocaml/playground/a.out");
            FileChannel fc = FileChannel.open(path);
            Executable e = exb.fromExe(fc);
            NamedValues namedValues = new NamedValues();

            primitiveRegistry.addPrimitive(new RegisterNamedValuePrimitive(namedValues));
            primitiveRegistry.addPrimitive(new FreshOOIdPrimitive(ooIdGenerator));
            primitiveRegistry.addPrimitive(new Int64FloatOfBits());
            primitiveRegistry.addPrimitive(new MlOpenDescriptorIn());
            primitiveRegistry.addPrimitive(new MlOpenDescriptorOut());


            Primitives primitives = primitiveRegistry.getPrimitives(e.getPrims());
            Interpreter interpreter = new Interpreter(e.getGlobalData(), primitives);
            HexPrinter.printBytes(e.getCodeFragment().code);
            interpreter.interpret(e.getCodeFragment().code);
            System.out.println(e);
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("We shouldsee more");
//            throw new RuntimeException(e);
        }
    }
}
