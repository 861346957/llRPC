package com.ll.network;

import com.ll.Utils.RetryUtils;
import com.ll.Utils.StringCustomUtils;
import com.ll.Utils.TimeCacheUtils;
import com.ll.client.ResultContext;
import com.ll.constant.ClientConstant;
import com.ll.context.ConfirmContext;
import com.ll.entity.BeanInfo;
import com.ll.entity.ConfirmMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


/**
 *
 * @author liang.liu
 * @date createTime：2021/5/4 14:35
 */
public class TcpClient {
    private String host;
    private Integer port;
    private Bootstrap bootstrap;
    private Channel channel;
    private static Logger log= LoggerFactory.getLogger(TcpClient.class);

    public TcpClient() {
    }

    public TcpClient(String host, Integer port) {
        this.host = host;
        if(port==null){
            port=ClientConstant.DEFAULT_PORT;
        }
        this.port = port;
        init();
    }
    private void init() {
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ClientChannelInitializer(new ResultCharHandler()));

    }

    public Channel getChannel() {
        return channel;
    }

    public String getName(){
        return StringCustomUtils.getString(ClientConstant.DEFAULT_PORT_SEPARATOR,host,port);
    }
    public  void whetherConnect(){
        if(!isOpen()){
            synchronized (this){
                if(!isOpen()){
                    RetryUtils.retry(new RetryUtils.Retry() {
                        @Override
                        public boolean logic() {
                            ChannelFuture channelFuture=null;
                            try {
                                log.info("connect is :"+getName());
                                channelFuture = bootstrap.connect(host, port).sync();
                                channel=channelFuture.channel();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return channelFuture == null ? false : channelFuture.isSuccess();
                        }
                    },3,120,1);
                }
            }
        }

    }
    public static String getKey(ChannelHandlerContext channelHandlerContext){
        return channelHandlerContext.channel().remoteAddress().toString().substring(1);
    }
    public boolean isOpen(){
        if(channel!=null && channel.isOpen()){
            return true;
        }
        return false;
    }
    public void sendMessage(final BeanInfo beanInfo){
        sendMessage(beanInfo,true);
    }
    public void sendMessage(final BeanInfo beanInfo,Boolean isAckResult){
        whetherConnect();
        final  ConfirmContext<TcpClient, BeanInfo> instance = ConfirmContext.getInstance(this, beanInfo);
        if(!beanInfo.getHeartbeat() && instance.isConfirm() && isAckResult){
            instance.addNoAckResult(beanInfo.getId(),new ConfirmMessage(this,beanInfo));
        }
        beanInfo.setCreateTime(TimeCacheUtils.getCacheTime());
        if(!StringUtils.isEmpty(beanInfo.getId())){
            ResultContext.getInstance().addLock(beanInfo.getId(),new Object());
        }
        ChannelFuture future = sendMessageByBeanInfo(beanInfo);
        if(!beanInfo.getHeartbeat() && instance.isConfirm()){
            future.addListener(new ChannelFutureListener(){
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if(future.isSuccess()){
                        System.out.println("消息发送成功："+StringCustomUtils.getJsonByObject(beanInfo));
                        instance.remove(beanInfo.getId());
                    }

                }
            });
        }
        if(!beanInfo.getHeartbeat() && instance.isConfirm()&& !instance.getAwaken() && instance.getNoAckResult().size()>0){
            synchronized (instance){
                if(!beanInfo.getHeartbeat() && instance.isConfirm() && !instance.getAwaken() && instance.getNoAckResult().size()>0){
                    instance.notify();
                    instance.setAwaken(true);
                }
            }

        }
    }
    public ChannelFuture sendMessageByBeanInfo(BeanInfo beanInfo){
        return this.channel.writeAndFlush(beanInfo);
    }
}
