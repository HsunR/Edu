package com.gpnu.course.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

import lombok.Data;

/**
 * 课程节表
 * @TableName co_section
 */
@TableName(value ="co_section")
@Data
public class Section implements Serializable {
    /**
     * 节ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long sectionId;

    /**
     * 所属章ID
     */
    private Long chapterId;

    /**
     * 所属课程ID（冗余，便于按课程直接查所有节）
     */
    private Long courseId;

    /**
     * 节标题
     */
    private String title;

    /**
     * 节排序序号
     */
    private Integer orderIndex;

    /**
     * 是否免费预览：0=否 1=是（公开课程中未入班也可访问）
     */
    private Integer isFree;

    /**
     * 
     */
    @Version
    private Integer version;

    /**
     * 
     */
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