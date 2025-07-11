package com.gpnu.resource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.gpnu.**.mapper")
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.gpnu.resource", "com.gpnu.common"})
public class IntelliEduResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliEduResourceApplication.class, args);
    }

}
