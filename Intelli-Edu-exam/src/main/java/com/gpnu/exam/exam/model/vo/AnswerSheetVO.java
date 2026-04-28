package com.gpnu.exam.exam.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Schema(description = "答卷VO")
public class AnswerSheetVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "答卷ID")
    private Long sheetId;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "答卷状态，0=未开始 1=进行中 2=已结束 3-已批阅完成")
    private Integer status;

    @Schema(description = "总分")
    private BigDecimal totalScore;

    @Schema(description = "客观题分数")
    private BigDecimal objectiveScore;

    @Schema(description = "主观题分数")
    private BigDecimal subjectiveScore;

    @Schema(description = "提交次数")
    private Integer submitCount;

    @Schema(description = "开始答题时间")
    private OffsetDateTime startAnswerTime;

    @Schema(description = "最后一次提交时间")
    private OffsetDateTime submitTime;

    @Schema(description = "答题截止时间")
    private OffsetDateTime deadline;
}
