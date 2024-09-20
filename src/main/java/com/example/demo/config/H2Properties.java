package com.example.demo.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Getter
@Setter
@ConfigurationProperties("spring.h2.console")
@Configuration
@Profile("test")
public class H2Properties {
    private String port;
}