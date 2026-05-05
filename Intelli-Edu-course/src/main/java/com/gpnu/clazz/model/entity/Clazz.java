package com.gpnu.clazz.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.OffsetDateTime;

import com.gpnu.clazz.model.enums.ClassStatus;
import lombok.Data;

/**
 * 班级表（课程开课实例）
 * @TableName co_class
 */
@TableName(value ="co_class")
@Data
public class Clazz implements Serializable {
    /**
     * 班级ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long classId;

    /**
     * 所属课程ID
     */
    private Long courseId;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 班级管理教师userId
     */
    private Long teacherId;

    /**
     * 邀请码（8位大写字母+数字）
     */
    private String inviteCode;

    /**
     * 最大学生数（NULL=不限制）
     */
    private Integer maxStudents;

    /**
     * 开课日期
     */
    private LocalDate startDate;

    /**
     * 结课日期
     */
    private LocalDate endDate;

    /**
     * 班级状态：0=招生中 1=进行中 2=已结束
     */
    private ClassStatus status;

    /**
     * 
     */
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