package com.ll.entity;

import com.ll.Utils.StringCustomUtils;
import com.ll.annotation.IClient;
import com.ll.constant.ClientConstant;
import org.springframework.util.StringUtils;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/3 16:46
 */
public class IClientInfo {
    private String value;
    private String project;

    public IClientInfo(IClient iClient){
        this.value=iClient.value();
        this.project=iClient.project();
    }
    public String getValue() {
       return value;
    }


    public String getProject() {
        if(StringUtils.isEmpty(this.project)){
            return ClientConstant.DEFAULT_PROJECT_NAME;
        }
        return project;
    }


}
