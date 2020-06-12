package interp.primitives;

import interp.value.Value;
import interp.io.ChannelRegistry;

public class MlOutChannelsList implements Primitive {
    private final ChannelRegistry channelRegistry;

    public MlOutChannelsList(ChannelRegistry channelRegistry) {
        this.channelRegistry = channelRegistry;
    }

    @Override
    public Value call(Value[] values) {
        return channelRegistry.outChannelList();
    }

    @Override
    public String getName() {
        return "caml_ml_out_channels_list";
    }
}
