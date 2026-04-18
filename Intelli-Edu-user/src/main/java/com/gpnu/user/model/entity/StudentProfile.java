package com.gpnu.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@TableName("us_student_profile")
@Data
public class StudentProfile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private Long userId;

    private String studentNo;
    private String grade;
    private String major;
    private Integer enrollmentYear;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}