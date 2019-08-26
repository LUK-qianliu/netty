package server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HelloServer {
    public static void main(String[] args) {

        EventLoopGroup bossGroup = new NioEventLoopGroup();// 主线程组,专门管理客户的连接
        EventLoopGroup workGrop = new NioEventLoopGroup();// 从线程组，用来管业务的完成

        try {
            // serverBootstrap就是服务器启动类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workGrop)
                    .channel(NioServerSocketChannel.class)// NIO的channel类型
                    .childHandler(new HelloServerInit()); // 此处可以定从线程组的任务

            // 启动serverBootstrap，绑定8088端口，启动方式是同步的方式
            ChannelFuture channelFuture = serverBootstrap.bind(8088).sync();

            // 监听关闭的channel，设置位同步方式
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            // 关闭线程组
            bossGroup.shutdownGracefully();
            workGrop.shutdownGracefully();
        }
    }
}
