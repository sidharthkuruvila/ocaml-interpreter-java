package interp.io;

import interp.customoperations.CustomOperationsValue;
import interp.value.ObjectValue;
import interp.value.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static interp.value.ObjectValue.Pair_tag;
import static interp.value.Value.Val_emptylist;

public class ChannelRegistry {
    private final List<CustomOperationsValue> channelList =
            new ArrayList<>(Arrays.asList(new CustomOperationsValue[]{
                    new CustomOperationsValue(
                            new ChannelCustomOperations(), new OutputStreamChannel(System.out)),
                    new CustomOperationsValue(
                            new ChannelCustomOperations(), new OutputStreamChannel(System.out)),
                    new CustomOperationsValue(
                            new ChannelCustomOperations(), new OutputStreamChannel(System.err))
            }));

    public CustomOperationsValue getChannel(int fd) {
        return channelList.get(fd);
    }

    public Value outChannelList() {
        Value res = Val_emptylist;
        for (CustomOperationsValue channel : channelList) {
            ObjectValue o = new ObjectValue(Pair_tag, 2);
            o.setField(0, channel);
            o.setField(1, res);
            res = o;
        }
        return res;
    }

}
