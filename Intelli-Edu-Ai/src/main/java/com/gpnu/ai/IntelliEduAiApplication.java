package com.gpnu.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.gpnu.**.mapper")
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.gpnu.ai", "com.gpnu.common"})
public class IntelliEduAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliEduAiApplication.class, args);
    }

}
