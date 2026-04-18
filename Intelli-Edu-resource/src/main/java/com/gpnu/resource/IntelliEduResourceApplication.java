package com.gpnu.resource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@MapperScan("com.gpnu.resource.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.gpnu.api.client")
public class IntelliEduResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliEduResourceApplication.class, args);
    }

}
