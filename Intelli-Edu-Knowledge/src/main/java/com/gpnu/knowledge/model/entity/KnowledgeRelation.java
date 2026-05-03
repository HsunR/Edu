package com.gpnu.knowledge.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@TableName("kn_knowledge_relation")
@Data
public class KnowledgeRelation implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long sourcePointId;

    private Long targetPointId;

    private String relationType;

    private Long courseId;

    private Integer weight;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
