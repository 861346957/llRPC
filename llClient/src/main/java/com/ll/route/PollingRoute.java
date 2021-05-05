package com.ll.route;

import com.ll.Utils.PollingUtils;
import com.ll.entity.Count;
import com.ll.network.TcpClient;

import java.util.List;


/**
 *
 * @author liang.liu
 * @date createTime：2021/5/3 13:59
 */
public class PollingRoute implements Route {
    private Count count=new Count();

    @Override
    public TcpClient getMachine(List<TcpClient> clientList) {
        Integer index = PollingUtils.getPolling(count, clientList.size(), this);
        return clientList.get(index);
    }
}
