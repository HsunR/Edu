package com.gpnu.exam.question.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum QuestionType {
    SINGLE_CHOICE(0, "单选"),
    MULTI_CHOICE(1, "多选"),
    TRUE_FALSE(2, "判断"),
    FILL_BLANK(3, "填空"),
    SHORT_ANSWER(4, "简答");

    @EnumValue
    private final int code;
    private final String desc;

    QuestionType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 是否为客观题（可自动判分）
     */
    public boolean isObjective() {
        return this == SINGLE_CHOICE || this == MULTI_CHOICE || this == TRUE_FALSE;
    }
}
