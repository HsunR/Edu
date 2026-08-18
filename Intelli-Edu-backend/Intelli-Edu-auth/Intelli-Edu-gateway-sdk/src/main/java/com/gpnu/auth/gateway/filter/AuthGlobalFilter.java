package com.gpnu.auth.gateway.filter;

import com.gpnu.auth.common.constants.AuthConstants;
import com.gpnu.auth.gateway.config.AuthFilterProperties;
import com.gpnu.auth.gateway.util.GatewayResponseHelper;
import com.gpnu.auth.provider.JwtTokenProvider;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthFilterProperties properties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 配置关闭时直接放行
        if (!properties.isEnabled()) {
            return chain.filter(exchange);
        }

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getPath().value();

        log.info("Gateway filter incoming request path: {}", path);

        /*
         * 先清理所有外部可能伪造的内部身份 Header。
         */
        ServerHttpRequest sanitizedRequest = request.mutate()
                .headers(headers -> {
                    headers.remove(AuthConstants.USER_ID_HEADER);
                    headers.remove(AuthConstants.USER_TYPE_HEADER);
                    headers.remove(AuthConstants.REQUEST_FROM_HEADER);
                    headers.remove(AuthConstants.INTERNAL_TIMESTAMP_HEADER);
                    headers.remove(AuthConstants.INTERNAL_SIGN_HEADER);
                })
                .build();

        exchange = exchange.mutate().request(sanitizedRequest).build();
        request = sanitizedRequest;

        // 1. 白名单路径直接放行
        if (isPathIgnored(path)) {
            log.info("Path {} in ignore list, skipping authentication.", path);
            return chain.filter(exchange);
        }

        // 2. 获取并验证 JWT
        String authHeader = request.getHeaders().getFirst(AuthConstants.AUTHORIZATION_HEADER);

        if (authHeader == null || !authHeader.startsWith(AuthConstants.BEARER_PREFIX)) {
            log.warn("Missing or invalid Authorization header for path: {}", path);
            return GatewayResponseHelper.writeErrorResponse(
                    response,
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.NOT_LOGIN_ERROR.getCode(),
                    "缺少身份认证信息或格式错误"
            );
        }

        String token = authHeader.substring(AuthConstants.BEARER_PREFIX_LENGTH);

        try {
            if (!jwtTokenProvider.validateAccessToken(token)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无效或已过期的身份认证信息");
            }

            // 3. 解析用户信息
            String userId = jwtTokenProvider.getUserIdFromAccessToken(token);
            Integer userType = jwtTokenProvider.getUserTypeFromAccessToken(token);

            if (userId == null || userId.isBlank() || userType == null) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "身份认证信息不完整");
            }

            /*
             * 4. 写入可信内部 Header，转发给下游服务。
             *
             * X-Request-From 只表示来源，不应单独作为安全依据。
             * 后续加入内部签名后，下游应同时校验 X-Internal-Sign。
             */
            ServerHttpRequest modifiedRequest = request.mutate()
                    .headers(headers -> {
                        headers.set(AuthConstants.USER_ID_HEADER, userId);
                        headers.set(AuthConstants.USER_TYPE_HEADER, userType.toString());
                        headers.set(AuthConstants.REQUEST_FROM_HEADER, AuthConstants.REQUEST_FROM_GATEWAY);
                    })
                    .build();

            log.info(
                    "Request authenticated. User ID: {}, User Type: {}. Forwarding to downstream service.",
                    userId,
                    userType
            );

            return chain.filter(exchange.mutate().request(modifiedRequest).build());

        } catch (BusinessException e) {
            log.error("JWT validation failed for path {}: {}", path, e.getMessage());
            return GatewayResponseHelper.writeErrorResponse(
                    response,
                    HttpStatus.UNAUTHORIZED,
                    e.getCode(),
                    e.getMessage()
            );
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation for path {}: {}", path, e.getMessage(), e);
            return GatewayResponseHelper.writeErrorResponse(
                    response,
                    HttpStatus.UNAUTHORIZED,
                    ErrorCode.SYSTEM_ERROR.getCode(),
                    "身份认证失败，系统内部错误"
            );
        }
    }

    /**
     * 判断请求路径是否命中白名单。
     */
    private boolean isPathIgnored(String path) {
        if (properties.getIgnorePaths() == null || properties.getIgnorePaths().isEmpty()) {
            return false;
        }

        for (String pattern : properties.getIgnorePaths()) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return -100;
    }
}