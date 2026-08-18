package com.gpnu.exam.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gpnu.exam.exam.model.enums.SheetStatus;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@TableName("ex_answer_sheet")
@Data
public class AnswerSheet implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long sheetId;

    private Long examId;

    private Long studentId;

    private SheetStatus status;

    private BigDecimal totalScore;

    private BigDecimal objectiveScore;

    private BigDecimal subjectiveScore;

    private Integer submitCount;

    private OffsetDateTime startAnswerTime;

    private OffsetDateTime submitTime;

    /**
     * 个人截止时间，null表示不自动交卷（allow_late_submit=true）
     */
    private OffsetDateTime deadline;

    @Version
    private Integer version;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
