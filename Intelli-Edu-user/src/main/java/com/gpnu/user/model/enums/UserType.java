package com.gpnu.user.model.enums;

import lombok.Getter;

@Getter
public enum UserType {

    STUDENT(1, "学生"),
    TEACHER(2, "教师"),
    ADMIN(3, "管理员");

    /**
     * 用户类型码
     */
    private final int code;

    /**
     * 用户类型描述
     */
    private final String description;

    UserType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据类型码获取对应的用户类型枚举
     * @param code 用户类型码
     * @return 对应的UserType，如果不存在则返回null
     */
    public static UserType getByCode(int code) {
        for (UserType type : values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据描述获取对应的用户类型枚举
     * @param description 用户类型描述
     * @return 对应的UserType，如果不存在则返回null
     */
    public static UserType getByDescription(String description) {
        for (UserType type : values()) {
            if (type.getDescription().equalsIgnoreCase(description)) {
                return type;
            }
        }
        return null;

    }
}
