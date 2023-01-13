package kr.secretcode;

import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class SampleCodec extends CombinedChannelDuplexHandler<StringDecoder, StringEncoder> {
    public SampleCodec() {
        super(new StringDecoder(), new StringEncoder());
    }
}
