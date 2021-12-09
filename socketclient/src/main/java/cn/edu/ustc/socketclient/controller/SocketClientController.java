package cn.edu.ustc.socketclient.controller;

import cn.edu.ustc.socketclient.model.dto.SocketClientDto;
import cn.edu.ustc.socketclient.service.SocketClientService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/socket-client")
public class SocketClientController {
    SocketClientService socketClientService;

    public SocketClientController(SocketClientService socketClientService) {
        this.socketClientService = socketClientService;
    }

    @PostMapping("/offline")
    public boolean offline(@RequestBody SocketClientDto socketServerDto) throws IOException {
        System.out.println(socketServerDto);
        socketClientService.offline(socketServerDto.clientName);
        return true;
    }

    @PostMapping("/online")
    public boolean online(@RequestBody SocketClientDto socketServerDto) {
        socketClientService.online(socketServerDto.clientName);
        return true;
    }
}
