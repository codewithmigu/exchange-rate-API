package com.exchange.api.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "com.exchange")
@PropertySource("classpath:application.properties")
public class TestConfig {
}
