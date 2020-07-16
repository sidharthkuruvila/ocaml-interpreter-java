package interp.primitives;

import interp.InterpreterContext;
import interp.value.Value;
import interp.customoperations.CustomOperationsValue;
import interp.io.Channel;

import static interp.Interpreter.valUnit;

public class MlFlush implements Primitive {
    @Override
    public Value call(InterpreterContext context, Value[] values) {
        try {
            CustomOperationsValue v = (CustomOperationsValue) values[0];
            Channel ch = (Channel) v.getData();
            ch.flush();
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
