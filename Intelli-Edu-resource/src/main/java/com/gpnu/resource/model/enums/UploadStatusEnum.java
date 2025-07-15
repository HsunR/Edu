package com.gpnu.resource.model.enums;

import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import lombok.Getter;

/**
 * 资源上传状态枚举
 */
@Getter
public enum UploadStatusEnum {

    PENDING(1),
    SUCCESS(2),
    FAILED(3);

    private final Integer code;

    UploadStatusEnum(Integer code) {
        this.code = code;
    }

    public static UploadStatusEnum getEnumByCode(Integer code) {
        for (UploadStatusEnum status : UploadStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "上传状态不存在，code: " + code);
    }
}
