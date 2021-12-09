package com.Aery.Controller;


import com.Aery.Model.ServerInfo;
import com.Aery.Model.SocketMsg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// 维护一个服务器列表
public class ServePool {
    public static List<ServerInfo> serverList;

    public static void init(){
        serverList = new ArrayList<ServerInfo>();
    }

    // 分配一个可用Server（负载均衡算法）
    public static ServerInfo selectOneServer(){
        if(serverList.isEmpty())return null;
        // 根据 最大overload - 当前overload 来决定优先分配服务器
        serverList.sort(
                new Comparator<ServerInfo>(){
                    @Override
                    public int compare(ServerInfo o1, ServerInfo o2) {
                        boolean res = (o1.msg.maxOverload-o1.currentOverload)>(o2.msg.maxOverload-o2.currentOverload);
                        return (res?1:-1);
                    }
                }
        );
        return serverList.get(0);
    }

    // 上线一个Server
    public static ServerInfo addServer(SocketMsg msg,String ip){
        var info = new ServerInfo();
        info.msg = msg;
        info.ip = ip;
        info.currentOverload = 0;
        serverList.add(info);
        return info;
    }
}
