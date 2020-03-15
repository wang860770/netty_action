package com.ww.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class CustomDecoderV1 extends LengthFieldBasedFrameDecoder {


    public CustomDecoderV1(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);
        if (byteBuf == null) {
            return null;
        }
        SimpleProtocol simpleProtocol = new SimpleProtocol();
        simpleProtocol.setProtocolType(byteBuf.readByte());
        int length = byteBuf.readInt();
        simpleProtocol.setBodyLength(length);
        if (simpleProtocol.getBodyLength() > 0) {
            byte[] body = new byte[length];
            byteBuf.readBytes(body);
            simpleProtocol.setBody(body);
        }
        in.release();
        return simpleProtocol;
    }

}
