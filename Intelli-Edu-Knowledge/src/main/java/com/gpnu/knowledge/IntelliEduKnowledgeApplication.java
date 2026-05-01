package com.gpnu.knowledge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(basePackages = {
        "com.gpnu.knowledge",
})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.gpnu.api.client")
@EnableScheduling
public class IntelliEduKnowledgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliEduKnowledgeApplication.class, args);
    }

}
