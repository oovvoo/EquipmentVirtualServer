package kr.secretcode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class VirtualDataHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ctx.writeAndFlush(System.lineSeparator());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /**
         * 랜덤 데이터 생성 후 리턴 이때 데이터는 라인브래이킹을 통해 구분 한다고 가정한다.
         * 클라이언트로부터 특정 Command가 들어오면 응답한다.
         * StringDecoder를 통해 Byte Stream을 String으로 디코딩 하여 수신 하며,
         * StringEncoder를 통해 String 데이터를 Byte로 인코딩 하여 송신
         */

        if(msg.toString().equalsIgnoreCase("get")){
            ctx.writeAndFlush(createRandomData());
            ctx.close();
        }if(msg.toString().equalsIgnoreCase("q!")){
            ctx.close();
        }

    }

    private static String createRandomData() {
        List<String> list = new Random().ints(new Random().ints(0,10).findAny().getAsInt()
                , 0,999999999).mapToObj(i -> String.format("TEST DATA-%010d",i)).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for(String s : list){
            sb.append(s);
            sb.append(System.lineSeparator());
        }
        return sb.toString();
    }

}
