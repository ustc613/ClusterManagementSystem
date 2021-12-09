package cn.edu.ustc.socketserver.model.support;

// 假定Register服务器IP固定为127.0.0.1，端口号固定为30000

import java.io.Serializable;

public class SocketMsg implements Serializable {
    public int isOnline;    // 0代表下线消息，1代表下线消息
    public int isServe;     // 1代表服务器S，0代表客户端
    public String name;     // 服务器/客户名字
    public int maxOverload; // 最大承载量；特殊地，客户端填0
    public int socketPort;
    public int urlPort;     // 本实例端口号
}
