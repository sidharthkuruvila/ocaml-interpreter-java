package interp;

import interp.value.ObjectValue;
import interp.value.Value;

public class OcamlInterpreterException extends RuntimeException {
    private final Value tag;
    private final Value value;
    private final int tagIndex;

    public OcamlInterpreterException(Value tag, Value value){
        this.tag = tag;
        this.value = value;
        tagIndex = -1;
    }

    public OcamlInterpreterException(int tagIndex, Value value){
        this.tag = null;
        this.value = value;
        this.tagIndex = tagIndex;
    }

    public Value getBucket(ObjectValue globalData) {
        Value tagValue = tag;
        if (tagValue == null) {
            tagValue = globalData.getField(tagIndex);
        }
        ObjectValue bucket = new ObjectValue(ValueTag.PAIR_TAG, 2);
        bucket.setField(0, tagValue);
        bucket.setField(1, value);
        return bucket;
    }
}
