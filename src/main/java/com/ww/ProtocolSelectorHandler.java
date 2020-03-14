package com.ww;

import com.ww.common.handler.ServerHeartBeatHandler;
import com.ww.http.MyHttpServerHandler;
import com.ww.tcp.CustomDecoder;
import com.ww.tcp.CustomEncoder;
import com.ww.tcp.CustomServerHandler;
import com.ww.websocket.MyTextWebSocketFrameHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * 协议选择器，支持动态协议HTTP WEBSOCKET TCP私有协议
 * Author: wangwei
 */
public class ProtocolSelectorHandler extends ByteToMessageDecoder {
    /**
     * websocket定义请求行前缀
     */
    private static final String WEBSOCKET_LINE_PREFIX = "GET /ws";
    /**
     * websocket的uri
     */
    private static final String WEBSOCKET_PREFIX = "/ws";
    /**
     * 检查10个字节，没有空格就是自定义协议
     */
    private static final int SPACE_LENGTH = 10;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println("before :" + ctx.pipeline().toString());
        if (isWebSocketUrl(in)) {
            System.out.println("addWebSocketHandlers");

            addWebSocketHandlers(ctx.pipeline());
        } else if (isCustomProcotol(in)) {
            System.out.println("addTCPProtocolHandlers");

            addTCPProtocolHandlers(ctx.pipeline());
        } else {
            System.out.println("addHTTPHandlers");
            addHTTPHandlers(ctx.pipeline());
        }
        ctx.pipeline().remove(this);
        System.out.println("after :" + ctx.pipeline().toString());
    }

    /**
     * 是否有websocket请求行前缀
     *
     * @param byteBuf
     * @return
     */
    private boolean isWebSocketUrl(ByteBuf byteBuf) {
        if (byteBuf.readableBytes() < WEBSOCKET_LINE_PREFIX.length()) {
            return false;
        }
        byteBuf.markReaderIndex();
        byte[] content = new byte[WEBSOCKET_LINE_PREFIX.length()];
        byteBuf.readBytes(content);
        byteBuf.resetReaderIndex();
        String s = new String(content, CharsetUtil.UTF_8);
        return s.equals(WEBSOCKET_LINE_PREFIX);


    }

    /**
     * 是否是自定义是有协议
     * @param byteBuf
     * @return
     */
    private boolean isCustomProcotol(ByteBuf byteBuf) {
        byteBuf.markReaderIndex();
        int length=Math.min(byteBuf.readableBytes(),SPACE_LENGTH);
        byte[] content = new byte[length];
        byteBuf.readBytes(content);
        byteBuf.resetReaderIndex();
        String s = new String(content, CharsetUtil.UTF_8);
        return s.indexOf(" ") == -1;
    }

    /**
     * 动态添加WebSocket处理器
     * @param pipeline
     */
    private void addWebSocketHandlers(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(8192));
        pipeline.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PREFIX));
        pipeline.addLast(new MyTextWebSocketFrameHandler());


    }
    /**
     * 动态添加TCP私有协议处理器
     * @param pipeline
     */
    private void addTCPProtocolHandlers(ChannelPipeline pipeline) {
//        pipeline.addLast(new StringDecoder());
        pipeline.addLast(new IdleStateHandler(2,0,0));
        pipeline.addLast(new CustomEncoder());
        pipeline.addLast(new ServerHeartBeatHandler());
        pipeline.addLast(new CustomDecoder(1024, 1, 4));
        pipeline.addLast(new CustomServerHandler());
    }


    /**
     * 动态添加HTTP处理器
     * @param pipeline
     */
    private void addHTTPHandlers(ChannelPipeline pipeline) {
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(8192));
        pipeline.addLast(new MyHttpServerHandler());


    }
}
