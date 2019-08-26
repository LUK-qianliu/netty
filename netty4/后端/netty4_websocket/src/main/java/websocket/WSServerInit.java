package websocket;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;


public class WSServerInit extends ChannelInitializer<SocketChannel> {
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec()); // webSocket基于http，使用http编码解码器
        pipeline.addLast(new ChunkedWriteHandler()); // 大数据流的读写
        pipeline.addLast(new HttpObjectAggregator(1024*64)); // FullHttpResponse和FullHttpRequest的聚合器，可以处理response和request

        // =============================  以上http协议的支持 ==============================

        /**
         * websocket协议的处理器，"/ws"是访问的路由
         * 本handler会处理一些繁重的事情
         * 会处理握手（close，ping，pong），ping+pong=心跳
         * 对于websocket来说，都是以frames的格式传输的
         */
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));

        // 自定义handler
        pipeline.addLast(new ChatHandler());
    }
}
