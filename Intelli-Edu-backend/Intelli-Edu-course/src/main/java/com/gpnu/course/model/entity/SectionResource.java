package com.gpnu.course.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

import lombok.Data;

/**
 * 节-资源关联表
 * @TableName co_section_resource
 */
@TableName(value ="co_section_resource")
@Data
public class SectionResource implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 节ID
     */
    private Long sectionId;

    /**
     * 资源ID（Resource服务）
     */
    private Long resourceId;

    /**
     * 资源大类（冗余）：VIDEO / DOCUMENT / IMAGE
     */
    private String resourceType;

    /**
     * 节内资源排序序号
     */
    private Integer orderIndex;

    /**
     * 
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 
     */
    private OffsetDateTime createdAt;

    /**
     * 
     */
    private OffsetDateTime updatedAt;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}