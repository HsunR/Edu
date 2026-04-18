package com.gpnu.resource.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 资源上传状态枚举
 */
@Getter
@AllArgsConstructor
public enum UploadStatus {

    PENDING(1,"待确认"),  //待确认
    SUCCESS(2,"成功"),  //成功
    FAILED(3,"失败");  //失败

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String desc;



    @JsonCreator
    public static UploadStatus getEnumByCode(Integer code) throws IllegalAccessException {
        if(code==null){
            throw new IllegalAccessException("上传状态code不能为空");
        }
        for (UploadStatus status : UploadStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalAccessException("未知的资源类型: "+ code);
    }
}
