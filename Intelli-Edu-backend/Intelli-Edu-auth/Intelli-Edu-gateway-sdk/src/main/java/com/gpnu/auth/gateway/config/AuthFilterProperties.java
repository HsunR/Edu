package com.gpnu.auth.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关鉴权配置属性
 * <p>
 * 对应 application.yml 中 {@code auth.gateway.*} 前缀的配置项。
 * </p>
 *
 * <pre>
 * auth:
 *   gateway:
 *     enabled: true
 *     ignore-paths:
 *       - /api/user/auth/login
 *       - /api/course/public/**
 *     skip-origin-paths:
 *       - /api/notify/callback
 * </pre>
 */
@Data
@ConfigurationProperties(prefix = "auth.gateway")
public class AuthFilterProperties {

    /**
     * 是否启用网关鉴权过滤器，默认开启。
     * 设为 false 时 AuthGlobalFilter 直接放行所有请求，
     * 替代原来注释掉 @Component 的做法。
     */
    private boolean enabled = true;

    /**
     * 无需认证的路径白名单，支持 Ant 风格通配符。
     * <p>例如：{@code /api/user/auth/login}、{@code /api/course/public/**}</p>
     */
    private List<String> ignorePaths = new ArrayList<>();

    /**
     * 不添加网关来源标识（X-Request-From）的路径。
     * <p>适用于第三方回调等不应携带网关标识的接口。</p>
     */
    private List<String> skipOriginPaths = new ArrayList<>();
}