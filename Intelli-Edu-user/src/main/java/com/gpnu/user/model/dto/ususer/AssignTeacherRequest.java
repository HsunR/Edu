package com.gpnu.user.model.dto.ususer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "注册教师请求对象")
public class AssignTeacherRequest implements Serializable {

    @NotBlank(message = "工号不能为空")
    private String teacherNo;

    private String title;               // 职称（选填）
    private String department;          // 院系（选填）
    private String bio;                 // 简介（选填）
}