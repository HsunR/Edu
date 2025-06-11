package com.gpnu.ai.model.enums;

import lombok.Getter;



@Getter
public enum MessageTypeEnum {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");
    private final String value;
    MessageTypeEnum(String value) { this.value = value; }

    public static MessageTypeEnum fromString(String str) {
        for (MessageTypeEnum type : MessageTypeEnum.values()) {
            if (type.value.equalsIgnoreCase(str)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown message role: " + str);
    }
}