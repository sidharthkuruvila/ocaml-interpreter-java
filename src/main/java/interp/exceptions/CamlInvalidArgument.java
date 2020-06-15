package interp.exceptions;

public class CamlInvalidArgument extends RuntimeException {
    public CamlInvalidArgument(String s) {
        super(s);
    }
}
