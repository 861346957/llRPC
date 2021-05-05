package com.ll.utils;

import com.ll.Utils.StringCustomUtils;
import com.ll.constant.ClientConstant;
import com.ll.entity.BeanInfo;
import com.ll.entity.ResultInfo;
import com.ll.serve.ChannelContext;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 16:02
 */
public class TaskExe {
    private static Logger log= LoggerFactory.getLogger(TaskExe.class);

    public static void executeMethod(BeanInfo beanInfo, String key){
        ResultInfo resultInfo = getMethodResult(beanInfo);
        ChannelContext.getInstance().sendMessage(resultInfo,key);
    }
    private static ResultInfo getMethodResult(BeanInfo beanInfo){
        Object result=null;
        try {
            Object bean = SpringBootBeanUtil.getBean(beanInfo.getClassName());
            Method method = bean.getClass().getMethod(beanInfo.getMethodName(),beanInfo.getParamTypes());
            result = method.invoke(bean, beanInfo.getParams());
        } catch (Throwable e) {
            log.info(StringCustomUtils.getErrorMessage(e));
            return new ResultInfo(beanInfo.getId(),result, ClientConstant.FAIL,StringCustomUtils.getErrorMessage(e));
        }
        return new ResultInfo(beanInfo.getId(),result);
    }
}
