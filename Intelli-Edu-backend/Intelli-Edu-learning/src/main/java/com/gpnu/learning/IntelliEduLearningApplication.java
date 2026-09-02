package com.gpnu.learning;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan(basePackages = {
        "com.gpnu.learning.mapper",
})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.gpnu.api.client")
public class IntelliEduLearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliEduLearningApplication.class, args);
    }

}
