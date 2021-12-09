package cn.edu.ustc.socketclient.service;

import cn.edu.ustc.socketclient.model.support.ServerInfoMsg;
import cn.edu.ustc.socketclient.model.support.SocketMsg;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service()
public class SocketClientService {

    private ConcurrentHashMap<String, Socket> socketMap = new ConcurrentHashMap<>();

    public void online(String clientName) {
        if (socketMap.containsKey(clientName)) {
            throw new IllegalArgumentException("client:" + clientName + " is already exist");
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                createSocketClient(clientName);
            }
        });
        t.start();
    }

    public void offline(String clientName) throws IOException {
        log.info(String.valueOf(socketMap));
        if (!socketMap.containsKey(clientName)) {
            throw new IllegalArgumentException("client:" + clientName + " not exist");
        }
        Socket socket = socketMap.get(clientName);
        //上报register下线消息
        messageToRegister(new SocketMsg(0, 0, clientName, 0, 0));
        socket.close();
        socketMap.remove(clientName);

    }

    private ServerInfoMsg messageToRegister(SocketMsg socketMsg) {
        try (Socket sock = new Socket("localhost", 30000);
             InputStream input = sock.getInputStream();
             OutputStream output = sock.getOutputStream()) {
            log.info("connect register success.");
            ObjectOutputStream writer = new ObjectOutputStream(output);
            var reader = new ObjectInputStream(new BufferedInputStream(input));
            writer.writeObject(JSONObject.toJSONString(socketMsg));
            writer.flush();

            if (socketMsg.isOnline == 1) {
                String msg = (String) reader.readObject();
                log.info("get server info success: " + msg);
                return JSONObject.parseObject(msg, ServerInfoMsg.class);
            }

        } catch (Exception e) {
            log.error("failed to connect register, reason:" + e.getMessage());
        }
        return null;
    }

    private void createSocketClient(String clientName) {
        //上报register该客户端上线
        ServerInfoMsg serverInfoMsg =
                messageToRegister(new SocketMsg(1, 0, clientName, 0, 0));
        if (serverInfoMsg == null) {
            throw new RuntimeException("failed to connect to server. server is busy now.");
        }
        try (Socket sock = new Socket(serverInfoMsg.ip, serverInfoMsg.port);
             InputStream input = sock.getInputStream();
             OutputStream output = sock.getOutputStream()) {
            log.info("connect server success.");
            socketMap.put(clientName, sock);
            sock.setKeepAlive(true);
            handle(input, output, clientName, serverInfoMsg.serverName);
        } catch (Exception e) {
            log.error("failed to connect server, reason:" + e.getMessage());
            //上报register该客户端与服务端连接失败
            if (!StringUtils.equals(e.getMessage(), "Socket closed")) {
                messageToRegister(new SocketMsg(0, 0, clientName, 0, 0));
            }
        }
    }

    private static void handle(InputStream input, OutputStream output, String clientName, String serverName)
            throws IOException {
        var writer = new BufferedWriter(new OutputStreamWriter(output, StandardCharsets.UTF_8));
        var reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        reader.readLine();
        for (;;) {
            writer.write("pong");
            writer.newLine();
            writer.flush();
            String resp = reader.readLine();
            log.info("[client:" + clientName + "] get msg:" + resp + " from server:" + serverName);
            if (!resp.equals("ping")) {
                throw new IOException("cannot get 'ping' response from server");
            }
        }
    }
}
