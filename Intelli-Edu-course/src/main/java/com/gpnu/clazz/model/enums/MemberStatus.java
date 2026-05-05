package com.gpnu.clazz.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MemberStatus {
    ACTIVE(0, "正常"),
    WITHDRAWN(1, "已退出");

    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;

    MemberStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
