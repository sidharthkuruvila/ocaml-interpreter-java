package interp.customoperations;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Function;

public class CustomOperations<T> {
    public String identifier;
    public Consumer<T> finalize;
    public Comparator<T> compare;
    public Function<T, Integer> hash;
    public Function<T, DataOutputStream> serialize;
    public Function<DataInputStream, T> deserialize;
    public Comparator<T> compareExt;
    public Long customFixedLength;
}
