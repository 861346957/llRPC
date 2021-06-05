package com.ll.network;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.entity.BeanInfo;
import com.ll.serve.ChannelContext;
import com.ll.serve.ServeResultContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 *
 * @author liang.liu
 * @date createTime：2021/5/5 11:02
 */
@ChannelHandler.Sharable
public class ResultCharHandler extends SimpleChannelInboundHandler<BeanInfo> {
    ObjectMapper mapper=new ObjectMapper();
    /**
     * 接收数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BeanInfo msg) throws Exception {
        System.out.println(mapper.writeValueAsString(msg));
        ServeResultContext.getInstance().addResult(msg,ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelContext.getInstance().addChannelHandlerContext(ctx);

    }

    /**
     * 当触发handlerRemoved，ChannelGroup会自动移除对应服务端的channel
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        ChannelContext.getInstance().removeChannelHandlerContext(ctx);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ChannelContext.getInstance().removeChannelHandlerContext(ctx);
    }
}
