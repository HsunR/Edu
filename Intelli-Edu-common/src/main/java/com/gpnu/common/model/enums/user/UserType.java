package com.gpnu.common.model.enums.user;

import lombok.Getter;

@Getter
public enum UserType {

    STUDENT(1, "学生"),
    TEACHER(2, "教师"),
    ADMIN(3, "管理员");

    /**
     * 用户类型码
     */
    private final Integer code;

    /**
     * 用户类型描述
     */
    private final String type;

    UserType(int code, String type) {
        this.code = code;
        this.type = type;
    }

    /**
     * 根据类型码获取对应的用户类型枚举
     * @param code 用户类型码
     * @return 对应的UserType，如果不存在则返回null
     */
    public static UserType getByCode(Integer code) {
        for (UserType type : values()) {
            if (type.getCode() .equals(code) ) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据描述获取对应的用户类型枚举
     * @param type 用户类型描述
     * @return 对应的UserType，如果不存在则返回null
     */
    public static UserType getByDescription(String type) {
        for (UserType userType : values()) {
            if (userType.getType().equalsIgnoreCase(type)) {
                return userType;
            }
        }
        return null;

    }
}
