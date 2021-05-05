package com.ll.Utils;

import com.ll.entity.Count;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 9:56
 */
public class PollingUtils {
    public static Integer getPolling(Count count, Integer size, Object lock){
        synchronized (lock){
            count.increasing();
            count.residualCalc(size);
            return count.get();
        }
    }
}
