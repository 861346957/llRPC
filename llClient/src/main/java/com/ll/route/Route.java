package com.ll.route;

import com.ll.network.TcpClient;

import java.util.List;
import java.util.Map;

/**
 *
 * @author liang.liu
 * @date createTime：2021/5/4 13:56
 */
public interface Route {
    TcpClient getMachine(List<TcpClient> clientList);
}
