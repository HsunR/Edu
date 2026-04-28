package com.gpnu.exam.question.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.OffsetDateTime;

@TableName("ex_question_bank")
@Data
public class QuestionBank implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long bankId;

    private String bankName;

    private Long courseId;

    private Long teacherId;

    private String description;

    private Integer questionCount;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
