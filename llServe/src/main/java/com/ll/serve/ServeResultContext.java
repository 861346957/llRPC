package com.ll.serve;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.Utils.ExpireCacheUtils;
import com.ll.constant.ClientConstant;
import com.ll.entity.BeanInfo;
import com.ll.entity.TaskQueue;
import com.ll.thread.ServeWork;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/3 14:20
 */
public class ServeResultContext {
    private Map<String, LinkedBlockingQueue<BeanInfo>> resultMap;
    private Map<String, Integer> readAndWriteOverTime;
    private ObjectMapper mapper;
    private static ServeResultContext serveResultContext;
    private ServeResultContext() {
        resultMap=new ConcurrentHashMap<>();
        readAndWriteOverTime=new ConcurrentHashMap<>();
        mapper=new ObjectMapper();
    }
    public static ServeResultContext getInstance(){
        if(serveResultContext==null){
            synchronized (ServeResultContext.class){
                if(serveResultContext==null){
                    serveResultContext=new ServeResultContext();
                }
            }
        }
        return serveResultContext;
    }
    public void addQueue(String key){
        if(!resultMap.containsKey(key)){
            resultMap.put(key,new LinkedBlockingQueue<BeanInfo>());
        }
    }
    public synchronized void initReadAndWriteOverTime(String key){
        readAndWriteOverTime.remove(key);
    }
    public synchronized void increasingReadAndWriteOverTime(String key){
        Integer integer = readAndWriteOverTime.get(key);
        if(integer==null){
            integer=0;
        }
        readAndWriteOverTime.put(key,++integer);
    }
    public Integer getReadAndWriteOverTimeCount(String key){
        return readAndWriteOverTime.get(key);
    }
    public synchronized Boolean removeQueue(String key){
        if(ClientConstant.READ_WRITE_OVERTIME_COUNT.equals(getReadAndWriteOverTimeCount(key))){
            resultMap.remove(key);
            readAndWriteOverTime.remove(key);
            return true;
        }
        return false;
    }
    public void addResult(String message,String key){
        try {
            BeanInfo beanInfo=mapper.readValue(message,new TypeReference<BeanInfo>(){});
            resultMap.get(key).offer(beanInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void addResult(BeanInfo beanInfo,String key){
        initReadAndWriteOverTime(key);
        if(beanInfo.getHeartbeat()){
            return;
        }
        resultMap.get(key).offer(beanInfo);
    }
    public LinkedBlockingQueue<BeanInfo> getQueue(String key){
        return resultMap.get(key);
    }

    public Map<String, LinkedBlockingQueue<BeanInfo>> getResultMap() {
        return resultMap;
    }
    public  List<TaskQueue> getTaskQueueList(){
        return ExpireCacheUtils.getData("getTaskQueueList", new ExpireCacheUtils.Load<List<TaskQueue>>() {
            @Override
            public List<TaskQueue> loadData() {
                List<TaskQueue> taskQueues=new ArrayList<>();
                for (Map.Entry<String, LinkedBlockingQueue<BeanInfo>> entry : resultMap.entrySet()) {
                    taskQueues.add(new TaskQueue(entry.getKey(),entry.getValue()));
                }
                return taskQueues;
            }
        },10);
    }
    public Integer getQueueSize(){
        return ExpireCacheUtils.getData("getQueueSize", new ExpireCacheUtils.Load<Integer>() {
            @Override
            public Integer loadData() {
                Integer size=0;
                for (LinkedBlockingQueue<BeanInfo> value : resultMap.values()) {
                    size+=value.size();
                }
                return size;
            }
        },1);
    }
}
