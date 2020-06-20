package interp.functions;

@FunctionalInterface
public interface Func3<T, U, V, R> {
    R apply(T var1, U var2, V var3);
}
