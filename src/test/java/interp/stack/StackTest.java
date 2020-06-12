package interp.stack;
import static org.junit.jupiter.api.Assertions.*;
import interp.LongValue;
import interp.value.Value;
import org.junit.jupiter.api.Test;

public class StackTest {

    @Test
    public void test1(){
        Value v1 = new LongValue(1);
        Value v2 = new LongValue(2);
        Value v3 = new LongValue(3);
        Value v4 = new LongValue(4);
        ValueStack stack = new ValueStack();
        stack.push(v1);
        stack.push(v2);
        stack.push(v3);
        assertEquals(v1, stack.get(2));
        assertEquals(v2, stack.get(1));
        assertEquals(v3, stack.get(0));
        stack.popNIgnore(2);
        assertEquals(v1, stack.get(0));
    }
}
