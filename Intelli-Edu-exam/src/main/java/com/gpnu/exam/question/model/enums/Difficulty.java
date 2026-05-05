package com.gpnu.exam.question.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Difficulty {
    VERY_EASY(1, "非常简单"),
    EASY(2, "简单"),
    MEDIUM(3, "中等"),
    HARD(4, "困难"),
    VERY_HARD(5, "非常困难");

    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;

    Difficulty(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
