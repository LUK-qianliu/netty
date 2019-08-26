package com.qianliu.fixlength.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

public class ServerFixedLength {
    private EventLoopGroup boss = null;
    private EventLoopGroup work = null;
    private ServerBootstrap bootstrap = null;

    public ServerFixedLength(){
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        boss = new NioEventLoopGroup();
        work = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();

        bootstrap.group(boss,work);

        bootstrap.channel(NioServerSocketChannel.class);

        //SO_SNDBUF发送缓存区，SO_RCVBUF是接受缓存区，SO_KEEPALIVE开启心跳检测
        bootstrap.option(ChannelOption.SO_SNDBUF,16*1024)
                .option(ChannelOption.SO_RCVBUF,16*1024)
                .option(ChannelOption.SO_KEEPALIVE,true);
    }

    /**
     *bootstrap指定handler，绑定端口
     */
    public ChannelFuture doAccept(int port) throws InterruptedException {
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("fixedLengthFrameDecoder",new FixedLengthFrameDecoder(3)); // 固定长度的粘包解码器，单位是字节，不足部分补上空格
                pipeline.addLast("stringDecoder",new StringDecoder(Charset.forName("UTF-8")));
                pipeline.addLast("serverFixedLengthHandler",new ServerFixedLengthHandler());
            }
        });

        ChannelFuture future = bootstrap.bind(port).sync();
        return future;
    }

    /**
     * 释放资源
     */
    public void release(){
        this.boss.shutdownGracefully();
        this.work.shutdownGracefully();
    }

    public static void main(String[] args) {
        ChannelFuture future = null;
        ServerFixedLength server = null;
        try{
            server = new ServerFixedLength();

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
