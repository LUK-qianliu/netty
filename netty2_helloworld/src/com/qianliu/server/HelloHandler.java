package com.qianliu.server;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.*;

public class HelloHandler extends SimpleChannelHandler {
    /**
     * 接收消息
     */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        //ChannelBuffer s = (ChannelBuffer) e.getMessage();
        //String s = new String(s.array());

        // 因为pipeline中添加了StringDecoder，所以e.getMessage()可以直接强转为String，否则需要使用上面的代码;
        String s = (String) e.getMessage();
        System.out.println(s);

        //回写数据
        // 回写的数据字节是一个字符串，如果没有StringEncoder通道，需要String -> ChennalBuffer再发送
        ctx.getChannel().write("hi");
        super.messageReceived(ctx, e);
    }

    /**
     * 捕获异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        System.out.println("exceptionCaught");
        super.exceptionCaught(ctx, e);
    }

    /**
     * 新连接
     */
    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelConnected");
        super.channelConnected(ctx, e);
    }

    /**
     * 必须是链接已经建立，关闭通道的时候才会触发
     */
    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelDisconnected");
        super.channelDisconnected(ctx, e);
    }

    /**
     * channel关闭的时候触发
     */
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelClosed");
        super.channelClosed(ctx, e);
    }
}
