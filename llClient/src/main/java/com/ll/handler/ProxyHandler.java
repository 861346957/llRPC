package com.ll.handler;

import com.ll.Utils.IdWorker;
import com.ll.Utils.StringCustomUtils;
import com.ll.annotation.IAsync;
import com.ll.annotation.IClient;
import com.ll.annotation.ISync;
import com.ll.client.ClientContext;
import com.ll.client.ResultContext;
import com.ll.constant.ClientConstant;
import com.ll.entity.AsyncResult;
import com.ll.entity.BeanInfo;
import com.ll.entity.IClientInfo;
import com.ll.entity.ResultInfo;
import com.ll.network.TcpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * @author liang.liu
 * @date createTime：2021/4/30 14:21
 */
public class ProxyHandler<T> implements InvocationHandler {
    private  Class<T> interfaceType;
    private static Logger log= LoggerFactory.getLogger(ProxyHandler.class);
    public ProxyHandler(Class<T> interfaceType) {
        this.interfaceType = interfaceType;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        IClientInfo iClientInfo = new IClientInfo(interfaceType.getAnnotation(IClient.class));

        String className=getClassName(iClientInfo.getValue());
        String id=IdWorker.getIdWorker().nextId();
        BeanInfo beanInfo = new BeanInfo(id,className, method.getName(), args, method.getParameterTypes());
        TcpClient client = ClientContext.getClinetContext().getClient(iClientInfo.getProject());
        client.sendMessage(beanInfo);
        if(method.isAnnotationPresent(IAsync.class)){
            return AsyncResult.createAsyncResult(id,client);
        }else{
            ResultInfo resultInfo=ResultContext.getInstance().getResult(id,null);
            if(ClientConstant.FAIL.equals(resultInfo.getStatus())){
                log.info("execute method is error:"+resultInfo.getErrorMessage());
            }
            return resultInfo.getResult() ;
        }

    }
    private String getClassName( String annotationValue){
        if(StringUtils.isEmpty(annotationValue)){
            return  StringCustomUtils.getClassName(interfaceType);
        }
        return annotationValue;
    }

}
