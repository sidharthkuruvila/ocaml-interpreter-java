package interp.primitives;

import interp.LongValue;
import interp.Value;
import interp.customoperations.CustomOperationsValue;
import interp.io.Channel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static interp.Interpreter.valUnit;

public class MlOutputCharPrimitive implements Primitive {
    @Override
    public Value call(Value[] values) {
        try {
            CustomOperationsValue v = (CustomOperationsValue) values[0];
            Channel ch = (Channel) v.getData();
            int b = ((LongValue) values[1]).getIntValue();
            ByteBuffer bb = ByteBuffer.wrap(new byte[]{(byte) b});
            ch.getFileChannel().write(bb);
        } catch(IOException e){
            throw new RuntimeException(e);
        }
        return valUnit;
    }

    @Override
    public String getName() {
        return "caml_ml_output_char";
    }
}
