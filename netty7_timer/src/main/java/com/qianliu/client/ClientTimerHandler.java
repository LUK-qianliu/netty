package com.qianliu.client;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

public class ClientTimerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("from server :  "+ msg.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("client exceptionCaught method run...");
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * 当连接建立成功后，出发的代码逻辑。
     * 在一次连接中只运行唯一一次。
     * 通常用于实现连接确认和资源初始化的。
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("client channel active");
    }
}
