package com.gpnu.auth.resource.config;

import com.gpnu.auth.resource.interceptor.FeignRelayUserInterceptor;
import com.gpnu.auth.resource.interceptor.UserInfoRelayInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class ResourceAuthAutoConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInfoRelayInterceptor())
                .addPathPatterns("/**");
    }

    @Bean
    @ConditionalOnClass(name = "feign.RequestInterceptor")
    public FeignRelayUserInterceptor feignRelayUserInterceptor() {
        return new FeignRelayUserInterceptor();
    }
}