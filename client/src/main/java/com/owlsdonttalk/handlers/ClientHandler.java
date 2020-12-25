package com.owlsdonttalk.handlers;

import com.owlsdonttalk.RequestData;
import com.owlsdonttalk.ResponseData;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.nio.charset.Charset;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx)
            throws Exception {

        String msg = "all work and no play makes jack a dull boy";
        System.out.println(msg);
        ChannelFuture future = ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        String s = buf.toString(Charset.defaultCharset());

        System.out.println(s);
        ctx.close();
    }
}