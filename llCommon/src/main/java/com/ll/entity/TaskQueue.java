package com.ll.entity;

import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 17:14
 */
public class TaskQueue {
    private String key;
    private LinkedBlockingQueue<BeanInfo> queue;

    public TaskQueue(String key, LinkedBlockingQueue<BeanInfo> queue) {
        this.key = key;
        this.queue = queue;
    }

    public String getKey() {
        return key;
    }

    public LinkedBlockingQueue<BeanInfo> getQueue() {
        return queue;
    }
}
