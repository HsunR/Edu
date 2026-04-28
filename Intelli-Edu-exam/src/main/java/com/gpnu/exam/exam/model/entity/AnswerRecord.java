package com.gpnu.exam.exam.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gpnu.exam.exam.model.enums.GradingStatus;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@TableName("ex_answer_record")
@Data
public class AnswerRecord implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long recordId;

    private Long sheetId;

    private Long questionId;

    private String answerContent;

    private BigDecimal score;

    private Boolean isCorrect;

    private GradingStatus gradingStatus;

    private Long graderId;

    private String comment;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
