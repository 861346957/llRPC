package com.ll.context;

import com.ll.constant.ClientConstant;
import com.ll.entity.ConfirmMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 * @author liang.liu
 * @date createTime：2021/5/1 9:20
 */
public class ConfirmContext<T,Q> {
    private  Map<String, ConfirmMessage<T,Q>> noAckResult;
    private static ConfirmContext confirmContext;
    private static Integer isConfirm;
    private static Integer confirmOverTime;
    private Boolean awaken;
    private ConfirmContext() {
        noAckResult=new ConcurrentHashMap<>();
        awaken=true;
    }
    public static  void init(Integer isConfirm,Integer confirmOverTime){
        ConfirmContext.isConfirm = isConfirm;
        ConfirmContext.confirmOverTime=confirmOverTime;
        if(confirmContext ==null){
            synchronized (ConfirmContext.class){
                if(confirmContext ==null){
                    confirmContext =new ConfirmContext();
                }
            }
        }
    }

    public static <T,Q> ConfirmContext<T,Q> getInstance(T type1,Q type2 ){
        return confirmContext;
    }
    public boolean isConfirm(){
        return ClientConstant.IS_CONFIRM.equals(isConfirm);
    }

    public ConfirmMessage remove(String key){
        return noAckResult.remove(key);
    }
    public void addNoAckResult(String key,ConfirmMessage result){
        if(isConfirm()){
            noAckResult.put(key,result);
        }
    }

    public void setAwaken(Boolean awaken) {
        this.awaken = awaken;
    }

    public Boolean getAwaken() {
        return awaken;
    }

    public Long getConfirmOverTime(){
        return 1000L*ConfirmContext.confirmOverTime;
    }
    public Map<String, ConfirmMessage<T,Q>> getNoAckResult() {
        return noAckResult;
    }

}
