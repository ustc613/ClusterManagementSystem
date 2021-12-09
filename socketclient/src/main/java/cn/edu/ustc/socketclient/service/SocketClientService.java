package cn.edu.ustc.socketclient.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class SocketClientService {

    public void createSocketClient() {
        // 上报register该客户端上线
        try (Socket sock = new Socket("localhost", socketServerConfig.socketPort);
             InputStream input = sock.getInputStream();
             OutputStream output = sock.getOutputStream()) {
            sock.setKeepAlive(true);
            handle(input, output);
        } catch (Exception e) {
            log.error("failed to connect server, reason:" + e.getMessage());
            // 上报register该客户端与服务端连接失败
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
