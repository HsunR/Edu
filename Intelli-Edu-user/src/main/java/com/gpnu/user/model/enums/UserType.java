package com.gpnu.user.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.gpnu.auth.common.constants.AuthConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum UserType {

    STUDENT(AuthConstants.ROLE_STUDENT, "Student"),
    TEACHER(AuthConstants.ROLE_TEACHER, "Teacher"),
    ADMIN(AuthConstants.ROLE_ADMIN,     "Admin");

    @EnumValue
    private final int code;

    @JsonValue
    private final String desc;

    public static UserType getByCode(Integer code) {
        if (code == null) return null;
        for (UserType e : values()) {
            if (e.code == code) return e;
        }
        return null;
    }


    @JsonCreator
    public static UserType formCode(Integer code) throws IllegalAccessException {
        for(UserType userType :  values()){
            if(userType.code == code){
                return userType;
            }
        }
        throw new IllegalAccessException("无法识别的用户类型:"+code);
    }
}