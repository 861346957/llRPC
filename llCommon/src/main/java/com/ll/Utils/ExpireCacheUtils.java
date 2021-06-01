package com.ll.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 16:07
 */
public class ExpireCacheUtils<T> {
    private static String SEPARATORS="-";
    private static volatile Map<String,CacheData> EXPIRE_CACHE_DATA = new HashMap<>();

    /**
     *
     * @param key
     * @param load
     * @param second
     * @param <T>
     * @return
     */
    public static  <T> T getData(String key, Load<T> load, int second){
       return getData(key,load,second,null);
    }
    public static  <T> T getData(String key, Load<T> load, int second,String date){
        CacheData<T> cacheData;
        cacheData = EXPIRE_CACHE_DATA.get(key);
        if(cacheData==null || cacheData.getExpire()<System.currentTimeMillis()){
            synchronized (key.intern()){
                 cacheData = EXPIRE_CACHE_DATA.get(key);
                if(cacheData==null || cacheData.getExpire()<System.currentTimeMillis()){
                    T data = load.loadData();
                    long start=System.currentTimeMillis();
                    long expire=getTimeMillis(second);
                    if(date!=null){
                        expire=getMillisecondValue(date);
                    }else{
                        expire=start+expire;
                    }
                    cacheData = new CacheData<T>(data,start, expire);
                    EXPIRE_CACHE_DATA.put(key,cacheData);
                }
            }
        }
        return cacheData.getData();
    }

    public static  <T> T getData(String key, Load<T> load, Long second){
        CacheData<T> cacheData;
        cacheData = EXPIRE_CACHE_DATA.get(key);
        if(cacheData==null || cacheData.getExpire()<System.currentTimeMillis()){
            synchronized (key.intern()){
                cacheData = EXPIRE_CACHE_DATA.get(key);
                if(cacheData==null || cacheData.getExpire()<System.currentTimeMillis()){
                    T data = load.loadData();
                    long start=System.currentTimeMillis();
                    second=start+second;
                    cacheData = new CacheData<T>(data,start, second);
                    EXPIRE_CACHE_DATA.put(key,cacheData);
                }
            }
        }
        return cacheData.getData();
    }

    /**
     *
     * @param key
     * @param load
     * @param second 如果成功设置了新的数据，这就是超时时间
     * @param interval 这个间隔时间内的，使用旧数据
     * @param <T>
     * @return
     */
    public static <T> T getAndSetData(String key,Load<T> load, int second,int interval){
        CacheData<T> cacheData = EXPIRE_CACHE_DATA.get(key);
        if(cacheData!=null && cacheData.getStartTime()+getTimeMillis(interval)>System.currentTimeMillis()){
            return cacheData.getData();
        }
        long start=System.currentTimeMillis();
        synchronized (key.intern()){
            cacheData = EXPIRE_CACHE_DATA.get(key);
            if(cacheData!=null && cacheData.getStartTime()+getTimeMillis(interval)>System.currentTimeMillis()){
                return cacheData.getData();
            }
            T data = load.loadData();
            cacheData = new CacheData<T>(data,start, start+getTimeMillis(second));
            EXPIRE_CACHE_DATA.put(key,cacheData);
            return data;
        }
    }
    /**
     * 例如：data="09-00-00" 明天9点数据失效
     *      data="07-09-00-00" 7天后9点数据失效
     * 每天超过data时间就重新获取数据
     *
     * @param key
     * @param load
     * @param date
     * @param <T>
     * @return
     */
    public static  <T> T getData(String key, Load<T> load, String date){
        return getData(key,load,0,date);

    }
    private static long getMillisecondValue(String date){
        String[] split = date.split(SEPARATORS);
        int[] timeArray= getIntArray(split);
        return calc(timeArray).getTime();
    }
    private static long getTimeMillis(int second){
        return 1000L*second;
    }

    private static int[] getIntArray(String[] split) {
        int[] timeArray=new int[split.length];
        for (int i = 0; i < split.length; i++) {
            timeArray[i]=Integer.parseInt(split[i]);
        }
        return timeArray;
    }

    public static Date calc(int[] timeArray){
        if(timeArray.length==3){
            return init(1,timeArray[0],timeArray[1],timeArray[2]);
        }
        if(timeArray.length==4){
            return init(timeArray[0],timeArray[1],timeArray[2],timeArray[3]);
        }
        return null;
    }
    public static Date init(int day,int hour,int minute,int second){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,day);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTime();
    }
    public  interface Load<T>{
        /**
         * 加载数据
         * @return
         */
        T loadData();
    }
    private static class CacheData<T>{
        private T data;
        private long startTime;
        private long expire;

        public CacheData(T data, long startTime,long expire) {
            this.data = data;
            this.startTime =startTime;
            this.expire =expire;
        }

        public long getStartTime() {
            return startTime;
        }

        public T getData() {
            return data;
        }

        public long getExpire() {
            return expire;
        }
    }

}
