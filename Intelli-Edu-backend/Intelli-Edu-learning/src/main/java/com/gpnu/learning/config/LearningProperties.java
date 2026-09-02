package com.gpnu.learning.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "learning")
public class LearningProperties {

    /**
     * 薄弱知识点掌握度阈值，低于该值视为薄弱点
     */
    private int weakMasteryThreshold = 60;

    /**
     * 薄弱度置信度：知识点至少作答多少次才全额计入薄弱度（见 WeakPointDetector）
     */
    private int weakConfidenceMinAnswers = 3;
}
