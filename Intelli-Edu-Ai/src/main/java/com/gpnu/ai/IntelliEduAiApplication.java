package com.gpnu.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

@SpringBootApplication
@MapperScan("com.gpnu.ai.mapper")
public class IntelliEduAiApplication {

    public static void main(String[] args) {
        //调试Bean对象的创建
//        SpringApplication app = new SpringApplication(IntelliEduAiApplication.class);
//        app.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {
//            System.setProperty("spring.main.allow-bean-definition-overriding", "true");
//        });
//        app.run(args);
        SpringApplication.run(IntelliEduAiApplication.class, args);
    }

}
