package interp;

import interp.customoperations.CustomOperationsValue;
import interp.value.ObjectValue;
import interp.value.Value;

import java.util.Comparator;

import static interp.ValueTag.ForwardTag;

public class Compare {

    private final CamlState camlState;

    public Compare(CamlState camlState) {
        this.camlState = camlState;
    }

    private final LongValue LESS = LongValue.wrap(-1);
    private final LongValue GREATER = LongValue.wrap(1);
    private final LongValue UNORDERED = LongValue.wrap(Long.MIN_VALUE);

    public LongValue compare(Value value1, Value value2, boolean total) {
        while(true) {
            if(value1 == value2 && total) {
                // goto next_item;
                throw new RuntimeException();
            } else if(value1 instanceof LongValue) {
                if(value1.equals(value2)){
                    // goto next_item;
                    throw new RuntimeException();
                } else if(value2 instanceof LongValue) {
                    return LongValue.wrap(Long.compare(
                            LongValue.unwrap((LongValue)value1),
                            LongValue.unwrap((LongValue)value2)));
                }else if(ForwardTag == value1.getTag()){
                    LongValue forwardedValue = (LongValue) ((ObjectValue)value1).getField(0);
                    value2 = forwardedValue;
                    continue;
                } else if (value2 instanceof CustomOperationsValue){
                    var cov = value2.asCustomOperationsValue();
                    var compare = cov.ops().compareExt;
                    if(compare == null) {
                        return LESS;
                    } else {
                        camlState.setCompareUnordered(false);
                        int res = compare.compare(value1, value2);
                        if(camlState.getCompareUnordered() && !total) {
                            return UNORDERED;
                        } else if(res != 0) {
                            return LongValue.wrap(res);
                        } else {
                            // goto next_item;
                            throw new RuntimeException();
                        }
                    }
                } else {
                    return LESS;
                }

            } else if (value2.isLongValue()){
                if(value1.getTag() == ForwardTag) {
                    value1 = value1.asObjectValue().getField(0);
                } else if(value1.isCustomOperationsValue()){
                    var cov = value1.asCustomOperationsValue();
                    var compare = cov.ops().compareExt;
                    if(compare == null) {
                        return GREATER;
                    } else {
                        camlState.setCompareUnordered(false);
                        int res = compare.compare(value1, value2);
                        if(camlState.getCompareUnordered() && !total) {
                            return UNORDERED;
                        } else if(res != 0) {
                            return LongValue.wrap(res);
                        } else {
                            // goto next_item;
                            throw new RuntimeException();
                        }
                    }
                } else {
                    return GREATER;
                }
            }
        }
    }
}
