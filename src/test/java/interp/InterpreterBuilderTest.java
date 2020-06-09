package interp;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.ByteBuffer;

public class InterpreterBuilderTest {
    @Test void fourBytesToLongLEParsesANumber() {
        byte[] ba  = new byte[] { 0x00, 0x00, 0x00, (byte)0xf0 };
        ByteBuffer bb = ByteBuffer.wrap(ba);
        System.out.println(0xff & ba[3]);
        long res = Util.getUint32(bb, 0);
        assertEquals(15*16, res);
    }
}
