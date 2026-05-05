package com.gpnu.exam.exam.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum GradingStatus {
    PENDING(0, "待批阅"),
    GRADED(1, "已批阅"),
    AI_GRADING(2, "AI批阅中");

    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;

    GradingStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
