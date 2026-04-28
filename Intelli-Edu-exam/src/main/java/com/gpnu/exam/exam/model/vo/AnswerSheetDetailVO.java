package com.gpnu.exam.exam.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Schema(description = "答卷详情VO（含所有答题记录）")
public class AnswerSheetDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "答卷ID")
    private Long sheetId;

    @Schema(description = "考试ID")
    private Long examId;

    @Schema(description = "考试名称")
    private String examName;

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "考试状态")
    private Integer status;

    @Schema(description = "总分")
    private BigDecimal totalScore;

    @Schema(description = "客观题得分")
    private BigDecimal objectiveScore;

    @Schema(description = "主观题得分")
    private BigDecimal subjectiveScore;

    @Schema(description = "提交次数")
    private Integer submitCount;

    @Schema(description = "开始答题时间")
    private OffsetDateTime startAnswerTime;

    @Schema(description = "最后一次提交时间")
    private OffsetDateTime submitTime;

    @Schema(description = "截止时间")
    private OffsetDateTime deadline;

    @Schema(description = "答题记录列表")
    private List<AnswerRecordVO> records;
}
