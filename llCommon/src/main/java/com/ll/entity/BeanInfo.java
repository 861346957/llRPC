package com.ll.entity;

import com.ll.Utils.TimeCacheUtils;

import java.io.Serializable;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/30 13:50
 */
public class BeanInfo implements Serializable {
    private static final long serialVersionUID = 722L;
    private String id;
    private String className;
    private String methodName;
    private Object[] params;
    private Class[] paramTypes;
    private Boolean isHeartbeat;
    private Long createTime;

    public BeanInfo(String id,String className, String methodName, Object[] params, Class[] paramTypes) {
        this.id=id;
        this.className = className;
        this.methodName = methodName;
        this.params = params;
        this.paramTypes = paramTypes;
        this.isHeartbeat=false;
    }

    public BeanInfo() {
        this.isHeartbeat=true;

    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public static BeanInfo createHeartbeatBean(){
        return new BeanInfo();
    }
    public String getId() {
        return id;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public Object[] getParams() {
        return params;
    }

    public Class[] getParamTypes() {
        return paramTypes;
    }

    public Boolean getHeartbeat() {
        return isHeartbeat;
    }

    public Long getCreateTime() {
        return createTime;
    }



    public void setHeartbeat(Boolean heartbeat) {
        isHeartbeat = heartbeat;
    }
}
