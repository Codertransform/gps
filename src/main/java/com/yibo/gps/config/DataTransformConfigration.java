package com.yibo.gps.config;

import com.yibo.gps.handler.TransformHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.server.HttpServer;

@Configuration
public class DataTransformConfigration {

    @Bean
    CommandLineRunner httpServerRunner(TransformHandler transformHandler) {
        return strings -> {
            createTcpServer(transformHandler);
        };
    }

    private void createTcpServer(TransformHandler transformHandler){
        HttpServer.create()
                .port(8080)
                .bindNow();
    }
}
