package com.gpnu.user.model.enums;

import lombok.Getter;

@Getter
public enum CaptchaType {

    KAPTCHA("kaptcha"),
    SMS("sms"),
    EMAIL("email");;

    private final String type;

    CaptchaType(String type) {
        this.type = type;
    }


    public static CaptchaType getCaptchaType(String type) {
        for (CaptchaType captchaType : CaptchaType.values()) {
            if (captchaType.getType().equalsIgnoreCase(type)) {
                return captchaType;
            }
        }
        return null;
    }
}
