package com.ll.thread;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/2 16:45
 */
public class ThreadContext {
    private AtomicLong freeTime;
    private AtomicLong expireTime;
    private AtomicInteger runThreadSize;
    private AtomicInteger coreSize;
    private static ThreadContext threadContext;
    private ThreadContext(Long expireTime,Integer coreSize) {
        this.coreSize=new AtomicInteger(coreSize);
        this.expireTime = new AtomicLong(expireTime);
        this.freeTime=new AtomicLong(0L);
        runThreadSize=new AtomicInteger(0);
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
        return freeTime.get() + expireTime.get() <= System.currentTimeMillis();
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

    public Integer getRunThreadSize() {
        return runThreadSize.get();
    }

    public Integer getCoreSize() {
        return coreSize.get();
    }

    public Integer runThreadSizeIncrementAndGet(){
        return  runThreadSize.incrementAndGet();
    }
    public boolean isClose(){
        int temp = runThreadSize.get();
        if(temp>coreSize.get()){
            return runThreadSize.compareAndSet(temp,temp-1);
        }
       return false;
    }
    public Long getExpireTime() {
        return expireTime.get();
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime.set(expireTime);
    }
}
