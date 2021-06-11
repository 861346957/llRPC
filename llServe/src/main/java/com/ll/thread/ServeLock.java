package com.ll.thread;

/**
 * Copyright (C) 1997-2020 康成投资（中国）有限公司
 * http://www.rt-mart.com
 * 版权归本公司所有，不得私自使用、拷贝、修改、删除，否则视为侵权
 *
 * @author liang.liu
 * @date createTime：2021/6/4 10:39
 */
public class ServeLock {
    private static volatile ServeLock serveLock;
    private Boolean isWait;
    private ServeLock() {
    }
    public static ServeLock getInstance(){
        if(serveLock==null){
            synchronized (ServeLock.class){
                if(serveLock==null){
                    serveLock=new ServeLock();
                }
            }
        }
        return  serveLock;
    }

    public void setWait() throws InterruptedException {
        synchronized (serveLock){
            ThreadContext.getInstance().compareAndSet(System.currentTimeMillis());
            this.isWait = true;
            serveLock.wait();
        }

    }
    public void notifyWait()  {
        synchronized (serveLock){
            if(isWait){
                serveLock.notifyAll();
                this.isWait=false;
            }
        }

    }
}
