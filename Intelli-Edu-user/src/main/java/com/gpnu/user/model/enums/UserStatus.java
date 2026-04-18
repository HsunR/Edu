package com.gpnu.user.model.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserStatus {

    NORMAL(1,"正常"),
    BAN(0,"禁止");

    @EnumValue
    private final int code;


    @JsonValue
    private final String description;

    @JsonCreator
    public static UserStatus fromCode(Integer code) throws IllegalAccessException {
        if (code == null) {
            return null;
        }
        for (UserStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
       throw new IllegalAccessException("未知的用户状态: " + code);
    }


}
