package com.gpnu.auth.config;

import com.gpnu.auth.common.jwt.JwtProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
@ComponentScan(basePackages = "com.gpnu.auth")
public class AuthCommonAutoConfiguration {
}