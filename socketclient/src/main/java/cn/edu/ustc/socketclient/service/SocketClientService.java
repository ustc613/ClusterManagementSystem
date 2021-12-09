package cn.edu.ustc.socketclient.service;

import cn.edu.ustc.socketclient.model.support.ServerInfoMsg;
import cn.edu.ustc.socketclient.model.support.SocketMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SocketClientService {

    private ConcurrentHashMap<String, Socket> socketMap = new ConcurrentHashMap<>();

    public void online(String serverName) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                createSocketClient(serverName);
            }
        });
        t.start();
    }

    public void offline(String serverName) throws IOException {
        if (!socketMap.contains(serverName)) {
            throw new IllegalArgumentException("client:" + serverName + "not exist");
        }
        Socket socket = socketMap.get(serverName);
        //上报register下线消息
        messageToRegister(new SocketMsg(0, 0, serverName, 0, 0));
        socket.close();
        socketMap.remove(serverName);

    }

    private ServerInfoMsg messageToRegister(SocketMsg socketMsg) {
        try (Socket sock = new Socket("localhost", 30000);
             InputStream input = sock.getInputStream();
             OutputStream output = sock.getOutputStream()) {
            log.info("connect register success.");
            ObjectOutputStream writer = new ObjectOutputStream(output);
            var reader = new ObjectInputStream(new BufferedInputStream(input));
            writer.writeObject(socketMsg);
            writer.flush();

            if (socketMsg.isOnline == 1) {
                return (ServerInfoMsg) reader.readObject();
            }

        } catch (Exception e) {
            log.error("failed to connect register, reason:" + e.getMessage());
        }
        return null;
    }

    private void createSocketClient(String serverName) {
        //上报register该客户端上线
        ServerInfoMsg serverInfoMsg =
                messageToRegister(new SocketMsg(1, 0, serverName, 0, 0));
        if (serverInfoMsg == null) {
            throw new RuntimeException("failed to get server info. ");
        }
        try (Socket sock = new Socket(serverInfoMsg.ip, serverInfoMsg.port);
             InputStream input = sock.getInputStream();
             OutputStream output = sock.getOutputStream()) {
            log.info("connect server success.");
            socketMap.put(serverName, sock);
            sock.setKeepAlive(true);
            handle(input, output);
        } catch (Exception e) {
            log.error("failed to connect server, reason:" + e.getMessage());
            //上报register该客户端与服务端连接失败
            messageToRegister(new SocketMsg(0, 0, serverName, 0, 0));
        }
    }

    private static void handle(InputStream input, OutputStream output) throws IOException {
        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        log.info("[server] " + reader.readLine());
        for (;;) {
            writer.write("pong");
            writer.newLine();
            writer.flush();
            String resp = reader.readLine();
            log.info("[server] " + resp);
            if (!resp.equals("ping")) {
                throw new IOException("cannot get 'ping' response from server");
            }
        }
    }
}
