package com.gpnu.exam.question.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gpnu.exam.question.model.enums.Difficulty;
import com.gpnu.exam.question.model.enums.QuestionType;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@TableName("ex_question")
@Data
public class Question implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long questionId;

    private Long bankId;

    private QuestionType questionType;

    private String stem;

    private String analysis;

    private String answer;

    private BigDecimal score;

    private Difficulty difficulty;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
