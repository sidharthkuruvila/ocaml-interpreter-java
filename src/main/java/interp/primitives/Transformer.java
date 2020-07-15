package interp.primitives;

import interp.LongValue;
import interp.value.DoubleArray;
import interp.value.Value;

public interface Transformer<T> {
    Value wrap(T v);
    T unwrap(Value v);

    Transformer intValue = new IntValueTranformer();
    Transformer doubleArray = new DoubleArrayTransformer();
    Transformer identity = new IdentityTransformer();
};

class IdentityTransformer implements Transformer<Value> {
    public Value wrap(Value v) {
        return v;
    }
    public Value unwrap(Value v) {
        return v;
    }
}


class IntValueTranformer implements Transformer<Integer> {
    public Value wrap(Integer v) {
        return LongValue.wrap(v);
    }
    public Integer unwrap(Value v) {
        return LongValue.unwrapInt((LongValue) v);
    }
}

class DoubleArrayTransformer implements Transformer<double[]> {

    @Override
    public Value wrap(double[] v) {
        return DoubleArray.wrap(v);
    }

    @Override
    public double[] unwrap(Value v) {
        return DoubleArray.unwrap((DoubleArray)v);
    }
}
