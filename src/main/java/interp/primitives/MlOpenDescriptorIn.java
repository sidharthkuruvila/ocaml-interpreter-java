package interp.primitives;

import interp.LongValue;
import interp.value.Value;
import interp.io.ChannelRegistry;

public class MlOpenDescriptorIn implements Primitive {

    private final ChannelRegistry channelRegistry;

    public MlOpenDescriptorIn(ChannelRegistry channelRegistry) {

        this.channelRegistry = channelRegistry;
    }

    @Override
    public Value call(Value[] values) {
        int fd = ((LongValue)values[0]).getIntValue();
        return channelRegistry.getChannel(fd);
    }

    @Override
    public String getName() {
        return "caml_ml_open_descriptor_in";
    }
}
