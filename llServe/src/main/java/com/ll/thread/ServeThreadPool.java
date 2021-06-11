package com.ll.thread;

import com.ll.serve.ServeResultContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/3 8:42
 */
public class ServeThreadPool {
    private Integer coreSize;
    private Integer maxCoreSize;
    private ThreadFactory threadFactory;
    private Long expireTime;
    private Integer threshold;
    private volatile List<Thread> threadList;
    private boolean closeThreadState;
    private static Logger logger= LoggerFactory.getLogger(ServeThreadPool.class);
    public ServeThreadPool( Integer coreSize,Integer maxCoreSize,Long expireTime,Integer threshold) {
        this.coreSize = coreSize;
        this.maxCoreSize = maxCoreSize;
        this.expireTime=expireTime*1000;
        this.threshold=threshold;
        threadList=new ArrayList<>(maxCoreSize);
        closeThreadState=false;
        threadFactory=new ServeThreadFactory();
    }

    public void execute(){
        ThreadContext.init(expireTime,coreSize);
        asyncExecute();
    }
    private void asyncExecute(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (;;){
                        if(threadList.size() < coreSize ||
                                (ServeResultContext.getInstance().getQueueSize()>=threshold && threadList.size() < maxCoreSize)) {
                            if(getThreadSize() < coreSize ||
                                    (ServeResultContext.getInstance().getQueueSize()>=threshold && getThreadSize() < maxCoreSize)){
                                runTask();
                            }
                        }
                        if (ThreadContext.getInstance().isOverTime() && threadList.size()>coreSize) {
                            closeThread();
                        }
//                        logger.info("run Thread Size:"+threadList.size()+":queueSize:"+
//                                ServeResultContext.getInstance().getQueueSize()+";closeThreadState:"+closeThreadState);
                        if(maxCoreSize.equals(threadList.size())){
                            if(ServeResultContext.getInstance().getQueueSize()==0){
                                TimeUnit.SECONDS.sleep(1);
                            }else if(ServeResultContext.getInstance().getQueueSize()<=threshold){
                                TimeUnit.SECONDS.sleep(2);
                            }else{
                                TimeUnit.SECONDS.sleep(5);
                            }

                        }else{
                            TimeUnit.SECONDS.sleep(1);
                        }

                    }
                } catch (InterruptedException e) {
                    logger.info("thread pool is interrupt");
                }
            }
        }).start();
    }
    private synchronized void runTask(){
        ServeWork serveWork = new ServeWork(threadFactory,this);
        Thread thread = serveWork.getThread();
        threadList.add(thread);
        thread.start();

    }
    private synchronized  void closeThread(){
        if(threadList.size()>coreSize && !closeThreadState){
            threadList.get(threadList.size() - 1).interrupt();
            closeThreadState=true;
        }

    }
    public synchronized void closeTask(Thread thread){
        threadList.remove(thread);
        closeThreadState=false;
    }
    public synchronized Integer getThreadSize(){
        return threadList.size();
    }
}
