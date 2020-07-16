package interp.primitives;

import interp.InterpreterContext;
import interp.functions.Func3;
import interp.value.Value;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PrimitiveRegistry {

    private final Map<String, Primitive> primitives = new HashMap<>();

    public Primitives getPrimitives(List<String> names) {
        return new Primitives(names.stream()
                .map((String name) -> primitives.getOrDefault(name, new UnimplementedPrimitive(name)))
                .collect(Collectors.toList()));
    }

    public void addPrimitive(Primitive primitive) {
        primitives.put(primitive.getName(), primitive);
    }

    public void addFunc0(String name, Supplier<Value> fn){
        primitives.put(name, new Func0Primitive(name, fn));
    }

    public void addFunc0Ctx(String name, Function<InterpreterContext, Value> fn) {
        primitives.put(name, new Func0CtxPrimitive(name, fn));
    }

    public <T extends Value> void addFunc1(String name, Function<T, Value> fn){
        primitives.put(name, new Func1Primitive<>(name, fn));
    }
    public <T extends Value> void addFunc1Ctx(String name, BiFunction<InterpreterContext, T, Value> fn) {
        primitives.put(name, new Func1CtxPrimitive<>(name, fn));
    }
    public <P1, R> void addFunc1(String name, Transformer<P1> param1, Transformer<R> returns, Function<P1, R> fn){
        primitives.put(name, new Primitive() {
            @Override
            public Value call(InterpreterContext context, Value[] values) {
                return returns.wrap(fn.apply(param1.unwrap(values[0])));
            }

            @Override
            public String getName() {
                return name;
            }
        });
    }

    public <T extends Value, U extends Value> void addFunc2(String name, BiFunction<T, U, Value> fn){
        primitives.put(name, new Func2Primitive<>(name, fn));
    }
    public <T extends Value, U extends Value, V extends Value>void addFunc3(String name, Func3<T, U, V, Value> fn){
        primitives.put(name, new Func3Primitive<>(name, fn));
    }
    public void unimplemented(String name) {
        primitives.put(name, new UnimplementedPrimitive(name));
    }

    public void addFuncN(String name, Function<Value[], Value> fn) {
        primitives.put(name, new FuncNPrimitive(name, fn));
    }
}
