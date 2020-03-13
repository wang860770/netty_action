package com.ww.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.util.CharsetUtil;


public class MyHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if (msg instanceof FullHttpRequest) {

            FullHttpRequest httpRequest = (FullHttpRequest) msg;
            ByteBuf content1 = httpRequest.content();
            String cont = content1.toString(CharsetUtil.UTF_8);
            System.out.println(String.format("收到HTTP客户端[%s]消息", ctx.channel().remoteAddress()+":"+ctx.channel().id().asLongText())  + ":" + cont);

        }


    }


}
