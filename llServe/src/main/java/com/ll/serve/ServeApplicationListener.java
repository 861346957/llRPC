package com.ll.serve;

import com.ll.Utils.StringCustomUtils;
import com.ll.constant.ClientConstant;
import com.ll.context.ConfirmContext;
import com.ll.network.TcpServe;
import com.ll.thread.ServeThreadPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/2 15:41
 */
@Component
public class ServeApplicationListener implements ApplicationListener<ContextRefreshedEvent>, EnvironmentAware {
    private Integer monitorPort;
    private Integer coreSize;
    private Integer maxCoreSize;
    private Long expireTime;
    private Integer threshold;
    private Integer readAndWriteTime;
    private Integer isConfirm;
    private Integer confirmOverTime;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent()==null){
            ConfirmContext.init(isConfirm,confirmOverTime);
            new TcpServe(monitorPort,readAndWriteTime);
            new ServeThreadPool(coreSize,maxCoreSize,expireTime,threshold).execute();
        }
    }


    @Override
    public void setEnvironment(Environment environment) {
        Integer monitorPort = StringCustomUtils.getInteger(environment.getProperty("lls.monitorPort"));
        Integer coreSize = StringCustomUtils.getInteger(environment.getProperty("lls.coreSize"));
        Integer maxCoreSize = StringCustomUtils.getInteger(environment.getProperty("lls.maxCoreSize"));
        Long threadExpireTime = StringCustomUtils.getLong(environment.getProperty("lls.threadExpireTime"));
        Integer threshold = StringCustomUtils.getInteger(environment.getProperty("lls.threshold"));
        Integer readAndWriteTime = StringCustomUtils.getInteger(environment.getProperty("lls.readAndWriteTime"));
        Integer isConfirm = StringCustomUtils.getInteger(environment.getProperty("lls.isConfirm"));
        Integer confirmOverTime = StringCustomUtils.getInteger(environment.getProperty("lls.confirmOverTime"));
        this.monitorPort = monitorPort == null ? ClientConstant.DEFAULT_PORT : monitorPort;
        this.coreSize = coreSize == null ? ClientConstant.CORE_SIZE : coreSize;
        this.maxCoreSize = maxCoreSize == null ? ClientConstant.MAX_CORE_SIZE : maxCoreSize;
        this.expireTime = threadExpireTime == null ? ClientConstant.DEFAULT_THREAD_EXPIRE_TIME : threadExpireTime;
        this.threshold = threshold == null ? ClientConstant.DEFAULT_THRESHOLD : threshold;
        this.readAndWriteTime = readAndWriteTime == null ? ClientConstant.DEFAULT_READ_WRITE_TIME : readAndWriteTime;
        this.isConfirm = isConfirm == null ? ClientConstant.NO_CONFIRM : isConfirm;
        this.confirmOverTime = confirmOverTime == null ? ClientConstant.CONFIRM_OVER_TIME : confirmOverTime;
    }
}
