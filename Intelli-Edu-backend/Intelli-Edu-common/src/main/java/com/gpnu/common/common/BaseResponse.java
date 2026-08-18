package com.gpnu.common.common;

import com.gpnu.common.constants.Constant;
import com.gpnu.common.exception.ErrorCode;
import lombok.Data;
import org.slf4j.MDC;

import java.io.Serial;
import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 业务状态码，0-成功，其他-失败
     */
    private int code;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 请求唯一标识，便于日志排查
     */
    private String requestId;

    public BaseResponse() {
        this.requestId = getCurrentRequestId();
    }

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.requestId = getCurrentRequestId();
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

    /**
     * 成功响应（无数据）
     */
    public static BaseResponse<Void> success() {
        return new BaseResponse<>(0, null, "ok");
    }

    /**
     * 成功响应（有数据）
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败响应（基于错误码枚举）
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败响应（自定义错误信息）
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }

    /**
     * 失败响应（自定义code和message）
     */
    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.code == 0;
    }

    /**
     * 链式设置 requestId
     */
    public BaseResponse<T> requestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    private String getCurrentRequestId() {
        return MDC.get(Constant.REQUEST_ID_MDC_KEY);
    }
}