package com.ll.entity;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 10:43
 */
public class ConfirmMessage<T,Q> {
    private T channel;
    private Q message;
    private String key;
    public ConfirmMessage(T channel, Q message) {
        this.channel = channel;
        this.message = message;
    }

    public ConfirmMessage(T channel, Q message, String key) {
        this.channel = channel;
        this.message = message;
        this.key = key;
    }

    public T getChannel() {
        return channel;
    }

    public Q getMessage() {
        return message;
    }

    public String getKey() {
        return key;
    }
}
