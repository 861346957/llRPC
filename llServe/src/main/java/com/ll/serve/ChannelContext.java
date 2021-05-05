package com.ll.serve;

import com.ll.Utils.StringCustomUtils;
import com.ll.Utils.TimeCacheUtils;
import com.ll.context.ConfirmContext;
import com.ll.entity.ConfirmMessage;
import com.ll.entity.ResultInfo;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/4 10:10
 */
public class ChannelContext {
    private static Logger logger= LoggerFactory.getLogger(ChannelContext.class);
    private static ChannelContext channelContext;
    private  Map<String, ChannelHandlerContext> channelList;

    private ChannelContext() {
        this.channelList = new ConcurrentHashMap<>();
    }

    public static ChannelContext getInstance(){
        if(channelContext==null){
            synchronized (ChannelContext.class){
                if(channelContext==null){
                    channelContext=new ChannelContext();
                }
            }
        }
        return  channelContext;
    }
    public static String getKey(ChannelHandlerContext channelHandlerContext){
        InetSocketAddress address=(InetSocketAddress)channelHandlerContext.channel().remoteAddress();
        return address.getAddress().getHostAddress();
        //return channelHandlerContext.channel().remoteAddress().toString().substring(1);
    }

    public void addChannelHandlerContext(ChannelHandlerContext channelHandlerContext){
        String key=ChannelContext.getKey(channelHandlerContext);
        logger.info("client is online:"+key);
        synchronized (this){
            channelList.put(key,channelHandlerContext);
            ServeResultContext.getInstance().addQueue(key);
            //ThreadExecute.getInstance().executeAsync(channelHandlerContext,coreNumber);
        }
        final ConfirmContext<ChannelContext, ResultInfo> confirmContext = ConfirmContext.getInstance(this, new ResultInfo());
        notify(confirmContext);
    }
    public void removeChannelHandlerContext(ChannelHandlerContext channelHandlerContext){
        String key=ChannelContext.getKey(channelHandlerContext);
        logger.info("client is Offline:"+key);
        synchronized (this) {
            channelHandlerContext.close();
            channelList.remove(key);
        }
    }
    public void removeChannelHandlerContextAndQueue(ChannelHandlerContext channelHandlerContext){
        String key=ChannelContext.getKey(channelHandlerContext);
        logger.info("read and write is overtime:"+key);
        ServeResultContext.getInstance().increasingReadAndWriteOverTime(key);
        synchronized (this) {
            channelHandlerContext.close();
            channelList.remove(key);
            ServeResultContext.getInstance().removeQueue(key);
        }

    }
    public void sendMessage(final ResultInfo resultInfo,String key) {
       sendMessage(resultInfo,key,true);
    }
    public void sendMessage(final ResultInfo resultInfo,String key,Boolean isAckResult){
        try{
            ChannelHandlerContext channelHandlerContext = channelList.get(key);
            final ConfirmContext<ChannelContext, ResultInfo> confirmContext = ConfirmContext.getInstance(this, resultInfo);
            if(confirmContext.isConfirm() && isAckResult){
                confirmContext.addNoAckResult(resultInfo.getId(),new ConfirmMessage(this,resultInfo,key));
            }
            ChannelFuture future =sendMessageAndCreateTime(channelHandlerContext,resultInfo);
            if(future==null){
                return;
            }
            if(confirmContext.isConfirm()){
                future.addListener(new ChannelFutureListener(){
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if(future.isSuccess()){
                            confirmContext.remove(resultInfo.getId());
                        }
                    }
                });
            }
            notify(confirmContext);
        }catch (Throwable t){
            logger.info("sendMessage is error:"+ StringCustomUtils.getErrorMessage(t));
        }

    }
    private void notify(ConfirmContext<ChannelContext, ResultInfo> confirmContext){
        if( confirmContext.isConfirm() && !confirmContext.getAwaken() && confirmContext.getNoAckResult().size()>0){
            synchronized (confirmContext){
                if(confirmContext.isConfirm() && !confirmContext.getAwaken() && confirmContext.getNoAckResult().size()>0){
                    confirmContext.notify();
                    confirmContext.setAwaken(true);
                }
            }

        }
    }
    public ChannelFuture sendMessageAndCreateTime(ChannelHandlerContext channelHandlerContext, ResultInfo resultInfo){
        resultInfo.setCreateTime(TimeCacheUtils.getCacheTime());
        if(channelHandlerContext!=null){
           return  channelHandlerContext.writeAndFlush(resultInfo);
        }
        System.out.println("channelHandlerContext is null");
        return null;
    }
}
