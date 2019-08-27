package com.qianliu.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

public class ServerTimerHandler extends ChannelInboundHandlerAdapter {
    // 业务处理逻辑
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("from client : " + (String) msg);

        ctx.writeAndFlush(Unpooled.copiedBuffer("ok".getBytes()));
    }

    // 异常处理逻辑
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("server exceptionCaught method run...");
        // cause.printStackTrace();
        ctx.close();
    }

}
