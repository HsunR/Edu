package com.gpnu.exam.paper.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum PaperStatus {
    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布");

    @EnumValue
    private final int code;
    private final String desc;

    PaperStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
