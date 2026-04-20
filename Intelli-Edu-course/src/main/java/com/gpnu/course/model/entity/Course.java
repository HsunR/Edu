package com.gpnu.course.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

import com.gpnu.course.model.enums.CourseStatus;
import lombok.Data;

/**
 * 课程表
 * @TableName co_course
 */
@TableName(value ="co_course")
@Data
public class Course implements Serializable {
    /**
     * 课程ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long courseId;

    /**
     * 课程名称
     */
    private String courseName;

    /**
     * 课程简介
     */
    private String description;

    /**
     * 课程封面图URL
     */
    private String coverUrl;

    /**
     * 授课教师userId
     */
    private Long teacherId;

    /**
     * 所属分类ID
     */
    private Long categoryId;

    /**
     * 课程状态：0=草稿 1=已发布 2=已归档
     */
    private CourseStatus status;

    /**
     * 是否公开：0=私有 1=公开（未入班可浏览目录）
     */
    private Integer isPublic;

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