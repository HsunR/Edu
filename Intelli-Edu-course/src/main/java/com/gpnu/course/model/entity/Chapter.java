package com.gpnu.course.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

import lombok.Data;

/**
 * 课程章表
 * @TableName co_chapter
 */
@TableName(value ="co_chapter")
@Data
public class Chapter implements Serializable {
    /**
     * 章ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long chapterId;

    /**
     * 所属课程ID
     */
    private Long courseId;

    /**
     * 章标题
     */
    private String title;

    /**
     * 章排序序号
     */
    private Integer orderIndex;

    /**
     * 
     */
    @Version
    private Integer version;

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