package com.gpnu.course.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CourseStatus {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    ARCHIVED(2, "已归档");

    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;

    CourseStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
