package interp;

import interp.customoperations.CustomOperations;
import interp.customoperations.CustomOperationsValue;
import interp.stack.ValueStack;
import interp.value.ObjectValue;
import interp.value.Value;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompareTest {
    @Test
    public void compareLongLong(){
        Compare compare =makeCompare();
        LongValue a = LongValue.wrap(1);
        LongValue b = LongValue.wrap(5);

        assertEquals(-1, compare.compare(a, b, false));
        assertEquals(0, compare.compare(b, b, false));
        assertEquals(1, compare.compare(b, a, false));
    }

    @Test
    public void compareForward(){
        Compare compare = makeCompare();
        LongValue a = LongValue.wrap(1);
        LongValue b = LongValue.wrap(5);

        ObjectValue o1 = new ObjectValue(ValueTag.ForwardTag, new Value[] { a });
        ObjectValue o2 = new ObjectValue(ValueTag.ForwardTag, new Value[] { b });

        assertEquals(-1, compare.compare(a, o2, false));
        assertEquals(-1, compare.compare(o1, b, false));
        assertEquals(-1, compare.compare(o1, o2, false));
    }

    @Test
    public void compareLongValueAndCustomOperationsValueCompareExt(){
        Compare compare = makeCompare();
        LongValue a = LongValue.wrap(1);

        CustomOperations customOperations = new CustomOperations();
        customOperations.compareExt = (Object value1, Object value2) -> 77;
        CustomOperationsValue b
                = new CustomOperationsValue(customOperations, 42);

        assertEquals(77, compare.compare(a, b, false));
        assertEquals(77, compare.compare(b, a, false));

        CustomOperations customOperations2 = new CustomOperations();
        customOperations2.compareExt = (Object value1, Object value2) -> 0;
        CustomOperationsValue c
                = new CustomOperationsValue(customOperations2, 42);

        assertEquals(0, compare.compare(a, c, false));
        assertEquals(0, compare.compare(c, a, false));
    }

    private Compare makeCompare() {
        CamlState camlState = new CamlState();
        return new Compare(camlState);
    }
}
