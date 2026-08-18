package com.gpnu.auth.common.jwt;

import com.gpnu.auth.config.AuthCommonAutoConfiguration;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * JWT 配置属性
 * <p>
 * 对应 application.yml 中 {@code jwt.*} 前缀的配置项。
 * 由 {@link AuthCommonAutoConfiguration} 通过 @EnableConfigurationProperties 自动注册，
 * 因此不再需要 @Component 注解。
 * </p>
 *
 * <pre>
 * # application.yml 示例
 * jwt:
 *   secret: your-256-bit-secret-key-at-least-32-bytes
 *   access-token-expiration-ms: 3600000
 *   refresh-token-expiration-days: 7
 * </pre>
 */
@Data
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥（HS256），长度需 >= 32 字节
     */
    @NotBlank
    private String secret;

    /**
     * Access Token 有效期（毫秒），默认 1 小时
     */
    @Min(1)
    private long accessTokenExpirationMs = 3600000;

    /**
     * Refresh Token 有效期（天），默认 7 天
     */
    @Min(1)
    private long refreshTokenExpirationDays = 7;
}