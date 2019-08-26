package com.qianliu.server;

import com.qianliu.proto.MessageProto;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

public class ProtoBufServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageProto.Message message = (MessageProto.Message) msg;
        System.out.println("from client:"+message);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //初始化MessageProto.Message对象
        MessageProto.Message.Builder builder = MessageProto.Message.newBuilder();
        builder.setId("2");
        builder.setContent("this is text for protobuf2");
        ctx.writeAndFlush(builder.build());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
