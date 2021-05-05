package com.ll.network;

import com.ll.Utils.StringCustomUtils;
import com.ll.client.ClientContext;
import com.ll.client.ResultContext;
import com.ll.entity.ResultInfo;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 11:02
 */
@ChannelHandler.Sharable
public class ResultCharHandler  extends SimpleChannelInboundHandler<ResultInfo> {
    /**
     * 接收数据
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResultInfo msg) throws Exception {
        System.out.println("服务端的消息："+ StringCustomUtils.getJsonByObject(msg));
        ResultContext.getInstance().addResult(msg);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive:"+TcpClient.getKey(ctx));
    }

    /**
     * 当触发handlerRemoved，ChannelGroup会自动移除对应服务端的channel
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }

    /**
     * 连接断开
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        String key = TcpClient.getKey(ctx);
        System.out.println("channelInactive:"+key);
        TcpClient clientByKey = ClientContext.getClinetContext().getClientByKey(key);
        clientByKey.whetherConnect();


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
