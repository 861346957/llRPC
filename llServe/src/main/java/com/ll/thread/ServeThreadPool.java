package com.ll.thread;

import com.ll.serve.ServeResultContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/3 8:42
 */
public class ServeThreadPool {
    //ThreadPoolExecutor


    private Integer coreSize;
    private Integer maxCoreSize;
    private ThreadFactory threadFactory;
    private Long expireTime;
    private Integer threshold;
    private static Logger logger= LoggerFactory.getLogger(ServeThreadPool.class);
    public ServeThreadPool( Integer coreSize,Integer maxCoreSize,Long expireTime,Integer threshold) {
        this.coreSize = coreSize;
        this.maxCoreSize = maxCoreSize;
        this.expireTime=expireTime;
        this.threshold=threshold;
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
                        if(ThreadContext.getInstance().getRunThreadSize() < coreSize ||
                                (ServeResultContext.getInstance().getQueueSize()>=threshold && ThreadContext.getInstance().getRunThreadSize() < maxCoreSize)){
                            synchronized (ServeThreadPool.class){
                                if(ThreadContext.getInstance().getRunThreadSize() < coreSize ||
                                        (ServeResultContext.getInstance().getQueueSize()>=threshold && ThreadContext.getInstance().getRunThreadSize() < maxCoreSize)) {
                                    ServeWork serveWork = new ServeWork(threadFactory);
                                    serveWork.getThread().start();
                                    ThreadContext.getInstance().runThreadSizeIncrementAndGet();
                                }
                            }
                        }
                        if(ThreadContext.getInstance().getRunThreadSize().equals(coreSize) && ServeResultContext.getInstance().getQueueSize()<threshold){
                            TimeUnit.SECONDS.sleep(1);
                        }
                        if(ThreadContext.getInstance().getRunThreadSize().equals(maxCoreSize) && ServeResultContext.getInstance().getQueueSize()>=threshold){
                            TimeUnit.SECONDS.sleep(5);
                        }
                    }
                } catch (InterruptedException e) {
                   logger.info("thread pool is interrupt");
                }
            }
        }).start();
    }
    private synchronized void run(){


    }


}
