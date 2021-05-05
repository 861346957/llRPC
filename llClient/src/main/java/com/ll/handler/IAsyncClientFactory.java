package com.ll.handler;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/2 15:20
 */
public class IAsyncClientFactory<T> implements FactoryBean<T> {
    private  Class<T> interfaceType;
    public IAsyncClientFactory(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }
    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(),new Class[]{interfaceType},new AsyncProxyHandler<>(interfaceType));
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
