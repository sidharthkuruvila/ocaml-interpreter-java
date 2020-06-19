package interp.io;

import interp.customoperations.CustomOperationsValue;
import interp.value.ObjectValue;
import interp.value.Value;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static interp.value.ObjectValue.Pair_tag;
import static interp.value.Value.Val_emptylist;

public class ChannelRegistry {
    private final List<CustomOperationsValue> channelList;

    public ChannelRegistry() {
        this(System.in, System.out, System.err);
    }

    public ChannelRegistry(InputStream in, OutputStream out, OutputStream err) {
        channelList =
                new ArrayList<>(Arrays.asList(
                        new CustomOperationsValue(
                                new ChannelCustomOperations(), new InputStreamChannel(in)),
                        new CustomOperationsValue(
                                new ChannelCustomOperations(), new OutputStreamChannel(out)),
                        new CustomOperationsValue(
                                new ChannelCustomOperations(), new OutputStreamChannel(err))));
    }

    public CustomOperationsValue getChannel(int fd) {
        return channelList.get(fd);
    }

    public Value outChannelList() {
        Value res = Val_emptylist;
        for (CustomOperationsValue channelOperationsValue : channelList) {
            if (!(channelOperationsValue.getData() instanceof OutputStreamChannel)) {
                continue;
            }
            ObjectValue o = new ObjectValue(Pair_tag, 2);
            o.setField(0, channelOperationsValue);
            o.setField(1, res);
            res = o;
        }
        return res;
    }

}
