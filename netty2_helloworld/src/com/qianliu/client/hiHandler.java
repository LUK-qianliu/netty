package com.qianliu.client;

import org.jboss.netty.channel.*;

@ChannelHandler.Sharable  // 表示client1 - 连接server1时使用这个handler实例，client2- 连接server2时也可以使用这个handler实例
                            // 如果被@ChannelHandler.Sharable注解，里面不可以存在“可被修改的属性”，这样client1去修改属性1，client2也去修改属性1，不安全
public class hiHandler extends SimpleChannelHandler{
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
        //ctx.getChannel().write("hi");
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
     * channel关闭的时候触发，如果连接失败。那么只会有channelClosed会触发，channelDisconnected不会触发
     */
    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        System.out.println("channelClosed");
        super.channelClosed(ctx, e);
    }
}
