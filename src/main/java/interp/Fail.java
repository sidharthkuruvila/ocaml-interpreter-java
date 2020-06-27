package interp;

import interp.value.StringValue;

public class Fail {
    public static int FAILURE_EXN  = 2;          /* "Failure" */
    public static void caml_invalid_argument(String s) {
        throw new RuntimeException(s);
    }

    public static void caml_raise_zero_divide() {
        throw new RuntimeException("Divide by zero error");
    }

    public static OcamlInterpreterException failWithException(String message) {
        return new OcamlInterpreterException(FAILURE_EXN, StringValue.ofString(message));
    }
}
