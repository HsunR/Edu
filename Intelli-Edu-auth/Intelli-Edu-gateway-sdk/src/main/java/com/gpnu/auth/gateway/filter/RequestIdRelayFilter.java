package com.gpnu.auth.gateway.filter;

import com.gpnu.auth.gateway.config.AuthFilterProperties;
import com.gpnu.auth.gateway.support.MdcContextLifter;
import com.gpnu.common.constants.Constant;
import cn.hutool.core.lang.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
@RequiredArgsConstructor
public class RequestIdRelayFilter implements GlobalFilter, Ordered {

    private final AuthFilterProperties properties;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 生成 RequestId
        String requestId = UUID.randomUUID().toString(true);

        // 2. 获取请求路径
        String path = exchange.getRequest().getPath().value();

        // 3. 构建新的请求头
        exchange = exchange.mutate().request(builder -> {
            builder.header(Constant.REQUEST_ID_HEADER, requestId);
            if (!isSkipOriginPath(path)) {
                builder.header(Constant.REQUEST_FROM_HEADER, Constant.GATEWAY_ORIGIN_NAME);
            }
        }).build();

        log.debug("RequestIdRelayFilter: path={}, requestId={}", path, requestId);

        // 4. 将 requestId 写入 Reactor Context，触发 MdcContextLifter 同步到 MDC
        return chain.filter(exchange)
                .contextWrite(ctx -> ctx.put(
                        MdcContextLifter.MDC_CONTEXT_PREFIX + Constant.REQUEST_ID_HEADER,
                        requestId));
    }

    private boolean isSkipOriginPath(String path) {
        for (String pattern : properties.getSkipOriginPaths()) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}