package cn.edu.ustc.socketserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("socket")
@Data
public class SocketServerConfig {
    public String name;
    public Integer maxOverload;
    public Integer socketPort;
}
