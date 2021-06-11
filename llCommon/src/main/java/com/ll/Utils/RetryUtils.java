package com.ll.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 14:57
 */
public class RetryUtils<T> {
    private static final Logger logger= LoggerFactory.getLogger(RetryUtils.class);

    /**
     *
     * @param retry
     * @param count 重试次数
     * @param timeOut 超时时间
     * @param awaitTime 等待时间
     */
    public static void retry(Retry retry,Integer count,Integer timeOut,Integer awaitTime){
        Long timeOutLong=timeOut*1000L;
        Long startTime=System.currentTimeMillis();
        for (int i = 1; i <= count; i++) {
            if(startTime+timeOutLong<System.currentTimeMillis()){
                throw new RuntimeException("retry is overTime");
            }
            if(retry.logic()){
                break;
            }
            if(i==count){
                throw new RuntimeException("retry count already expired");
            }
            if(awaitTime!=null){
                try {
                    TimeUnit.SECONDS.sleep(awaitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    /**
     *
     * @param retry
     * @param count 重试次数
     * @param timeOut 超时时间
     * @param awaitTime 等待时间
     */
    public static <T> T retry(ObjectRetry<T> retry,Integer count,Integer timeOut,Integer awaitTime){
        Long timeOutLong=timeOut*1000L;
        Long startTime=System.currentTimeMillis();
        for (int i = 1; i <= count; i++) {
            if(startTime+timeOutLong<System.currentTimeMillis()){
                logger.info("retry is overTime");
                break;
            }
            T result=retry.logic();
            if(result!=null){
                return result;
            }
            if(i==count){
                logger.info("retry count already expired");
                break;
            }
            if(awaitTime!=null){
                try {
                    TimeUnit.SECONDS.sleep(awaitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public interface Retry{
        boolean logic();
    }
    public interface ObjectRetry<T>{
        T logic();
    }
}
