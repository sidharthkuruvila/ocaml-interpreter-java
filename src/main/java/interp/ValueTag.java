package interp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ValueTag {
    ForwardTag(250),
    PAIR_TAG(0),
    Object_tag(248),
    Closure_tag(247);

    private final int tag;

    ValueTag(int tag) {
        this.tag = tag;
    }

    public static ValueTag of(int i) {
        return mapping.get(i);
    }

    public int getTag() {
        return tag;
    }

    private static final Map<Integer, ValueTag> mapping = Arrays.stream(ValueTag.values())
            .collect(Collectors.toMap(ValueTag::getTag, Function.identity()));
}
