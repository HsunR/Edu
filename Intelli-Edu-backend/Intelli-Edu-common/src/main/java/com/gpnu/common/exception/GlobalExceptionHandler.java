package com.gpnu.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.gpnu.common.common.BaseResponse;
import com.gpnu.common.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessException(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?>  businessException(RuntimeException e) {
        log.error("RuntimeException", e);
        //todo 生产环境中的话 e.getMessage() 可能会暴露敏感信息，到时候改为" 系统内部异常，请稍后重试"
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }

    /**
     * 处理参数校验异常（如 @NotNull, @NotBlank 等）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException", ex);
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        StringBuilder errorMsg = new StringBuilder("参数校验失败: ");
        for (Map.Entry<String, String> entry : errors.entrySet()) {
            errorMsg.append(String.format("[%s: %s]", entry.getKey(), entry.getValue()));
        }

        return ResultUtils.error(ErrorCode.PARAMS_ERROR, errorMsg.toString());
    }

    /**
     * 处理 JSON 解析异常（包括枚举值无效、格式错误等）
     * 对应异常：HttpMessageNotReadableException
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public BaseResponse<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("JSON 解析异常: {}", e.getMessage());

        // 1. 尝试提取具体的枚举错误信息
        Throwable cause = e.getCause();
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) cause;
            Object badValue = ife.getValue();
            String targetTypeName = ife.getTargetType().getSimpleName();

            // 获取字段路径，例如 "registerType"
            String fieldName = ife.getPath().stream()
                    .map(ref -> ref.getFieldName())
                    .findFirst()
                    .orElse("未知字段");

            // 2. 构建友好的错误提示
            String msg = String.format("字段 [%s] 的值 [%s] 无效，请检查是否为正确的 %s 类型", fieldName, badValue, targetTypeName);
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, msg);
        }

        // 3. 其他 JSON 解析错误（如缺少引号、格式不对）
        return ResultUtils.error(ErrorCode.PARAMS_ERROR, "请求参数格式错误，请检查 JSON 结构");
    }

}
