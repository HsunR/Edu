package com.gpnu.auth.gateway.handler;

import com.gpnu.auth.gateway.util.GatewayResponseHelper;
import com.gpnu.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

/**
 * 网关全局异常处理器
 * <p>
 * 捕获网关层面的异常（路由失败、下游不可达、超时等），
 * 统一返回 {@link com.gpnu.common.common.BaseResponse} 格式的 JSON 响应，
 * 避免 Spring 默认的 HTML 错误页或框架格式的 JSON 导致前端解析失败。
 * </p>
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        String path = exchange.getRequest().getPath().value();

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus httpStatus;
        int errorCode;
        String message;

        if (ex instanceof ResponseStatusException rse) {
            // 路由未找到(404)、方法不允许(405) 等
            httpStatus = HttpStatus.valueOf(rse.getStatusCode().value());
            errorCode = rse.getStatusCode().value();
            message = rse.getReason() != null ? rse.getReason() : "请求的资源不存在";
            log.warn("Gateway ResponseStatusException: path={}, status={}, reason={}",
                    path, httpStatus.value(), message);

        } else if (ex instanceof ConnectException) {
            // 下游服务不可达
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            errorCode = ErrorCode.SYSTEM_ERROR.getCode();
            message = "服务暂时不可用，请稍后重试";
            log.error("Gateway ConnectException: path={}, downstream unreachable", path, ex);

        } else if (ex instanceof TimeoutException) {
            // 请求超时
            httpStatus = HttpStatus.GATEWAY_TIMEOUT;
            errorCode = ErrorCode.SYSTEM_ERROR.getCode();
            message = "请求超时，请稍后重试";
            log.error("Gateway TimeoutException: path={}", path, ex);

        } else {
            // 其他未知异常
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            errorCode = ErrorCode.SYSTEM_ERROR.getCode();
            message = "系统内部错误";
            log.error("Gateway unexpected exception: path={}, type={}", path, ex.getClass().getName(), ex);
        }

        return GatewayResponseHelper.writeErrorResponse(
                exchange.getResponse(), httpStatus, errorCode, message);
    }
}