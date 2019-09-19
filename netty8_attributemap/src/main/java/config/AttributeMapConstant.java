package config;

import io.netty.util.AttributeKey;
import pojo.NettyChannel;

/**
 * 常量配置文件
 */
public class AttributeMapConstant {

    public static final AttributeKey<NettyChannel> NETTY_CHANNEL_KEY = AttributeKey.valueOf("netty.channel");
}
