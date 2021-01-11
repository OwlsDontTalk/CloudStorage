package com.owlsdonttalk.handlers.out;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;

public class OutboundResponceToClientHandler extends ChannelOutboundHandlerAdapter {

    private BufferedOutputStream out;



    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        System.out.println("[HANDLER] Outbound Responce To Client Handler");
        String str = (String) msg;
        byte[] arr = (str + " StringToByteBufHandler2 ").getBytes();
        ByteBuf buf = ctx.alloc().buffer(arr.length);
        buf.writeBytes(arr);

    }
}
