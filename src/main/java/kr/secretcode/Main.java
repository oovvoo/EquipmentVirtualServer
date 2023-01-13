package kr.secretcode;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        if (args.length > 0) {
            try {

                if (args.length == 1) {
                    int startPort = Integer.parseInt(args[0]);
                    startServer(startPort, startPort);
                } else if (args.length == 2) {
                    int startPort = Integer.parseInt(args[0]);
                    int endPort = Integer.parseInt(args[1]);
                    startServer(startPort, endPort);
                }

            } catch (NumberFormatException e) {
                System.out.println("invalid argument exception" + System.lineSeparator() + "Start Default Port 7070 Only One Server");

            }
        }


    }

    private static void startServer(int startPort, int endPort) throws InterruptedException {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .channel(NioServerSocketChannel.class)
                .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .option(ChannelOption.SO_BACKLOG, 200)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new LineBasedFrameDecoder(3)
                                , new SampleCodec()
                                , new VirtualDataHandler()
                        );
                    }
                });

        List<Channel> channelList = new ArrayList<>();

        IntStream.rangeClosed(startPort, endPort).forEach(port -> {
            try {
                Channel channel = bootstrap.bind(new InetSocketAddress(port)).sync().channel();
                channelList.add(channel);
            } catch (InterruptedException e) {
                System.out.println("port = " + port);
            } catch (Exception e) {
                System.out.println("error port = " + port);
            }
        });

        for (Channel ch : channelList) {
            Executors.newCachedThreadPool().execute(() -> {
                try {
                    ch.closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

        }

    }
}