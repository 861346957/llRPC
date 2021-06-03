package com.ll.entity;

import com.ll.Utils.TimeCacheUtils;
import com.ll.constant.ClientConstant;

import java.io.Serializable;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 14:57
 */
public class ResultInfo implements Serializable {
    private static final long serialVersionUID = 722L;
    private String id;
    private Object result;
    private String status;
    private String errorMessage;
    private Long createTime;
    public ResultInfo(String id, Object result) {
      this(id,result, ClientConstant.SUCCESS);
    }

    public ResultInfo() {
    }

    public ResultInfo(String id, Object result, String status) {
        this.id = id;
        this.result = result;
        this.status = status;
    }
    public ResultInfo(String id, Object result, String status,String errorMessage) {
        this.id = id;
        this.result = result;
        this.status = status;
        this.errorMessage = errorMessage;
    }
    public static ResultInfo getErrorResultInfo(String id,String  errorMessage){
        return new ResultInfo(id,null,ClientConstant.FAIL,errorMessage);
    }
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
