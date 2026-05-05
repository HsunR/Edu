package com.gpnu.exam.exam.model.vo;

import com.gpnu.exam.exam.model.enums.GradingStatus;
import com.gpnu.exam.question.model.enums.QuestionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "答题记录VO")
public class AnswerRecordVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "答题记录ID")
    private Long recordId;

    @Schema(description = "问题ID")
    private Long questionId;

    @Schema(description = "学生的答案内容")
    private String answerContent;

    @Schema(description = "得分")
    private BigDecimal score;

    @Schema(description = "是否正确，客观题会自动判定")
    private Boolean isCorrect;

    @Schema(description = "批改状态，0=未批改 1=已批改 2-AI批改中")
    private GradingStatus gradingStatus;

    @Schema(description = "批改教师ID，null表示未批改或AI批改中")
    private Long graderId;

    @Schema(description = "评语")
    private String comment;

    /** 题目快照信息（来自试卷） */
    @Schema(description = "题目类型，0=单选 1=多选 2=判断 3=填空 4=简答")
    private QuestionType questionType;

    @Schema(description = "题目内容")
    private String stem;

    @Schema(description = "题目分值")
    private BigDecimal questionScore;

    @Schema(description = "正确答案，客观题有值")
    private String correctAnswer;
}
