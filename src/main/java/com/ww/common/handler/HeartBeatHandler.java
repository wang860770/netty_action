package com.ww.common.handler;

import com.ww.tcp.SimpleProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

public abstract class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    public static final byte PING = -1;
    public static final byte PONG = -2;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf=(ByteBuf)msg;
        byteBuf.markReaderIndex();
        byte receiveHeart = byteBuf.readByte();
        byteBuf.resetReaderIndex();
        if (receiveHeart < 0) {
            byteBuf.skipBytes(4);//略过长度
            if (receiveHeart == PING) {
//                System.out.println(String.format("收到[%s]发来的PING", ctx.channel().remoteAddress()));
                handlePing(ctx);
//                System.out.println(String.format("发送[%s]发来的PONG", ctx.channel().remoteAddress()));
            } else {
//                System.out.println(String.format("收到[%s]发来的PONG", ctx.channel().remoteAddress()));
                handlePong(ctx);

            }
            byteBuf.release();
        } else {
            ctx.fireChannelRead(byteBuf);
        }
    }



    void handleHeart(byte heart, ChannelHandlerContext ctx) {
        SimpleProtocol simpleProtocol = new SimpleProtocol();
        simpleProtocol.setProtocolType(heart);
        simpleProtocol.setBodyLength(0);
        ChannelFuture channelFuture = ctx.writeAndFlush(simpleProtocol);
//        channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
//            @Override
//            public void operationComplete(Future<? super Void> future) throws Exception {
//                if(!future.isSuccess())     {
//                    System.out.println(future.cause());
//                }
//            }
//        });
//        System.out.println(String.format("发送%s到[%s]", heart==-1?"Ping":"PONG", ctx.channel().remoteAddress()) + ", 已发送心跳个数: " + heartBeatCount);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            switch (e.state()) {
                case READER_IDLE:
                    handleReaderIdle(ctx);
                    break;
                case WRITER_IDLE:
                    handleWriterIdle(ctx);
                    break;
                case ALL_IDLE:
                    handleAllIdle(ctx);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().id().asLongText() + " channelActive");
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
//        System.out.println("客户端 channelInactive");
    }



    protected void handlePing(ChannelHandlerContext ctx) {
//        System.out.println(String.format("收到[%s]发来的PING", ctx.channel().remoteAddress()));
    }

    protected void handlePong(ChannelHandlerContext ctx) {
//        System.out.println(String.format("收到[%s]发来的Pong", ctx.channel().remoteAddress()));
    }

    protected void handleReaderIdle(ChannelHandlerContext ctx) {
//        System.err.println(ctx.channel().remoteAddress() + " ReaderIdle");
    }

    protected void handleWriterIdle(ChannelHandlerContext ctx) {
//        System.out.println(ctx.channel().remoteAddress() + " WriterIdle");
    }

    protected void handleAllIdle(ChannelHandlerContext ctx) {
//        System.out.println(ctx.channel().remoteAddress() + " AllIdle");
    }
}