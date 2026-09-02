package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Schema(description = "知识点掌握度概览")
public class MasteryOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "知识点ID")
    private Long pointId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "知识点名称")
    private String pointName;

    @Schema(description = "掌握度 0-100")
    private Integer masteryLevel;

    @Schema(description = "累计应得总分")
    private BigDecimal totalScore;

    @Schema(description = "累计实际得分")
    private BigDecimal earnedScore;

    @Schema(description = "累计作答题数")
    private Integer answerCount;

    @Schema(description = "累计答对题数")
    private Integer correctCount;

    @Schema(description = "最近练习时间")
    private OffsetDateTime lastPracticeAt;
}
