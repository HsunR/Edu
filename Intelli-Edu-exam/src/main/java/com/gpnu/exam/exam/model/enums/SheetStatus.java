package com.gpnu.exam.exam.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum SheetStatus {
    ANSWERING(0, "答题中"),
    SUBMITTED(1, "已提交"),
    GRADED(2, "已批阅");

    @EnumValue
    private final int code;
    private final String desc;

    SheetStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
