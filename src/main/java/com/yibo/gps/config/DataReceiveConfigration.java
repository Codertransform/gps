package com.yibo.gps.config;

import com.yibo.gps.handler.TcpDecoderHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.netty.tcp.TcpServer;

@Configuration
public class DataReceiveConfigration {

    @Bean
    CommandLineRunner serverRunner(TcpDecoderHandler tcpDecoderHandler) {
        return strings -> {
            createTcpServer(tcpDecoderHandler);
        };
    }

    private void createTcpServer(TcpDecoderHandler tcpDecoderHandler){
        TcpServer.create().handle((in,out) -> {
                    in.receive()
                            .asByteArray()
                            .subscribe();
                    return Flux.never();
                })
                //实例只写了如何添加handler,可添加delimiter，tcp生命周期，decoder，encoder等handler
                .doOnConnection(conn ->conn.addHandler(tcpDecoderHandler))
                .port(8081)
                .bindNow();
    }
}
