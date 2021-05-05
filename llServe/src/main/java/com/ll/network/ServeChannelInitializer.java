package com.ll.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 *
 * @author liang.liu
 * @date createTime：2021/4/30 11:19
 */
public class ServeChannelInitializer extends ChannelInitializer<SocketChannel> {
    private ChannelHandler channelHandler;
    private Integer readAndWriteTime;

    public ServeChannelInitializer(ChannelHandler channelHandler,Integer readAndWriteTime) {
        this.channelHandler = channelHandler;
        this.readAndWriteTime = readAndWriteTime;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        // 增加心跳事件支持
        // 第一个参数:  读空闲0秒,不监控
        // 第二个参数： 写空闲0秒,不监控
        // 第三个参数： 读写空闲秒
        pipeline.addLast(new IdleStateHandler(0,0,readAndWriteTime));
        //java对象编码器
        pipeline.addLast("encoder", new ObjectEncoder());
        //java对象解码器
        pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
        //服务器端业务处理器
        pipeline.addLast(channelHandler);

        //增加自定义心跳处理
        pipeline.addLast(new HeartBeatHandler());
    }
}
