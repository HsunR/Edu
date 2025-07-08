package com.gpnu.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Component;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.gpnu.gateway", "com.gpnu.common"})
public class IntelliEduGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntelliEduGatewayApplication.class, args);
    }

}
