package interp;

import interp.value.Value;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValueTag {

    public static final int ForwardTag = 250;
    public static final int PAIR_TAG = 0;
    public static final int Object_tag = 248;
    public static final int Closure_tag = 247;
    public static final int String_tag = 252;
    public static final int Double_tag = 253;
    public static final int Double_array_tag = 254;
    public static final int Custom_tag = 255;
    public static final int Infix_tag = 249;
    public static final int Abstract_tag = 251;
    public static final int Forward_tag = 250;
    public static final int Some_tag = 0;

//    ValueTag(int tag) {
//        this.tag = tag;
//    }

    public static int of(int i) {
        return i;
    }

//    public int getTag() {
//        return tag;
//    }

//    public static List<ValueTag> values() {
//        return values;
//    }
//
//    private static final Map<Integer, ValueTag> mapping = ValueTag.values()
//            .stream()
//            .collect(Collectors.toMap(ValueTag::getTag, Function.identity()));
}
