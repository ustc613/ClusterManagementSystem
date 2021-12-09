package cn.edu.ustc.socketserver.service;

import cn.edu.ustc.socketserver.config.SocketServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class SocketServiceService {
    SocketServerConfig socketServerConfig;

    public SocketServiceService(SocketServerConfig socketServerConfig) {
        this.socketServerConfig = socketServerConfig;
    }



}
