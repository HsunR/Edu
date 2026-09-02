package com.gpnu.learning.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@TableName("lp_mastery")
@Data
public class LpMastery implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long masteryId;

    private Long studentId;

    private Long classId;

    private Long courseId;

    private Long pointId;

    private Integer masteryLevel;

    private BigDecimal totalScore;

    private BigDecimal earnedScore;

    private Integer answerCount;

    private Integer correctCount;

    private OffsetDateTime lastPracticeAt;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
