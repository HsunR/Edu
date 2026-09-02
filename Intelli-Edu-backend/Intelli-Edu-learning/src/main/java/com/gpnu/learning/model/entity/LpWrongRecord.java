package com.gpnu.learning.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@TableName("lp_wrong_record")
@Data
public class LpWrongRecord implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long wrongId;

    private Long studentId;

    private Long classId;

    private Long courseId;

    private Long questionId;

    private Long recordId;

    private Long examId;

    private Integer questionType;

    private BigDecimal fullScore;

    private BigDecimal earnedScore;

    private String wrongType;

    private Integer isResolved;

    private OffsetDateTime resolvedAt;

    private Integer wrongCount;

    private OffsetDateTime lastWrongAt;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
