package com.ll.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.entity.ResultInfo;
import com.ll.network.TcpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 15:07
 */
public final class ResultContext {
    private static volatile ResultContext resultContext;
    private ObjectMapper objectMapper=new ObjectMapper();
    private Map<String, ResultInfo> map=new ConcurrentHashMap<>();
    private Map<String, Object> lockMap=new ConcurrentHashMap<>();
    private static Logger log= LoggerFactory.getLogger(ResultContext.class);
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
    public void addLock(String id,Object lock){
        lockMap.put(id,lock);
    }
    public Object removeLock(String id){
        return lockMap.remove(id);
    }
    public Object getLock(String id){
        return lockMap.get(id);
    }
    public void addResult(ResultInfo resultInfo){
        if(resultInfo!=null){
            Object lock = getLock(resultInfo.getId());
            synchronized (lock){
                map.put(resultInfo.getId(),resultInfo);
                lock.notify();
            }
        }
    }
    public ResultInfo getResult(String id, TcpClient client){
        Object lock = getLock(id);
        synchronized (lock){
            if(map.containsKey(id)){
                removeLock(id);
                return map.remove(id);
            }
            try {
                lock.wait();
            } catch (InterruptedException e) {
                log.info("getResult thread is interrupted");
            }

        }
        if(map.containsKey(id)){
            removeLock(id);
            return map.remove(id);
        }
        return null;
//        Long start= TimeCacheUtils.getCacheTime();
//        Long second=100L;
//        while (start+second>TimeCacheUtils.getCacheTime()){
//            if(map.containsKey(id)){
//                return map.remove(id);
//            }
//        }
//        for (;;){
//            if(map.containsKey(id)){
//               return map.remove(id);
//            }
//            client.whetherConnect();
//            try {
//                TimeUnit.MILLISECONDS.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
