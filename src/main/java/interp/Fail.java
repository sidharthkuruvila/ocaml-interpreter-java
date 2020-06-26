package interp;

public class Fail {
    public static void caml_invalid_argument(String s) {
        throw new RuntimeException(s);
    }

    public static void caml_raise_zero_divide() {
        throw new RuntimeException("Divide by zero error");
    }
}
