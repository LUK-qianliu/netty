package com.qianliu.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.WriteTimeoutHandler;

import java.util.concurrent.TimeUnit;

public class ClientTimer {
    // 处理请求和处理服务端响应的线程组
    private EventLoopGroup group = null;
    // 服务启动相关配置信息
    private Bootstrap bootstrap = null;
    private ChannelFuture future = null;

    public ClientTimer(){
        init();
    }

    private void init(){
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        // 绑定线程组
        bootstrap.group(group);
        // 设定通讯模式为NIO
        bootstrap.channel(NioSocketChannel.class);
        // bootstrap.handler(new LoggingHandler(LogLevel.INFO));
    }

    public void setHandlers() throws InterruptedException{
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new StringEncoder());
                ch.pipeline().addLast(new StringDecoder());
                // 写操作自定断线。 在指定时间内，没有写操作，自动断线。
                ch.pipeline().addLast(new WriteTimeoutHandler(3));
                ch.pipeline().addLast(new ClientTimerHandler());
            }
        });
    }

    /**
     * 重新连接可能是future不在active状态，也坑是第一次创建的连接
     * @param host
     * @param port
     * @return
     * @throws InterruptedException
     */
    public ChannelFuture getChannelFuture(String host, int port) throws InterruptedException{
        if(future == null){
            future = this.bootstrap.connect(host, port).sync();
        }
        if(!future.channel().isActive()){
            future = this.bootstrap.connect(host, port).sync();
        }
        return future;
    }

    public void release(){
        this.group.shutdownGracefully();
    }

    public static void main(String[] args) {
        ClientTimer client = null;
        ChannelFuture future = null;
        try{
            client = new ClientTimer();
            client.setHandlers();

            future = client.getChannelFuture("localhost", 9999);

            // 循环写入数据
            for(int i = 0; i < 3; i++){
                future.channel().writeAndFlush("hello,"+i);
                TimeUnit.SECONDS.sleep(2);
            }

            //5秒不写入数据，断线
            TimeUnit.SECONDS.sleep(5);

            future = client.getChannelFuture("localhost", 9999);

            future.channel().writeAndFlush(Unpooled.copiedBuffer("hello,finnally".getBytes()));
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(null != future){
                try {
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(null != client){
                client.release();
            }
        }
    }
}
