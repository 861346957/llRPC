package com.ll.network;

import com.ll.Utils.ThreadUtils;
import com.ll.Utils.TimeCacheUtils;
import com.ll.constant.ClientConstant;
import com.ll.context.ConfirmContext;
import com.ll.entity.ConfirmMessage;
import com.ll.entity.ResultInfo;
import com.ll.serve.ChannelContext;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 17:21
 */
public class TcpServe {
    private Integer monitorPort;
    private ServerBootstrap bootstrap;
    private Integer readAndWriteTime;
    private static Logger log= LoggerFactory.getLogger(TcpServe.class);
    public TcpServe(Integer monitorPort,Integer readAndWriteTime) {
        this.monitorPort = monitorPort;
        this.readAndWriteTime = readAndWriteTime;
        bootstrap =new ServerBootstrap();
        init();
    }
    private void init(){
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workGroup=new NioEventLoopGroup();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                //识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
                .option(ChannelOption.SO_BACKLOG, 128)
                //是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //.localAddress(monitorPort)
                .childHandler(new ServeChannelInitializer(new ResultCharHandler(),readAndWriteTime));
        try {
            bootstrap.bind(monitorPort).sync();
            startMissingMessageRetry();
        } catch (InterruptedException e) {
            e.printStackTrace();
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * 启动发送消息重试
     * 保存超时的消息删除，超过RETRY_MESSAGE_TIME时间未确认消息重试发送
     */
    private void startMissingMessageRetry(){
        ThreadUtils.start(new Runnable() {
            @Override
            public void run() {
                ConfirmContext<ChannelContext, ResultInfo> instance = ConfirmContext.getInstance(ChannelContext.getInstance(), new ResultInfo());
                while (true){
                    Map<String, ConfirmMessage<ChannelContext, ResultInfo>> noAckResult = instance.getNoAckResult();
                    if(noAckResult.size()==0){
                        synchronized (instance){
                            if(noAckResult.size()==0){
                                try {
                                    instance.setAwaken(false);
                                    instance.wait();
                                } catch (InterruptedException e) {
                                    log.info("send missingMessage thread is  interrupted");
                                }
                            }
                        }
                    }
                    if(noAckResult.size()==0){
                        continue;
                    }
                    Iterator<Map.Entry<String, ConfirmMessage<ChannelContext, ResultInfo>>> iterator = noAckResult.entrySet().iterator();
                    while (iterator.hasNext()){
                        ConfirmMessage<ChannelContext, ResultInfo> confirmMessage = iterator.next().getValue();
                        ResultInfo resultInfo=confirmMessage.getMessage();
                        if(resultInfo.getCreateTime()+instance.getConfirmOverTime()<= TimeCacheUtils.getCacheTime()){
                            System.out.println("服务端消息删除");
                            iterator.remove();
                            continue;
                        }
                        if(resultInfo.getCreateTime()+ ClientConstant.RETRY_MESSAGE_TIME<TimeCacheUtils.getCacheTime()){
                            System.out.println("服务端发送消息");
                            confirmMessage.getChannel().sendMessage(resultInfo,confirmMessage.getKey(),false);
                        }
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        log.info("send missingMessage thread is  interrupted");
                    }
                }

            }
        },2);
    }

}
