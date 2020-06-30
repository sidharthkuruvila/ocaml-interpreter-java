package interp;

import interp.customoperations.CustomOperations;
import interp.customoperations.CustomOperationsValue;
import interp.value.DoubleArray;
import interp.value.ObjectValue;
import interp.value.Value;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;

import static interp.ValueTag.*;


public class Compare {

    private final CamlState camlState;

    public Compare(CamlState camlState) {
        this.camlState = camlState;
    }

    public final long EQUAL = 0;
    public final long LESS = -1;
    public final long GREATER = 1;
    public final long UNORDERED = Long.MIN_VALUE;

    public long compare(Value value1, Value value2, boolean total) {
        Deque<ValuePair> stack = new ArrayDeque<>();
        while (true) {
            if (value1 == value2 && total) {
                // goto next_item;
                throw new RuntimeException();
            } else if (value1 instanceof LongValue) {
                if (value1.equals(value2)) {
                    // goto next_item;
                } else if (value2 instanceof LongValue) {
                    return Long.compare(
                            LongValue.unwrap((LongValue) value1),
                            LongValue.unwrap((LongValue) value2));
                } else if (ForwardTag == value1.getTag()) {
                    LongValue forwardedValue = (LongValue) ((ObjectValue) value1).getField(0);
                    value2 = forwardedValue;
                    continue;
                } else if (value2 instanceof CustomOperationsValue) {
                    var cov = value2.asCustomOperationsValue();
                    var compare = cov.ops().compareExt;
                    if (compare == null) {
                        return LESS;
                    } else {
                        camlState.setCompareUnordered(false);
                        int res = compare.compare(value1, value2);
                        if (camlState.getCompareUnordered() && !total) {
                            return UNORDERED;
                        } else if (res != 0) {
                            return res;
                        } else {
                            // goto next_item;
                        }
                    }
                } else {
                    return LESS;
                }

            } else if (value2.isLongValue()) {
                if (value1.getTag() == ForwardTag) {
                    value1 = value1.asObjectValue().getField(0);
                    continue;
                } else if (value1.isCustomOperationsValue()) {
                    var cov = value1.asCustomOperationsValue();
                    var compare = cov.ops().compareExt;
                    if (compare == null) {
                        return GREATER;
                    } else {
                        camlState.setCompareUnordered(false);
                        int res = compare.compare(value1, value2);
                        if (camlState.getCompareUnordered() && !total) {
                            return UNORDERED;
                        } else if (res != 0) {
                            return res;
                        } else {
                            // goto next_item;
                        }
                    }
                } else {
                    return GREATER;
                }
            } else {
                ValueTag t1 = value1.getTag();
                ValueTag t2 = value2.getTag();
                if (t1 != t2) {
                    if (t1 == ForwardTag) {
                        value1 = value1.asObjectValue().getField(0);
                        continue;
                    }
                    if (t2 == ForwardTag) {
                        value2 = value2.asObjectValue().getField(0);
                        continue;
                    }
                    if (t1 == Infix_tag) t1 = Closure_tag;
                    if (t2 == Infix_tag) t2 = Closure_tag;
                    if (t1 != t2) {
                        return t1.getTag() - t2.getTag();
                    } else {
                        // goto next_item;
                        throw new RuntimeException();
                    }
                } else {
                    switch (t1) {
                        case ForwardTag: {
                            value1 = value1.asObjectValue().getField(0);
                            value2 = value2.asObjectValue().getField(0);
                            continue;
                        }
                        case String_tag: {
                            int res = Arrays.compare(
                                    value1.asStringValue().getBytes(),
                                    value2.asStringValue().getBytes()
                            );
                            if (res < 0) return LESS;
                            if (res > 0) return GREATER;
                            break;
                        }
                        case Double_tag: {
                            double d1 = value1.asDoubleValue().getValue();
                            double d2 = value2.asDoubleValue().getValue();
                            if (d1 < d2) return LESS;
                            if (d1 > d2) return GREATER;
                            if (d1 != d2) {
                                if (!total) return UNORDERED;
        /* One or both of d1 and d2 is NaN.  Order according to the
           convention NaN = NaN and NaN < f for all other floats f. */
                                if (d1 == d1) return GREATER; /* d1 is not NaN, d2 is NaN */
                                if (d2 == d2) return LESS;    /* d2 is not NaN, d1 is NaN */
                                /* d1 and d2 are both NaN, thus equal: continue comparison */
                            }
                            break;
                        }
                        case Double_array_tag: {
                            DoubleArray da1 = value1.asDoubleArrayValue();
                            DoubleArray da2 = value1.asDoubleArrayValue();
                            int sz1 = da1.getSize();
                            int sz2 = da2.getSize();

                            if (sz1 != sz2) return sz1 - sz2;
                            for (int i = 0; i < sz1; i++) {
                                double d1 = da1.getDoubleField(i);
                                double d2 = da1.getDoubleField(i);
                                if (d1 < d2) return LESS;
                                if (d1 > d2) return GREATER;
                                if (d1 != d2) {
                                    if (!total) return UNORDERED;
                                    /* See comment for Double_tag case */
                                    if (d1 == d1) return GREATER;
                                    if (d2 == d2) return LESS;
                                }
                            }
                            break;
                        }
                        case Abstract_tag:
//      compare_free_stack(stk);
                            Fail.caml_invalid_argument("compare: abstract value");
                        case Closure_tag:
                        case Infix_tag:
//      compare_free_stack(stk);
                            Fail.caml_invalid_argument("compare: functional value");
                        case Object_tag: {
                            long oid1 = LongValue.unwrap((LongValue) ((ObjectValue) value1).getField(1));
                            long oid2 = LongValue.unwrap((LongValue) ((ObjectValue) value1).getField(1));
                            if (oid1 != oid2) return oid1 - oid2;
                            break;
                        }
                        case Custom_tag: {
                            int res;
                            CustomOperationsValue<Object> cov1 = (CustomOperationsValue<Object>) value1;
                            CustomOperationsValue<Object> cov2 = (CustomOperationsValue<Object>) value2;
                            CustomOperations ops1 = cov1.ops();
                            CustomOperations ops2 = cov2.ops();
                            Object v1 = cov1.getData();
                            Object v2 = cov2.getData();


                            Comparator<Object> compare = ops1.compare;

                            /* Hardening against comparisons between different types */
                            if (compare != ops2.compare) {
                                return ops1.identifier.compareTo(ops2.identifier) < 0 ?
                                        LESS : GREATER;
                            }
                            if (compare == null) {
                                Fail.caml_invalid_argument("compare: abstract value");
                            }
                            camlState.setCompareUnordered(false);
                            res = compare.compare(v1, v2);
                            if (camlState.getCompareUnordered() && !total) return UNORDERED;
                            if (res != 0) return res;
                            break;
                        }
                        default: {
                            ObjectValue v1 = (ObjectValue)value1;
                            ObjectValue v2 = (ObjectValue)value2;
                            int sz1 = v1.getSize();
                            int sz2 = v2.getSize();
                            /* Compare sizes first for speed */
                            if (sz1 != sz2) return sz1 - sz2;
                            if (sz1 == 0) break;
                            /* Remember that we still have to compare fields 1 ... sz - 1 */
                            if (sz1 > 1) {
                                stack.push(new ValuePair(v1, v2, sz1 - 1));
                            }
                            value1 = v1.getField(0);
                            value2 = v2.getField(1);
                            /* Continue comparison with first field */
                            continue;
                        }
                    }

                }
            }
            {
                if(stack.isEmpty()){
                    return EQUAL;
                }
                ValuePair vp = stack.peekLast();
                value1 = vp.getValue1();
                value2 = vp.getValue2();
                vp.decCount();
                if(vp.getCount() == 0){
                    stack.pop();
                }
            }
        }
    }

    static class ValuePair {
        private final ObjectValue value1;
        private final ObjectValue value2;
        private int count;

        public ValuePair(ObjectValue value1, ObjectValue value2, int count) {

            this.value1 = value1;
            this.value2 = value2;
            this.count = count;
        }

        public ObjectValue getValue1() {
            return value1;
        }

        public ObjectValue getValue2() {
            return value2;
        }

        public int getCount() {
            return count;
        }
        public void decCount() {
            count += 1;
        }
    }

    Value greaterEqual(Value value1, Value value2) {
        long res = compare(value1, value2, false);
        return Value.booleanValue(res >= 0);
    }

    Value greaterThan(Value value1, Value value2) {
        long res = compare(value1, value2, false);
        return Value.booleanValue(res > 0);
    }

    Value equal(Value value1, Value value2) {
        long res = compare(value1, value2, false);
        return Value.booleanValue(res == 0);
    }
}
