package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
@Profile("test")
@RequiredArgsConstructor
@Slf4j
public class H2ConsoleConfig {

    private final H2Properties properties;
    private Server webServer;

    @EventListener
    public void start(ContextRefreshedEvent event) throws SQLException{
        log.info("h2 port : {}", properties.getPort());
        this.webServer = Server.createWebServer("-webPort", properties.getPort(), "-tcpAllowOthers").start();
    }

    @EventListener
    public void stop(ContextClosedEvent event){
        this.webServer.stop();

    }

}

