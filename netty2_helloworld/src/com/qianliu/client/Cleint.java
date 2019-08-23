package com.qianliu.client;


import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Cleint {
    public static void main(String[] args) {
        ClientBootstrap bootstrap = new ClientBootstrap();

        ExecutorService boss = Executors.newCachedThreadPool();
        ExecutorService work = Executors.newCachedThreadPool();

        bootstrap.setFactory(new NioClientSocketChannelFactory(boss,work));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline  = Channels.pipeline();
                pipeline.addLast("decoder", new StringDecoder()); // StringDecoder这个管道可以让我们直接在读取数据时的数据变成已经反序列化的String类型（系统默认读取的数据时Chennel）
                pipeline.addLast("encoder", new StringEncoder()); // StringEncoder可以让我们回写数据的时候直接返回一个字符串
                pipeline.addLast("helloHandler", new hiHandler());
                return pipeline;
            }
        });

        //连接服务端
        ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("localhost",10101));
        Channel channel = channelFuture.getChannel();

        System.out.println("start..");

        Scanner s = new Scanner(System.in);
        while (true){
            //System.out.println("请写入数据：");
            channel.write(s.next());
        }

    }
}
