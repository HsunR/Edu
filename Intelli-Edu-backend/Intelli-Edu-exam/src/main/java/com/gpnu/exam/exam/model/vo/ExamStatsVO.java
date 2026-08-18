package com.gpnu.exam.exam.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "考试统计VO")
public class ExamStatsVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "应考人数")
    private Integer totalStudents;
    @Schema(description = "已交卷人数")
    private Integer submittedCount;
    @Schema(description = "答题中人数")
    private Integer answeringCount;
    @Schema(description = "已批阅人数")
    private Integer gradedCount;
    @Schema(description = "最高分")
    private BigDecimal maxScore;
    @Schema(description = "最低分")
    private BigDecimal minScore;
    @Schema(description = "平均分")
    private BigDecimal avgScore;
}
