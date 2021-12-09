package cn.edu.ustc.socketserver.util;

import cn.edu.ustc.socketserver.model.support.ServerInfoMsg;
import cn.edu.ustc.socketserver.model.support.SocketMsg;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

@Slf4j
public class RegisterUtil {
    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(30000)) {
            log.info("启动socket register 测试服务器...");
            for (;;) {
                Socket sock = server.accept();
                log.info("connected from " + sock.getRemoteSocketAddress());
                Thread t = new RegisterHandler(sock);
                t.start();
            }
        } catch (Exception e) {
            log.error("socket register意外关闭，原因：" + e.getMessage());
        }
    }
}

@Slf4j
class RegisterHandler extends Thread {
    Socket sock;

    public RegisterHandler(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run() {
        try (InputStream input = this.sock.getInputStream();
             OutputStream output = this.sock.getOutputStream()) {
            handle(input, output);
        } catch (Exception e) {
            try {
                this.sock.close();
            } catch (IOException ioe) {
            }
            log.error("disconnected. reason:" + e.getMessage());
        }
    }

    private void handle(InputStream input, OutputStream output) throws IOException, InterruptedException, ClassNotFoundException {
        var reader = new ObjectInputStream(new BufferedInputStream(sock.getInputStream()));
        var writer = new ObjectOutputStream(sock.getOutputStream());
        String msg = (String) reader.readObject();
        SocketMsg socketMsg = JSONObject.parseObject(msg, SocketMsg.class);
        log.info("get msg: " + msg);
        if (socketMsg.isServe == 0) {
            writer.writeObject(
                    JSONObject.toJSONString(new ServerInfoMsg("localhost", 2333, "socket-server1")));
            writer.flush();
        }
    }

}
