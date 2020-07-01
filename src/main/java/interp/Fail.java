package interp;

import interp.value.StringValue;
import interp.value.Value;

public class Fail {
    public static int SYS_ERROR_EXN = 1;         /* "Sys_error" */
    public static int FAILURE_EXN  = 2;          /* "Failure" */
    public static int NOT_FOUND_EXN = 6;         /* "Not_found" */
    public static void caml_invalid_argument(String s) {
        throw new RuntimeException(s);
    }

    public static void caml_raise_zero_divide() {
        throw new RuntimeException("Divide by zero error");
    }

    public static OcamlInterpreterException failWithException(String message) {
        return new OcamlInterpreterException(FAILURE_EXN, StringValue.ofString(message));
    }

    public static OcamlInterpreterException notFoundException() {
        return new OcamlInterpreterException(NOT_FOUND_EXN);
    }

    public static OcamlInterpreterException caml_sys_error(Value value) {
        return new OcamlInterpreterException(FAILURE_EXN, value);
    }
}
