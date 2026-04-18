package com.gpnu.user.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum SexType {
    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女");

    /**
     * 性别代码（对应数据库存储值）
     */
    @EnumValue

    private final Integer code;

    /**
     * 性别描述
     */
    @JsonValue
    private final String desc;

    /**
     * 根据代码获取枚举对象
     * @param code 数据库中的代码 (0, 1, 2)
     * @return 对应的枚举对象，如果未找到则返回 UNKNOWN
     */
    public static SexType getByCode(Integer code) {
        if (code == null) {
            return UNKNOWN;
        }
        return Arrays.stream(SexType.values())
                .filter(item -> item.getCode().equals(code))
                .findFirst()
                .orElse(UNKNOWN);
    }

    @JsonCreator
    public static SexType fromCode(Integer code) {
        return getByCode(code);
    }
}
