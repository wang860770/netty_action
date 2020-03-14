package com.ww.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class CustomClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

//        ctx.executor().scheduleAtFixedRate(() -> {
//
//            SimpleProtocol simpleProtocol = new SimpleProtocol();
//            simpleProtocol.setProtocolType((byte) 2);
//            String s = "你好，我是自定义协议";
//            byte[] bytes = s.getBytes(CharsetUtil.UTF_8);
//            simpleProtocol.setBody(bytes);
//            simpleProtocol.setBodyLength(bytes.length);
//
//            ctx.writeAndFlush(simpleProtocol);
//        }, 0, 5000, TimeUnit.MILLISECONDS);



    }


}
