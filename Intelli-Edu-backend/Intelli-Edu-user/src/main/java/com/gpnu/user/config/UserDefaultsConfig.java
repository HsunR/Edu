package com.gpnu.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "user.defaults")
public class UserDefaultsConfig {
    private String avatarUrl;
    private String avatarUrlPrefix;
    private String personalSignature;
}