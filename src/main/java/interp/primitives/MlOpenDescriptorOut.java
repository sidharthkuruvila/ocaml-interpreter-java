package interp.primitives;

import interp.InterpreterContext;
import interp.LongValue;
import interp.value.Value;
import interp.io.ChannelRegistry;

public class MlOpenDescriptorOut implements Primitive {

    private final ChannelRegistry channelRegistry;

    public  MlOpenDescriptorOut(ChannelRegistry channelRegistry) {
        this.channelRegistry = channelRegistry;
    }

    @Override
    public Value call(InterpreterContext context, Value[] values) {
        int fd = ((LongValue)values[0]).getIntValue();
        return channelRegistry.getChannel(fd);
    }

    @Override
    public String getName() {
        return "caml_ml_open_descriptor_out";
    }
}
