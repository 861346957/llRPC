package com.ll.thread;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 *已过时
 * @author liang.liu
 * @date createTime：2021/5/1 15:50
 */
@Deprecated
public class ThreadPool {
    private Integer threadNumber;
    private String  key;

    public ThreadPool(Integer threadNumber,String key ) {
        this.threadNumber = threadNumber;
        this.key=key;
    }
    public ThreadPoolExecutor getPool(){
        return new ThreadPoolExecutor(threadNumber,threadNumber,1, TimeUnit.SECONDS,new LinkedBlockingDeque<Runnable>(),new ServeThreadFactory(key));
    }
}
