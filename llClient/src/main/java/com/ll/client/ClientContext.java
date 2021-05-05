package com.ll.client;

import com.ll.Utils.StringCustomUtils;
import com.ll.Utils.ThreadUtils;
import com.ll.Utils.TimeCacheUtils;
import com.ll.constant.ClientConstant;
import com.ll.context.ConfirmContext;
import com.ll.entity.BeanInfo;
import com.ll.entity.ConfirmMessage;
import com.ll.network.TcpClient;
import com.ll.route.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 9:23
 */
public class ClientContext {
    private String hosts;
    private String routeClass;
    private Route route;
    private Map<String,List<TcpClient>> clientMap=new ConcurrentHashMap<>();
    private static ClientContext clientContext;
    private static Logger logger= LoggerFactory.getLogger(ClientContext.class);
    public ClientContext(String hosts,String routeClass,Integer isConfirm,Integer confirmOverTime) {
        this.hosts = hosts;
        if(StringUtils.isEmpty(routeClass)){
            routeClass=ClientConstant.DEFAULT_ROUTE_CLASS;
        }
        this.routeClass=routeClass;
        ConfirmContext.init(isConfirm,confirmOverTime);
        init();
    }
    private void init(){
        String[] projectList = hosts.split(ClientConstant.DEFAULT_SEPARATOR);
        for (String project : projectList) {
            String key=ClientConstant.DEFAULT_PROJECT_NAME;
            String value=project;
            if(project.contains(ClientConstant.DEFAULT_PROJECT_SEPARATOR)){
                String[] split = project.split(ClientConstant.DEFAULT_PROJECT_SEPARATOR);
                key=split[0];
                value=split[1];

            }
            String[] hostArray = value.split(ClientConstant.DEFAULT_HOST_SEPARATOR);
            List<TcpClient> tcpClients = clientMap.get(key);
            if(tcpClients==null){
                tcpClients=new ArrayList<>();
                clientMap.put(key,tcpClients);
            }
            for (String hostString : hostArray) {
                Integer port=null;
                String host=hostString;
                if(hostString.contains(ClientConstant.DEFAULT_PORT_SEPARATOR)){
                    String[] hostAndPort = hostString.split(ClientConstant.DEFAULT_PORT_SEPARATOR);
                    host=hostAndPort[0];
                    port= StringCustomUtils.getInteger(hostAndPort[1]);
                }
                TcpClient tcpClient=new TcpClient(host,port);
                tcpClient.whetherConnect();
                tcpClients.add(tcpClient);
            }
        }

        startHeartbeat();
        startMissingMessageRetry();
    }
    private void startHeartbeat(){
        ThreadUtils.start(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Collection<List<TcpClient>> values = clientMap.values();
                    for (List<TcpClient> clients : values) {
                        for (TcpClient client : clients) {
                            client.sendMessage(BeanInfo.createHeartbeatBean());
                        }
                    }
                    try {
                        Thread.sleep(15*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        },1);
    }

    /**
     * 启动发送消息重试
     * 保存超时的消息删除，超过RETRY_MESSAGE_TIME时间未确认消息重试发送
     */
    private void startMissingMessageRetry(){
        ThreadUtils.start(new Runnable() {
            @Override
            public void run() {
                ConfirmContext<TcpClient, BeanInfo> instance = ConfirmContext.getInstance(new TcpClient(), new BeanInfo());
                while (true){
                    Map<String, ConfirmMessage<TcpClient, BeanInfo>> noAckResult = instance.getNoAckResult();
                    if(noAckResult.size()==0){
                        synchronized (instance){
                            if(noAckResult.size()==0){
                                try {
                                    instance.setAwaken(false);
                                    instance.wait();
                                } catch (InterruptedException e) {
                                    logger.info("send missingMessage thread is  interrupted");
                                }
                            }
                        }
                    }
                    if(noAckResult.size()==0){
                        continue;
                    }
                    System.out.println("发送失败消息线程已唤醒");
                    Iterator<Map.Entry<String, ConfirmMessage<TcpClient, BeanInfo>>> iterator = noAckResult.entrySet().iterator();
                    while (iterator.hasNext()){
                        ConfirmMessage<TcpClient, BeanInfo> confirmMessage = iterator.next().getValue();
                        BeanInfo beanInfo=confirmMessage.getMessage();
                        if(beanInfo.getCreateTime()+instance.getConfirmOverTime()<= TimeCacheUtils.getCacheTime()){
                            System.out.println("超时任务删除");
                            iterator.remove();
                            continue;
                        }
                        if(beanInfo.getCreateTime()+ClientConstant.RETRY_MESSAGE_TIME<TimeCacheUtils.getCacheTime()){
                            System.out.println("发送消息："+StringCustomUtils.getJsonByObject(beanInfo));
                            confirmMessage.getChannel().sendMessage(beanInfo,false);
                        }
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        logger.info("send missingMessage thread is  interrupted");
                    }
                }

            }
        },2);
    }
    public static void setClientContext(ClientContext clientContext){
        ClientContext.clientContext=clientContext;
    }
    public static ClientContext getClinetContext(){
        return ClientContext.clientContext;
    }

    public TcpClient getClient(String key) {
        return getRoute().getMachine(clientMap.get(key));
    }
    public TcpClient getClientByKey(String key){
        Collection<List<TcpClient>> values = clientMap.values();
        for (List<TcpClient> value : values) {
            for (TcpClient tcpClient : value) {
                if(tcpClient.getName().equals(key)){
                    return tcpClient;
                }
            }
        }
        return null;
    }
    public void sendMessage(BeanInfo message, String key){
        getClient(key).sendMessage(message);
    }
    private Route getRoute(){
        if(this.route==null){
            try {
                this.route=(Route) Class.forName(routeClass).newInstance();
            } catch (ClassNotFoundException e) {
                logger.error("route class not found");
            } catch (Exception e) {
                logger.error("bean initialization is error");
            }
        }
        return this.route;
    }
}
