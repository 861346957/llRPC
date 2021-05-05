package com.ll.Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 17:20
 */
public class ThreadUtils {
    public static AtomicInteger count=new AtomicInteger(0);
    public static Logger logger= LoggerFactory.getLogger(ThreadUtils.class);
    private static String name="llRPC-thread-";
    public static AtomicInteger nameId=new AtomicInteger(0);

    /**
     * 小于threshold的值启动超线程
     * @param runnable
     * @param threshold
     */
    public static boolean start(Runnable runnable,Integer threshold){
        if(count.get()<threshold){
            synchronized (ThreadUtils.class){
                if(count.get()<threshold){
                    new Thread(new RunnableUtils(runnable),name+nameId.incrementAndGet()).start();
                    count.incrementAndGet();
                    return true;
                }
            }
        }
        return false;
    }
    public static Integer getRunThreadSize(){
        return count.get();
    }
    public static void start(Runnable runnable,Integer threshold,Integer overTime){
        long start = System.currentTimeMillis();
        while (!start(runnable,threshold)){
            if(start+overTime<=System.currentTimeMillis()){
                logger.info("start thread is overtime");
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    static class RunnableUtils implements Runnable{
        private Runnable runnable;
        public RunnableUtils(Runnable runnable) {
            this.runnable = runnable;
        }
        @Override
        public void run() {
            runnable.run();
            count.decrementAndGet();
        }
    }
}
