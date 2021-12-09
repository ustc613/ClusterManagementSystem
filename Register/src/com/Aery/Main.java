package com.Aery;

import com.Aery.Controller.ClientListener;
import com.Aery.Controller.DBManager;
import com.Aery.Controller.ServePool;
import com.Aery.Controller.ServerListener;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, ClassNotFoundException, InterruptedException {
        // 初始化DB
        DBManager.init();
        // 初始化 Server 列表
        ServePool.init();
	    // 开始监听线程
        Thread t1 = new ServerListener();
        t1.start();
        Thread t2 = new ClientListener();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Register程序结束！");
    }
}
