package interp.primitives;

import interp.InterpreterContext;
import interp.LongValue;
import interp.value.Value;
import interp.customoperations.CustomOperationsValue;
import interp.io.Channel;

import java.nio.ByteBuffer;

import static interp.Interpreter.valUnit;

public class MlOutputCharPrimitive implements Primitive {
    @Override
    public Value call(InterpreterContext context, Value[] values) {
        CustomOperationsValue v = (CustomOperationsValue) values[0];
        Channel ch = (Channel) v.getData();
        int b = ((LongValue) values[1]).getIntValue();
        ByteBuffer bb = ByteBuffer.wrap(new byte[]{(byte) b});
        ch.writeByte(b);
        return valUnit;
    }

    @Override
    public String getName() {
        return "caml_ml_output_char";
    }
}
