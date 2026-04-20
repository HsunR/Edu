package com.gpnu.clazz.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

import com.gpnu.clazz.model.enums.MemberStatus;
import lombok.Data;

/**
 * 班级成员表
 * @TableName co_class_member
 */
@TableName(value ="co_class_member")
@Data
public class ClassMember implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 班级ID
     */
    private Long classId;

    /**
     * 学生userId
     */
    private Long studentId;

    /**
     * 成员状态：0=正常 1=已退出
     */
    private MemberStatus status;

    /**
     * 加入时间
     */
    private OffsetDateTime joinedAt;

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