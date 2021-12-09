package cn.edu.ustc.socketserver.listener;

import cn.edu.ustc.socketserver.config.SocketServerConfig;
import cn.edu.ustc.socketserver.model.support.SocketMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class SocketServerListener implements ApplicationListener<ApplicationReadyEvent> {
    SocketServerConfig socketServerConfig;

    @Value("${server.port}")
    String urlPort;

    public SocketServerListener(SocketServerConfig socketServerConfig) {
        this.socketServerConfig = socketServerConfig;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try (ServerSocket server = new ServerSocket(socketServerConfig.socketPort)) {
            log.info("启动socket server服务器...");
            //上报register该服务器上线
            messageToRegister(new SocketMsg(1, 1, socketServerConfig.name, socketServerConfig.maxOverload,
                    socketServerConfig.socketPort));
            for (;;) {
                Socket sock = server.accept();
                sock.setKeepAlive(true);
                log.info("connected from " + sock.getRemoteSocketAddress());
                Thread t = new ServerHandler(sock);
                t.start();
            }
        } catch (Exception e) {
            //报register该服务器下线
            messageToRegister(new SocketMsg(0, 1, socketServerConfig.name, socketServerConfig.maxOverload,
                    socketServerConfig.socketPort));
            e.printStackTrace();
            log.error("socket服务器意外关闭，原因：" + e.getMessage());
        }
    }

    private void messageToRegister(SocketMsg socketMsg) {
        try (Socket sock = new Socket("localhost", 30000);
             InputStream input = sock.getInputStream();
             OutputStream output = sock.getOutputStream()) {
            log.info("connect register success.");
            ObjectOutputStream writer = new ObjectOutputStream(sock.getOutputStream());
            writer.writeObject(socketMsg);
            writer.flush();
        } catch (Exception e) {
            log.error("failed to connect register, reason:" + e.getMessage());
        }
    }
}

@Slf4j
class ServerHandler extends Thread {
    Socket sock;

    public ServerHandler(Socket sock) {
        this.sock = sock;
    }

    @Override
    public void run() {
        try (InputStream input = this.sock.getInputStream()) {
            try (OutputStream output = this.sock.getOutputStream()) {
                handle(input, output);
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                this.sock.close();
            } catch (IOException ioe) {
            }
            log.error("client disconnected. reason:" + e.getMessage());
        }
    }

    private void handle(InputStream input, OutputStream output) throws IOException, InterruptedException {
        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        writer.write("ping\n");
        writer.flush();
        for (;;) {
            String s = reader.readLine();
            if (!s.equals("pong")) {
                throw new IOException("failed to connected" + sock.getRemoteSocketAddress());
            }
            // 心跳检测，每10秒发一次消息，未收到回复中断连接
            Thread.sleep(10000);
            writer.write("ping\n");
            writer.flush();
        }
    }

}
