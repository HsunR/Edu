package com.gpnu.learning.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * WMCLR 推荐算法权重与行为配置。
 * <p>
 * 对应文档「公式 4」：Score = w1*S_weak + w2*S_wrong + w3*S_diff + w4*S_new - w5*S_rep
 */
@Data
@Component
@ConfigurationProperties(prefix = "recommend")
public class RecommendProperties {

    /** 默认返回条数上限 */
    private int defaultLimit = 10;

    /** 重复推荐 Redis 标记天数 */
    private int repeatPenaltyDays = 7;

    private double weightWeak = 0.35;
    private double weightWrong = 0.30;
    private double weightDiff = 0.15;
    private double weightNew = 0.15;
    private double weightRepeat = 0.05;

    /** 错题紧迫分：wrong_count 达到该值时 S_wrong 饱和为 1 */
    private int wrongUrgencyCap = 5;

    /** 已解决错题的 S_wrong 衰减系数 */
    private double resolvedWrongFactor = 0.2;
}
