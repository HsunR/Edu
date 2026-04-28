package com.gpnu.exam.question.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;

@TableName("ex_question_option")
@Data
public class QuestionOption implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long optionId;

    private Long questionId;

    private String label;

    private String content;

    private Boolean isCorrect;

    private Integer orderIndex;

    @TableLogic
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
