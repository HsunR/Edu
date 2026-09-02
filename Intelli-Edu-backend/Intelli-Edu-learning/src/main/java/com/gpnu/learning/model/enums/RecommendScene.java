package com.gpnu.learning.model.enums;

import lombok.Getter;

/**
 * 推荐业务场景，与功能文档 F5 中 scene 一致。
 */
@Getter
public enum RecommendScene {

    /** 薄弱知识点复习：图谱/掌握度页「去复习」 */
    REVIEW_WEAK("REVIEW_WEAK"),

    /** 错题同类练习：错题集「同类练习」 */
    REVIEW_WRONG("REVIEW_WRONG"),

    /** 今日复习计划：课程首页综合推荐 */
    DAILY_PLAN("DAILY_PLAN"),

    /** 教师教学干预：班级薄弱点 + 高频错题 */
    TEACHER_INTERVENTION("TEACHER_INTERVENTION");

    private final String code;

    RecommendScene(String code) {
        this.code = code;
    }

    public static RecommendScene fromCode(String code) {
        if (code == null || code.isBlank()) {
            return REVIEW_WEAK;
        }
        for (RecommendScene scene : values()) {
            if (scene.code.equalsIgnoreCase(code)) {
                return scene;
            }
        }
        throw new IllegalArgumentException("未知推荐场景: " + code);
    }
}
