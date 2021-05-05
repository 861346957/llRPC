package com.ll.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * @author liang.liu
 * @date createTime：2021/5/2 11:19
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private ChannelHandler channelHandler;

    public ClientChannelInitializer(ChannelHandler channelHandler) {
        this.channelHandler = channelHandler;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //编码器
        pipeline.addLast("encoder", new ObjectEncoder());
        //解码器
        pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
        //服务器端业务处理器
        pipeline.addLast(channelHandler);
    }
}
