package interp;

import interp.value.Value;

import static interp.Interpreter.valTrue;
import static interp.value.Value.*;

public class Sys {
    private static int valueSize = 8;
    private static String osType = "Unix";
    private static long maxWoSize = (1L<<31);

    public static Value sysConstWordSize() {
        return new LongValue(8*valueSize);
    }

    public static Value constIntSize() {
        return new LongValue(8 * valueSize - 1) ;
    }

    public static Value constOsTypeUnix() {
        return booleanValue(osType.equals("Unix"));
    }

    public static String getOsType() {
        return osType;
    }

    public static Value constOsTypeWin32() {
        return booleanValue(osType.equals("Win32"));
    }

    public static Value constOsTypeCygwin() {
        return booleanValue(osType.equals("Cygwin"));
    }

    public static Value constOsMaxWoSize() {
        return new LongValue(maxWoSize);
    }

    public static long getMaxWoSize() {
        return maxWoSize;
    }
}
