package com.ll.Utils;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/2 10:21
 */
public class TimeCacheUtils {
    public static  Long getCacheTime(){
       return  ExpireCacheUtils.getData("getCacheTime", new ExpireCacheUtils.Load<Long>() {
            @Override
            public Long loadData() {
                return System.currentTimeMillis();
            }
        },100L);
    }
}
