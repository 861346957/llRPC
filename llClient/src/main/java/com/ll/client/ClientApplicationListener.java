package com.ll.client;

import com.ll.Utils.StringCustomUtils;
import com.ll.constant.ClientConstant;
import com.ll.context.ConfirmContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/1 9:17
 */
@Component
public class ClientApplicationListener implements ApplicationListener<ContextRefreshedEvent> , EnvironmentAware {
    private String hosts;
    private String routeClass;
    private Integer isConfirm;
    private Integer confirmOverTime;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent()==null){
            //WebServerInitializedEvent event=
           ClientContext.setClientContext(new ClientContext(hosts,routeClass,isConfirm,confirmOverTime));
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        String hosts = environment.getProperty("llc.hosts");
        String route = environment.getProperty("llc.route");
        Integer isConfirm = StringCustomUtils.getInteger(environment.getProperty("llc.isConfirm"));
        Integer confirmOverTime = StringCustomUtils.getInteger(environment.getProperty("llc.confirmOverTime"));
        this.hosts= hosts;
        this.routeClass= StringUtils.isEmpty(route) ? ClientConstant.DEFAULT_ROUTE_CLASS :route;
        this.isConfirm= isConfirm==null ? ClientConstant.NO_CONFIRM :isConfirm;
        this.confirmOverTime= confirmOverTime==null ? ClientConstant.CONFIRM_OVER_TIME :confirmOverTime;
    }
}
