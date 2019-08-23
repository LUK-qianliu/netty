package com.qianliu.server;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public static void main(String[] args) {

        //开启一个服务类
        ServerBootstrap bootstrap = new ServerBootstrap();

        //boss线程监听端口，worker线程负责数据读写
        ExecutorService boss = Executors.newCachedThreadPool();
        ExecutorService work = Executors.newCachedThreadPool();

        // 设置nio socket工厂：交代监听端口的boss，和读写事件的work
        bootstrap.setFactory(new NioServerSocketChannelFactory(boss,work));

        // 设置管道工厂: 可以在启动自定义管道，也可以使用系统自带的一些管道
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decoder", new StringDecoder()); // StringDecoder这个管道可以让我们直接在读取数据时的数据变成已经反序列化的String类型（系统默认读取的数据时Chennel）
                pipeline.addLast("encoder", new StringEncoder()); // StringEncoder可以让我们回写数据的时候直接返回一个字符串
                pipeline.addLast("helloHandler", new HelloHandler());
                return pipeline;
            }
        });

        // 绑定消息端口
        bootstrap.bind(new InetSocketAddress(10101));

        System.out.println("start!!!");
    }
}
