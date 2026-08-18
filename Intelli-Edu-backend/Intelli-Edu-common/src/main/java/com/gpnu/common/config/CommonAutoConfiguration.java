package com.gpnu.common.config;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * common 模块自动装配入口
 * 依赖 common 的模块通过 Spring Boot AutoConfiguration 机制
 * 自动注册 common 中的所有 Bean，不再需要消费方手动 @ComponentScan。
 */
@Configuration
@AutoConfigureBefore(RedisAutoConfiguration.class)
@ComponentScan(basePackages = "com.gpnu.common")
public class CommonAutoConfiguration {
}