package interp;

import interp.value.DoubleArray;
import interp.value.ObjectValue;
import interp.value.Value;

import java.util.Arrays;

import static interp.Interpreter.valUnit;


public class Dummy {
    public static double[] allocDouble(int size) {
        return new double[size];
    }

    public static Value update(Value dummyValue, Value newValValue) {
        if(newValValue instanceof DoubleArray) {
            DoubleArray dummy = (DoubleArray) dummyValue;
            DoubleArray newVal = (DoubleArray) newValValue;
            newVal.blitTo(0, dummy, 0, newVal.getSize());
        } else if(newValValue instanceof ObjectValue) {
            ObjectValue dummy = (ObjectValue) dummyValue;
            ObjectValue newVal = (ObjectValue) newValValue;
            if(newVal.getTag() == ValueTag.Infix_tag ) {
                assert dummy.getTag() == ValueTag.Infix_tag;
                newVal.blitTo(0, dummy, 0, dummy.getSize());
            } else {
                assert dummy.getTag() != ValueTag.Infix_tag;
                dummy.setTag(newVal.getTag());
                newVal.blitTo(0, dummy, 0, dummy.getSize());
            }
        } else {
            throw new RuntimeException("Failed to match");
        }
        return valUnit;
    }
}
