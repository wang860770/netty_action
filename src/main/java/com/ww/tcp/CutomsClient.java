package com.ww.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class CutomsClient {
    EventLoopGroup group;
    Bootstrap bootstrap;
    public CutomsClient() {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();

    }

    public void start(String ip, int port) throws InterruptedException {
        try {
            bootstrap.group(group).channel(NioSocketChannel.class)
                    .handler(new CutsomClientHandlerInitializer()); //自定义一个初始化类
            ChannelFuture channelFuture = bootstrap.connect(ip, port).sync();
            channelFuture.channel().closeFuture().sync();
        }  finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CutomsClient gameClient = new CutomsClient();
        gameClient.start("127.0.0.1",8080);
    }
}
