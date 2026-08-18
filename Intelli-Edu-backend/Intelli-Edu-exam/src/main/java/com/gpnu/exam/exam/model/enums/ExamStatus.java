package com.gpnu.exam.exam.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ExamStatus {
    NOT_STARTED(0, "未开始"),
    IN_PROGRESS(1, "进行中"),
    ENDED(2, "已结束"),
    GRADED(3, "已批阅完成");

    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;

    ExamStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
