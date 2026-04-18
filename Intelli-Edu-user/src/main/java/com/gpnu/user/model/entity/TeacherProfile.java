package com.gpnu.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@TableName("us_teacher_profile")
@Data
public class TeacherProfile implements Serializable {

    @TableId(type = IdType.INPUT)
    private Long userId;

    private String teacherNo;
    private String title;
    private String department;
    private String bio;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}