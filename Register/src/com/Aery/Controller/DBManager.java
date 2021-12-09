package com.Aery.Controller;

import com.Aery.Model.ServerInfo;
import com.Aery.Model.SocketMsg;

import java.sql.*;

public class DBManager {

    public static final String URL = "jdbc:mysql://localhost:3306/monitor";
    public static final String USER = "root";
    public static final String PASSWORD = "123456";

    public static Connection conn;

    // 初始化连接 DB
    public static void init() throws ClassNotFoundException, SQLException {
        //1. 加载驱动程序
        Class.forName("com.mysql.cj.jdbc.Driver");
        //2. 获得数据库连接
        conn = DriverManager.getConnection(URL, USER, PASSWORD);
        System.out.println("连接monitor数据库成功！");
    }

    // 插入数据库的一行 Serve 数据
    public static void updateServer(ServerInfo info, String ip) throws SQLException {
        String sql =" if not exists (select 1 from server_info where name = ?)" +
                    "      insert into server_info(ip,name,maxcount,nowcount,ontime,socketport) values(?,?,?,?,getdate(),?)" +
                    "   else" +
                    "      update server_info set (ip,maxcount,nowcount,ontime,socketport)  = (?,?,?,getdate(),?) " +
                    "   where name = ?"
                ;

        PreparedStatement ptm = conn.prepareStatement(sql); //预编译SQL，减少sql执行

        //传参
        ptm.setString(1, info.msg.name);

        ptm.setString(2, ip);
        ptm.setString(3, info.msg.name);
        ptm.setInt(4, info.msg.maxOverload);
        ptm.setInt(5, info.currentOverload);
        ptm.setInt(6, info.msg.port);

        ptm.setString(7, ip);
        ptm.setInt(8, info.msg.maxOverload);
        ptm.setInt(9, info.currentOverload);
        ptm.setInt(10, info.msg.port);

        ptm.setString(11, info.msg.name);

        //执行
        ptm.execute();
    }

    // 更新数据库的一行 Serve 数据
    public static void updateServer(ServerInfo info) throws SQLException {
        String sql =" update server_info set (nowcount)  = (?) where name = ?";
        PreparedStatement ptm = conn.prepareStatement(sql); //预编译SQL，减少sql执行
        //传参
        ptm.setInt(1, info.currentOverload);
        ptm.setString(2, info.msg.name);
        //执行
        ptm.execute();
    }

    // 更新数据库的一行 Client 数据
    public static void insertClient(SocketMsg msg,String serve, String ip) throws SQLException {
        String sql =" if not exists (select 1 from client_info where name = ?)" +
                "      insert into client_info(ip,name,servername,nowcount,clientPort,status) values(?,?,?,?,?,?)" +
                "   else" +
                "      update client_info set (ip,servername,nowcount,clientPort,status) = (?,?,?,?,?) where where name = ?"
                ;

        PreparedStatement ptm = conn.prepareStatement(sql); //预编译SQL，减少sql执行

        //传参
        ptm.setString(1, msg.name);

        ptm.setString(2, ip);
        ptm.setString(3, serve);
        ptm.setInt(4, 1);
        ptm.setInt(5, msg.port);
        ptm.setInt(6, msg.isOnline);

        ptm.setString(7, ip);
        ptm.setString(8, serve);
        ptm.setInt(9, 1);
        ptm.setInt(10, msg.port);
        ptm.setInt(11, msg.isOnline);

        ptm.setString(12, msg.name);

        //执行
        ptm.execute();
    }
}
