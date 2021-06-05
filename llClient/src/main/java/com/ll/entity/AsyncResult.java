package com.ll.entity;

import com.ll.client.ResultContext;
import com.ll.constant.ClientConstant;
import com.ll.network.TcpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/3 15:27
 */
public class AsyncResult<T> {
    private String key;
    private TcpClient client;
    private static Logger log= LoggerFactory.getLogger(AsyncResult.class);
    public AsyncResult(String key,TcpClient client) {
        this.key = key;
        this.client=client;
    }
    public static AsyncResult createAsyncResult(String key,TcpClient client){
        return new AsyncResult(key,client);
    }
    public T getResult(){
        return getResult(null);
    }
    public T getResult(Long overTime){
        ResultInfo resultInfo= ResultContext.getInstance().getResult(key,overTime);
        if(ClientConstant.FAIL.equals(resultInfo.getStatus())){
            log.info("execute method is error:"+resultInfo.getErrorMessage());
            throw new RuntimeException(resultInfo.getErrorMessage());
        }
        return (T)resultInfo.getResult() ;
    };
}
