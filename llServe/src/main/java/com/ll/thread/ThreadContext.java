package com.ll.thread;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/2 16:45
 */
public final class ThreadContext {
    private AtomicLong freeTime;
    private AtomicLong expireTime;
    private AtomicInteger coreSize;
    private static volatile ThreadContext threadContext;
    private ThreadContext(Long expireTime,Integer coreSize) {
        this.coreSize=new AtomicInteger(coreSize);
        this.expireTime = new AtomicLong(expireTime);
        this.freeTime=new AtomicLong(0L);
    }
    public static void init(Long expireTime,Integer coreSize){
        if(threadContext==null){
            synchronized (ThreadContext.class){
                if(threadContext==null){
                    threadContext=new ThreadContext(expireTime,coreSize);
                }
            }
        }
    }
    public static ThreadContext getInstance(){
        return threadContext;
    }
    public boolean isOverTime(){
        if(freeTime.get()==0){
            return  false;
        }
        return freeTime.get() + expireTime.get() < System.currentTimeMillis();
    }
    public Long getFreeTime() {
        return freeTime.get();
    }

    public void compareAndSet(Long freeTime) {
        this.freeTime.compareAndSet(0L,freeTime);

    }

    public void setFreeTime(Integer freeTime) {
        this.freeTime.set(freeTime);
    }

    public Integer getCoreSize() {
        return coreSize.get();
    }

    public Long getExpireTime() {
        return expireTime.get();
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime.set(expireTime);
    }
}
