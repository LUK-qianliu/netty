package com.qianliu.client;

import com.qianliu.proto.MessageProto;
import com.qianliu.server.ProtoServer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ProtoClient {
    public void connect(int port) throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();

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
            ChannelFuture f = b.connect("localhost",port).sync();

            System.out.println("client start");
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8088;
        new ProtoClient().connect(port);
    }
}
