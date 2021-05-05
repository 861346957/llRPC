package com.ll.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/2 15:31
 */
public class ServeThreadFactory implements ThreadFactory {
    private final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;
    private  String  key="";

    public ServeThreadFactory(String key) {
        this();
        this.key = key;
    }

    public ServeThreadFactory() {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() :
                Thread.currentThread().getThreadGroup();
        namePrefix = "llRPC-pool-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t;
        if(r==null){
            t = new Thread(group,
                    key+namePrefix + threadNumber.getAndIncrement());
        }else{
            t = new Thread(group, r,
                    key+namePrefix + threadNumber.getAndIncrement(),
                    0);
        }
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

}
