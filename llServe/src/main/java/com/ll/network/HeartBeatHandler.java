package com.ll.network;

import com.ll.serve.ChannelContext;
import com.ll.serve.ServeResultContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/3 9:40
 */
@ChannelHandler.Sharable
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {
    private static Logger log= LoggerFactory.getLogger(HeartBeatHandler.class);
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //用于触发用户时间，包含渎空闲/写空闲
        if(evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;
            if(event.state()==IdleState.ALL_IDLE){
                String key=ChannelContext.getKey(ctx);
                log.info(key+" is read and write overTime:"+new Date().toLocaleString());
                ServeResultContext.getInstance().increasingReadAndWriteOverTime(key);
                ChannelContext.getInstance().removeChannelHandlerContextAndQueue(ctx);
            }
        }
    }
}
