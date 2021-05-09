package com.ll.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.entity.ResultInfo;
import com.ll.network.TcpClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 15:07
 */
public final class ResultContext {
    private static volatile ResultContext resultContext;
    private ObjectMapper objectMapper=new ObjectMapper();
    private Map<String, ResultInfo> map=new ConcurrentHashMap<>();
    private ResultContext() {
    }
    public static ResultContext getInstance(){
        if(resultContext==null){
            synchronized (ResultContext.class){
                if(resultContext==null){
                    resultContext=new ResultContext();
                }
            }
        }
        return resultContext;
    }
    private static Map<String, ResultInfo> resultMap=new ConcurrentHashMap<>();
    public void addResult(ResultInfo resultInfo){
        if(resultInfo!=null){
            map.put(resultInfo.getId(),resultInfo);
        }
    }
    public ResultInfo getResult(String id, TcpClient client){
        for (;;){
            if(map.containsKey(id)){
               return map.remove(id);
            }
            client.whetherConnect();
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
