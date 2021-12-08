package com.Aery;

// 假定Register服务器IP固定为127.0.0.1，端口号固定为30000

public class SocketMsg implements java.io.Serializable{
    public int isOnline;    // 1代表下线消息，0代表下线消息
    public int isServe;     // 1代表服务器S，0代表客户端
    public String name;     // 服务器/客户名字
    public int maxOverload; // 最大承载量；特殊地，客户端填0
    public int port;        // 本实例端口号
}
