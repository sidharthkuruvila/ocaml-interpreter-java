package interp.primitives;

import interp.LongValue;
import interp.Value;
import interp.customoperations.CustomOperationsValue;
import interp.io.Channel;
import interp.io.ChannelCustomOperations;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class MlOpenDescriptorOut implements Primitive {

    @Override
    public Value call(Value[] values) {
        int fd = ((LongValue)values[0]).getIntValue();
        if(fd == 1) {
            return new CustomOperationsValue(new ChannelCustomOperations(), new Channel(new FileOutputStream(FileDescriptor.out).getChannel()));
        } else if(fd == 2) {
            return new CustomOperationsValue(new ChannelCustomOperations(), new Channel(new FileOutputStream(FileDescriptor.err).getChannel()));
        } else {
            throw new RuntimeException("Unknown descriptor");
        }

    }

    @Override
    public String getName() {
        return "caml_ml_open_descriptor_out";
    }
}
