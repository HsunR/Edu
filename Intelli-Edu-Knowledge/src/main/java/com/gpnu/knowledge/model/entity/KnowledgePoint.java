package com.gpnu.knowledge.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@TableName("kn_knowledge_point")
@Data
public class KnowledgePoint implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    private Long pointId;

    private String pointName;

    private Long courseId;

    private Long parentId;

    private String description;

    private Integer orderIndex;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
