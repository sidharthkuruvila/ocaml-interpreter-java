package interp.primitives;

import interp.InterpreterContext;
import interp.LongValue;
import interp.customoperations.CustomOperationsValue;
import interp.io.Channel;
import interp.value.StringValue;
import interp.value.Value;

import static interp.Interpreter.valUnit;

public class MlOutput implements Primitive {
    @Override
    public Value call(InterpreterContext context, Value[] values) {
        CustomOperationsValue customOperationsValue = (CustomOperationsValue)values[0];

        StringValue stringValue = (StringValue)values[1];
        LongValue offsetValue = (LongValue)values[2];
        LongValue lengthValue = (LongValue)values[3];
        Channel channel = (Channel)customOperationsValue.getData();
        byte[] bytes = stringValue.getBytes();
        int offset = offsetValue.getIntValue();
        int length = lengthValue.getIntValue();
        channel.writeBytes(bytes, offset, length);
        return valUnit;
    }

    @Override
    public String getName() {
        return "caml_ml_output";
    }
}
