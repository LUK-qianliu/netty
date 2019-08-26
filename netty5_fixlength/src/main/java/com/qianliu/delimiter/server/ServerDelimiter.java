package com.qianliu.delimiter.server;

import com.qianliu.fixlength.server.ServerFixedLengthHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

public class ServerDelimiter {

    // 监听线程组，监听客户端请求
    private EventLoopGroup acceptorGroup = null;
    // 处理客户端相关操作线程组，负责处理与客户端的数据通讯
    private EventLoopGroup clientGroup = null;
    // 服务启动相关配置信息
    private ServerBootstrap bootstrap = null;
    public ServerDelimiter(){
        init();
    }

    /**
     * 初始化参数
     */
    private void init(){
        acceptorGroup = new NioEventLoopGroup();
        clientGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        // 绑定线程组
        bootstrap.group(acceptorGroup, clientGroup);
        // 设定通讯模式为NIO
        bootstrap.channel(NioServerSocketChannel.class);
        // 设定缓冲区大小
        bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
        // SO_SNDBUF发送缓冲区，SO_RCVBUF接收缓冲区，SO_KEEPALIVE开启心跳监测（保证连接有效）
        bootstrap.option(ChannelOption.SO_SNDBUF, 16*1024)
                .option(ChannelOption.SO_RCVBUF, 16*1024)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    /**
     *bootstrap指定handler，绑定端口
     */
    public ChannelFuture doAccept(int port) throws InterruptedException {
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                ByteBuf byteBuf = Unpooled.copiedBuffer("$E$".getBytes());
                // DelimiterBasedFrameDecoder的两个参数：最大长度，和结束符
                pipeline.addLast("fixedLengthFrameDecoder",new DelimiterBasedFrameDecoder(1024,byteBuf)); // 自定义结素符号的编码解码器
                pipeline.addLast("stringDecoder",new StringDecoder(Charset.forName("UTF-8")));
                pipeline.addLast("serverDelimiterHandler",new ServerDelimiterHandler());
            }
        });

        ChannelFuture future = bootstrap.bind(port).sync();
        return future;
    }

    /**
     * 释放资源
     */
    public void release(){
        this.acceptorGroup.shutdownGracefully();
        this.clientGroup.shutdownGracefully();
    }

    public static void main(String[] args){
        ChannelFuture future = null;
        ServerDelimiter server = null;
        try{
            server = new ServerDelimiter();

            future = server.doAccept(9999);
            System.out.println("server started.");
            future.channel().closeFuture().sync();
        }catch(InterruptedException e){
            e.printStackTrace();
        }finally{
            if(null != future){
                try {
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(null != server){
                server.release();
            }
        }
    }

}
