package cn.edu.ustc.socketserver.controller;

import cn.edu.ustc.socketserver.model.dto.SocketServerDto;
import cn.edu.ustc.socketserver.service.SocketServiceService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/socket-server")
public class SocketServerController {
    SocketServiceService socketServiceService;

    public SocketServerController(SocketServiceService socketServiceService) {
        this.socketServiceService = socketServiceService;
    }

    @PostMapping("/offline")
    public boolean offline(@RequestBody SocketServerDto socketServerDto) {
        System.out.println(socketServerDto);
        return false;
    }

    @PostMapping("/online")
    public boolean online(@RequestBody SocketServerDto socketServerDto) {
        socketServiceService.createSocketClient();
        return false;
    }
}
