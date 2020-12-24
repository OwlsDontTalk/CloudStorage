package com.owlsdonttalk.handlers.out;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class ResponceToClientHandler extends ChannelOutboundHandlerAdapter {



    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("ResponceToClientHandler. Converting string response to byte[] to end pipeline");
        String str = (String) msg;
        System.out.println(str);
        byte[] arr = (str + " - response!").getBytes();

        ByteBuf buf = ctx.alloc().buffer(arr.length);
        buf.writeBytes(arr);
        ctx.writeAndFlush(buf);
    }


}
