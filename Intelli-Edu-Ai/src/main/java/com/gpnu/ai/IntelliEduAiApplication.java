package com.gpnu.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.gpnu.**.mapper")
@EnableDiscoveryClient
public class IntelliEduAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliEduAiApplication.class, args);
    }

}
