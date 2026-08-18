package com.gpnu.knowledge.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@TableName("kn_question_knowledge")
@Data
public class QuestionKnowledge implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long pointId;

    private Long questionId;

    private Long courseId;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
