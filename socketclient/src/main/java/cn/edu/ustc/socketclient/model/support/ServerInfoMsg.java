package cn.edu.ustc.socketclient.model.support;

import lombok.AllArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@ToString
@AllArgsConstructor
public class ServerInfoMsg implements Serializable {
    public String ip;
    public int port;
    public String serverName;
}
