package com.gpnu.gateway;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.stereotype.Component;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
@EnableDiscoveryClient
public class IntelliEduGatewayApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(IntelliEduGatewayApplication.class);
        app.addInitializers(ctx -> {
            ctx.addBeanFactoryPostProcessor(bf ->
                    bf.registerSingleton("authFilterPropertiesLogger", (ApplicationRunner) args2 -> {
                        var props = ctx.getBean(com.gpnu.auth.gateway.config.AuthFilterProperties.class);
                        System.out.println("AuthFilterProperties => " + props);
                    })
            );
        });
        app.run(args);
    }


}
