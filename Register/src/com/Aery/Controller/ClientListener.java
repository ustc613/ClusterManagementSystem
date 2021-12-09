package com.Aery.Controller;

import com.Aery.Model.ServerInfo;
import com.Aery.Model.ServerInfoMsg;
import com.Aery.Model.SocketMsg;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class ClientListener extends Thread {

    @Override
    public void run(){
        Listen();
    }

    // 一直循环监听的线程
    public void Listen(){
        try {
            ServerSocket ss = new ServerSocket(30001);
            while(!closeFlag){
                System.out.println("监听....");
                Socket s = ss.accept();
                System.out.println("Client:"+s.getInetAddress().getLocalHost()+"已连接到 Register");

                String ip = s.getInetAddress().getHostAddress();

                // 反序列化 Server 发送来的消息
                var reader = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
                String string = (String) reader.readObject();
                SocketMsg msg = JSONObject.parseObject(string, SocketMsg.class);

                System.out.println("Client Msg："+ msg);
                ReceiveClientMsg(msg,ip);

                System.out.println("Client 成功处理");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 当有一个 client 来主动连接本Register时
    // 1. 从 Server 列表 选出一个 Server IP/port
    // 2. 数据库更新 client 数据
    // 3. 把 Server IP/port 消息通知 client （UDP短连接）
    public void ReceiveClientMsg(SocketMsg msg, String ip) throws SQLException {
        // 从 Server 列表 选出一个 Server IP/port
        ServerInfo s = ServePool.selectOneServer();
        s.currentOverload += 1;
        // 把 Server IP/port 消息通知 client （UDP短连接）
        SendServerInfoToClient(s,msg,ip);
        // 数据库更新 client 数据
        DBManager.insertClient(msg,s.msg.name,ip);
        // 数据库更新 server 数据
        DBManager.updateServer(s);
    }

    // 返还分配 Server IP/port 消息给 client
    public void SendServerInfoToClient(ServerInfo serverInfo,SocketMsg clientMsg,String ip){
        try {
            ServerInfoMsg msg = new ServerInfoMsg();
            msg.ip = serverInfo.ip;
            msg.port = serverInfo.msg.port;
            msg.serverName = serverInfo.msg.name;
            // TODO 该发给哪个 port?
            Socket s = new Socket(ip,clientMsg.port);
            OutputStream os = s.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            //向Client端发送一条消息
            bw.write(JSONObject.toJSONString(msg));
            bw.flush();
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean closeFlag = false;
}
