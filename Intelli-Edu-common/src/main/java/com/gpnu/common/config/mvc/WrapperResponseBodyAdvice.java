package com.gpnu.common.config.mvc;

import com.gpnu.common.common.BaseResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WrapperResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 1. 如果已经是 BaseResponse，不包装
        if (BaseResponse.class.isAssignableFrom(returnType.getParameterType())) {
            return false;
        }

        // 2. 如果是 Flux ，不包装，交给 Controller 自己处理
        if (returnType.getParameterType() != null &&
                returnType.getParameterType().getName().equals("reactor.core.publisher.Flux")) {
            return false;
        }

        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            @NonNull MethodParameter returnType,
            @NonNull MediaType selectedContentType,
            @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
            @NonNull ServerHttpRequest request,
            @NonNull ServerHttpResponse response) {

        String path = request.getURI().getPath();

        if (path.contains("/v2/api-docs")
                || path.contains("/v3/api-docs")
                || path.contains("/swagger")
                || path.contains("/doc.html")) {
            return body;
        }

        // 关键：SSE 响应不包装（text/event-stream）
        if (MediaType.TEXT_EVENT_STREAM.includes(selectedContentType)) {
            return body;
        }

        // 返回 null 时，包装为空成功响应
        if (body == null) {
            return BaseResponse.success();
        }

        // 已经包装过，直接返回
        if (body instanceof BaseResponse) {
            return body;
        }


        if (StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            try {
                return com.fasterxml.jackson.databind.json.JsonMapper.builder()
                        .build()
                        .writeValueAsString(BaseResponse.success(body));
            } catch (Exception e) {
                throw new RuntimeException("String 类型响应包装失败", e);
            }
        }

        // 普通对象统一包装
        return BaseResponse.success(body);
    }
}
