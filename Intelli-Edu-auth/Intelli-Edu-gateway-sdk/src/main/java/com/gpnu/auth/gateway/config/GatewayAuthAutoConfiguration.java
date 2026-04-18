package com.gpnu.auth.gateway.config;

import com.gpnu.auth.gateway.filter.AuthGlobalFilter;
import com.gpnu.auth.gateway.filter.RequestIdRelayFilter;
import com.gpnu.auth.gateway.handler.GatewayExceptionHandler;
import com.gpnu.auth.provider.JwtTokenProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * gateway-sdk 自动装配入口
 * <p>
 * 引入 gateway-sdk 依赖后自动注册：
 * <ul>
 *   <li>{@link AuthFilterProperties} — 网关鉴权配置</li>
 *   <li>{@link AuthGlobalFilter} — JWT 鉴权过滤器</li>
 *   <li>{@link RequestIdRelayFilter} — 请求ID生成过滤器</li>
 *   <li>{@link GatewayExceptionHandler} — 全局异常处理器</li>
 * </ul>
 * </p>
 */
@Configuration
@EnableConfigurationProperties(AuthFilterProperties.class)
public class GatewayAuthAutoConfiguration {

    @Bean
    public AuthGlobalFilter authGlobalFilter(JwtTokenProvider jwtTokenProvider,
                                             AuthFilterProperties properties) {
        return new AuthGlobalFilter(jwtTokenProvider, properties);
    }

    @Bean
    public RequestIdRelayFilter requestIdRelayFilter(AuthFilterProperties properties) {
        return new RequestIdRelayFilter(properties);
    }

    @Bean
    public GatewayExceptionHandler gatewayExceptionHandler() {
        return new GatewayExceptionHandler();
    }
}