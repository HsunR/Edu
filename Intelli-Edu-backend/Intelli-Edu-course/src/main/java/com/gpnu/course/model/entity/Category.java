package com.gpnu.course.model.entity;


import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

import lombok.Data;

/**
 * 课程分类表
 * @TableName co_category
 */
@TableName(value ="co_category")
@Data
public class Category implements Serializable {
    /**
     * 分类ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long categoryId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父分类ID，NULL表示顶级分类
     */
    private Long parentId;

    /**
     * 同级排序序号
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