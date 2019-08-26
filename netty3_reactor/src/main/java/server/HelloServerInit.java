package server;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 初始化器：channel注册后，会执行里面的相应的初始化方法
 */
public class HelloServerInit extends ChannelInitializer<SocketChannel> {

    protected void initChannel(SocketChannel socketChannel) throws Exception {
        // 通过socketChannel获取对应处理的管道
        ChannelPipeline pipeline = socketChannel.pipeline();

        // HttpServerCodec是netty自带的handler，是http协议的编码解码器
        pipeline.addLast("HttpServerCodec",new HttpServerCodec());
        pipeline.addLast("customHandler",new CustomHandler());
    }
}
