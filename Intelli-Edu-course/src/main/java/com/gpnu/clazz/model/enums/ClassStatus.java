package com.gpnu.clazz.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ClassStatus {
    RECRUITING(0, "招生中"),
    ACTIVE(1, "进行中"),
    ENDED(2, "已结束");

    @EnumValue
    private final int code;
    private final String desc;

    ClassStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
