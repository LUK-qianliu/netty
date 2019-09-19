package client;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import pojo.NettyChannel;

import java.util.*;

import static config.AttributeMapConstant.NETTY_CHANNEL_KEY;

public class AttClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * channelActive在channelRead之前
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Attribute<NettyChannel> att = ctx.attr(NETTY_CHANNEL_KEY);  // 向ChannelHandlerContext中传入AttributeKey<NettyChannel>对象（key），获取Attribute<NettyChannel>（value）
        NettyChannel nettyChannel = att.get();// 获取AttributeKey<NettyChannel>中的nettyChannel
        if (nettyChannel == null) {
            NettyChannel newNChannel = new NettyChannel("Att1Client", new Date());
            att.setIfAbsent(newNChannel); // 给NETTY_CHANNEL_KEY设置一个value
        }else {
            System.out.println(nettyChannel.getName() + "attributeMap 中是有值的");
            System.out.println(nettyChannel.getName() + "=======" + nettyChannel.getCreateDate());
        }
        System.out.println("Att1CientHandler Active");
        ctx.fireChannelActive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Attribute<NettyChannel> attr = ctx.attr(NETTY_CHANNEL_KEY);
        NettyChannel nettyChannel = attr.get();
        if (nettyChannel == null) {
            NettyChannel newNChannel = new NettyChannel("Att1Client", new Date());
            attr.setIfAbsent(newNChannel); // 给NETTY_CHANNEL_KEY设置一个value
        }else {
            System.out.println(nettyChannel.getName() + "attributeMap 中是有值的");
            System.out.println(nettyChannel.getName() + "=======" + nettyChannel.getCreateDate());
        }
        System.out.println("Att1ClientHandler read Message:" + msg);

        ctx.fireChannelRead(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
