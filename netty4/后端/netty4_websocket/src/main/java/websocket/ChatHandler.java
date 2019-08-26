package websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import sun.util.calendar.BaseCalendar;

import java.util.Locale;


/**
 * 处理web消息的handler
 *
 * TextWebSocketFrame：再netty中专门处理web文本的处理器
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 所有的客户端channel的管理器
    public static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取客户端发过来的消息
        String content = msg.text();
        System.out.println("接收到的消息"+content);

        // 消息刷到客户端
//        for(Channel channel : clients){
//            channel.writeAndFlush(new TextWebSocketFrame("[服务器端收到消息：]"+ content));
//        }
        clients.writeAndFlush(new TextWebSocketFrame("[服务器端收到消息：]"+ content));
    }

    /**
     * 打开浏览器就会触发此方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel()); // 将客户的channel加入到ChannelGroup中
    }

    /**
     * 用户关闭浏览器触发此方法
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当使用ChannelGroup管理channel时，handlerRemoved触发，就会自动使用下面这行代码
        //clients.remove(ctx.channel());
        System.out.println("客户端断开，channel的长id："+ctx.channel().id().asLongText());
        System.out.println("客户端断开，channel的短id："+ctx.channel().id().asShortText());
    }
}
