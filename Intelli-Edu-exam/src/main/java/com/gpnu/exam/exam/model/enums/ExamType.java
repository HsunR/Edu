package com.gpnu.exam.exam.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ExamType {
    EXAM(0, "考试"),
    PRACTICE(1, "练习"),
    HOMEWORK(2, "作业");

    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;

    ExamType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
