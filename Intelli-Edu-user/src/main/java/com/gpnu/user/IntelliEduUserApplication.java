package com.gpnu.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.gpnu.**.mapper")
@ComponentScan(basePackages = {"com.gpnu.user", "com.gpnu.common"})
@EnableDiscoveryClient
public class IntelliEduUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliEduUserApplication.class, args);
    }

}
