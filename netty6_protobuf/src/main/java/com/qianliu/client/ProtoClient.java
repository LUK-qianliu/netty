package com.qianliu.client;

import com.qianliu.proto.MessageProto;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;


import java.util.concurrent.TimeUnit;

public class ProtoClient {
    public void connect(int port) throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture f = null; // 回调

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast("encoder",new ProtobufEncoder()); // encoder
                            ch.pipeline().addLast("decoder",new ProtobufDecoder(MessageProto.Message.getDefaultInstance())); // decoder:将数据段转化为Message对象
                            ch.pipeline().addLast("protoBufClientHandler",new ProtoBufClientHandler());
                        }
                    });

            // 绑定端口，同步等待成功
            f = b.connect("localhost",port).sync();

            System.out.println("client start");
            TimeUnit.SECONDS.sleep(1);
            // 等待服务端监听端口关闭
            f.addListener(ChannelFutureListener.CLOSE);
        } finally {
            // 优雅退出，释放线程池资源
            group.shutdownGracefully();

            if(null != f){
                try {
                    f.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8088;
        new ProtoClient().connect(port);
    }
}
