package interp.primitives;

import interp.LongValue;
import interp.Value;
import interp.customoperations.CustomOperationsValue;
import interp.io.Channel;

import java.io.IOException;
import java.nio.ByteBuffer;

import static interp.Interpreter.valUnit;

public class MlFlush implements Primitive {
    @Override
    public Value call(Value[] values) {
        try {
            CustomOperationsValue v = (CustomOperationsValue) values[0];
            Channel ch = (Channel) v.getData();
//            ch.getFileChannel().force(true);
        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return valUnit;
    }

    @Override
    public String getName() {
        return "caml_ml_flush";
    }
}
