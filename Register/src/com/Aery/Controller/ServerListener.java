package com.Aery.Controller;

import com.Aery.Model.SocketMsg;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class ServerListener extends Thread{

    @Override
    public void run(){
        Listen();
    }

    // 一直循环监听的线程
    public void Listen(){
        try {
            while(!closeFlag){
                ServerSocket ss = new ServerSocket(30000);
                System.out.println("监听....");
                Socket s = ss.accept();
                System.out.println("Server:"+s.getInetAddress().getLocalHost()+"已连接到 Register");

                String ip = s.getInetAddress().getHostAddress();

                // 反序列化 Server 发送来的消息
                var reader = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
                String string = (String) reader.readObject();
                SocketMsg msg = JSONObject.parseObject(string, SocketMsg.class);

                System.out.println("Server Msg："+ string);
                ReceiveServerMsg(msg,ip);

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 当有一个 server 来主动连接本Register时
    // 1. 更新 Server列表
    // 2. 数据库更新 Server 数据
    public void ReceiveServerMsg(SocketMsg msg, String ip) throws SQLException {
        // 更新 Server列表
        var info = ServePool.addServer(msg,ip);
        // 数据库更新 Server 数据
        DBManager.updateServer(info,ip);
    }
    public boolean closeFlag = false;
}
