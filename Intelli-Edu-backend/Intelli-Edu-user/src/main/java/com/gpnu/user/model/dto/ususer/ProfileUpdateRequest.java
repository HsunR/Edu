package com.gpnu.user.model.dto.ususer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户档案更新请求")
public class ProfileUpdateRequest implements Serializable {

    // --- 学生档案字段 ---
    @Schema(description = "学号", example = "20230001")
    private String studentNo;

    @Schema(description = "年级", example = "2023")
    private String grade;

    @Schema(description = "专业", example = "计算机科学与技术")
    private String major;

    @Schema(description = "入学年份", example = "2023")
    private Integer enrollmentYear;

    // --- 教师档案字段 ---
    @Schema(description = "教师工号", example = "T2023001")
    private String teacherNo;

    @Schema(description = "职称", example = "副教授")
    private String title;

    @Schema(description = "所属院系", example = "计算机学院")
    private String department;

    @Schema(description = "个人简介", example = "我是一名计算机科学与技术专业的教师，主要研究方向是人工智能和大数据。")
    private String bio;

}