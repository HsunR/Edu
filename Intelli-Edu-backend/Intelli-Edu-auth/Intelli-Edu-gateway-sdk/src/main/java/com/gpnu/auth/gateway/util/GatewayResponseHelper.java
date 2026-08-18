package com.gpnu.auth.gateway.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpnu.common.common.BaseResponse;
import com.gpnu.common.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * 网关响应构建工具
 * <p>
 * 统一构建符合 {@link BaseResponse} 格式的 JSON 响应，
 * 供 {@link com.gpnu.auth.gateway.filter.AuthGlobalFilter} 和
 * {@link com.gpnu.auth.gateway.handler.GatewayExceptionHandler} 复用。
 * </p>
 */
@Slf4j
public final class GatewayResponseHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private GatewayResponseHelper() {
    }

    /**
     * 构建错误响应并写出
     *
     * @param response   ServerHttpResponse
     * @param httpStatus HTTP 状态码
     * @param errorCode  业务错误码
     * @param message    错误信息
     * @return Mono<Void>
     */
    public static Mono<Void> writeErrorResponse(ServerHttpResponse response,
                                                 HttpStatus httpStatus,
                                                 int errorCode,
                                                 String message) {
        response.setStatusCode(httpStatus);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");

        BaseResponse<?> baseResponse = ResultUtils.error(errorCode, message);
        String json = toJson(baseResponse);

        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    /**
     * 将对象序列化为 JSON 字符串
     * <p>序列化失败时降级为手动拼接，确保始终返回合法 JSON。</p>
     */
    private static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("JSON serialization failed, fallback to manual build", e);
            return "{\"code\":500,\"message\":\"系统内部错误\",\"data\":null}";
        }
    }
}