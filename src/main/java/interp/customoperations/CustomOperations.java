package interp.customoperations;

import interp.value.Value;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Comparator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomOperations<T> {
    public String identifier;
    public Consumer<T> finalize;
    public Comparator<T> compare;
    public Function<T, Integer> hash;
    public Function<T, byte[]> serialize;
    public Function<DataInputStream, T> deserialize;
    public Comparator<Value> compareExt;
    public Long customFixedLength;

    public static <V> CustomOperationsValue<V> wrap(CustomOperations<V> co, V t) {
        return new CustomOperationsValue<V>(co, t);
    }

    public static <V> V unwrap(CustomOperationsValue<V> c) {
        return c.getData();
    }
}
