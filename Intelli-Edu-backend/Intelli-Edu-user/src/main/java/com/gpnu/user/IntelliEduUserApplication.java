package com.gpnu.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.gpnu.user.mapper")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.gpnu.api.client")
public class IntelliEduUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliEduUserApplication.class, args);
    }

}
