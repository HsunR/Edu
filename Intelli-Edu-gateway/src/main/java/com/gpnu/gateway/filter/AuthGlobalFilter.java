package com.gpnu.gateway.filter;

import com.gpnu.common.common.ResultUtils; // 引入您的ResultUtils
import com.gpnu.common.exception.BusinessException; // 引入您的BusinessException
import com.gpnu.common.exception.ErrorCode;       // 引入您的ErrorCode
import com.gpnu.common.jwt.JwtTokenProvider;     // 引入Common模块中的JwtTokenProvider
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 全局认证过滤器，验证JWT
 */
//注解注释掉的话，则关闭网关过滤器
@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    private static final String AUTH_HEADER = HttpHeaders.AUTHORIZATION;
    private static final String BEARER_PREFIX = "Bearer ";

    // 用于向下游服务传递用户信息的HTTP Header名称
    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_TYPE = "X-User-Type";

    private final AntPathMatcher pathMatcher = new AntPathMatcher();


    // 无需认证的路径白名单
    // 这里可以使用 AntPathMatcher 进行更灵活的路径匹配
    private static final List<String> IGNORE_PATHS = List.of(
            "/api/user/auth/login",
            "/api/user/auth/register",
            "/api/user/auth/register/send-code",
            "/api/user/auth/login/send-code",
            "/api/user/auth/refresh-token",
            // Swagger UI 相关路径（通常需要根据实际Swagger配置调整）
            "/v2/api-docs", // Springfox old version
            "/v3/api-docs/**", // Springdoc-openapi
            "/**/v3/api-docs/**",
            "**/doc.html",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/webjars/**",
            "/favicon.ico"
            // TODO: 根据实际情况添加其他不需要认证的公共路径，例如一些公开的课程信息查询
    );



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String path = request.getPath().value();
        log.info("Gateway filter incoming request path: {}", path);

        // 1. 白名单路径直接放行
        if (isPathInIgnoreList(path)) {
            log.info("Path {} in ignore list, skipping authentication.", path);
            return chain.filter(exchange);
        }

        // 2. 获取并验证JWT
        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return handleUnauthorized(response, ErrorCode.NOT_LOGIN_ERROR.getCode(), "缺少身份认证信息或格式错误");
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            // 直接调用 common 模块中的 JwtTokenProvider 进行验证
            if (!jwtTokenProvider.validateAccessToken(token)) {
                // validateAccessToken 内部已经处理了过期和黑名单的日志
                // 这里只需判断结果并抛出业务异常
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无效或已过期的身份认证信息");
            }

            // 解析Claims以获取用户信息
            Claims claims = jwtTokenProvider.getAllClaimsFromToken(token); // 假设此方法是public的

            String userId = jwtTokenProvider.getUserIdFromAccessToken(token);
            String userType = jwtTokenProvider.getUserTypeFromAccessToken(token);

            // 3. 将用户信息传递到下游服务
            // 确保不会覆盖现有同名Header，这里是添加或替换
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header(HEADER_USER_ID, userId)
                    .header(HEADER_USER_TYPE, userType)
                    .build();

            log.info("Request authenticated. User ID: {}, User Type: {}. Forwarding to downstream service.", userId, userType);
            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (BusinessException e) {
            log.error("JWT validation failed for path {}: {}", path, e.getMessage(), e);
            return handleUnauthorized(response, e.getCode(), e.getMessage()); // 使用业务异常信息
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation or parsing for path {}: {}", path, e.getMessage(), e);
            return handleUnauthorized(response, ErrorCode.SYSTEM_ERROR.getCode(), "身份认证失败，系统内部错误");
        }
    }

    /**
     * 判断请求路径是否在白名单中
     * 实际生产环境建议使用 AntPathMatcher，例如：
     * private AntPathMatcher antPathMatcher = new AntPathMatcher();
     * antPathMatcher.match(ignorePath, path)
     */

    private boolean isPathInIgnoreList(String path) {
        for (String ignorePattern : IGNORE_PATHS) {
            boolean matched = pathMatcher.match(ignorePattern, path);
            log.info("Matching pattern: {}, path: {}, matched: {}", ignorePattern, path, matched);
            if (matched) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理未授权响应
     */
    private Mono<Void> handleUnauthorized(ServerHttpResponse response, int errorCode, String errorMessage) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED); // HTTP 状态码 401
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");

        // 使用ResultUtils构建响应体
        // 注意：BaseResponse的toString方法默认可能不是JSON格式，
        // 生产环境建议使用 ObjectMapper 将 BaseResponse 对象转换为 JSON 字符串
        String responseJson = ResultUtils.error(errorCode, errorMessage).toString();

        DataBuffer buffer = response.bufferFactory().wrap(responseJson.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 设置过滤器执行顺序，越小越先执行
     * 认证过滤器通常会放在较早的位置，以在路由之前完成认证
     */
    @Override
    public int getOrder() {
        return -100; // 较早的执行顺序
    }
}