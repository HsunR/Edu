package com.gpnu.user.model.dto.ususer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "注册教师请求对象")
public class AssignTeacherRequest implements Serializable {

    @NotBlank
    @Schema(description = "将要分配教师的用户ID",example = "123456789")
    private Long userId;

    @NotBlank(message = "工号不能为空")
    @Schema(description = "教师工号", example = "T12345")
    private String teacherNo;

    @Schema(description = "教师职称",example = "教授")
    private String title;

    @Schema(description = "教师所属院系",example = "计算机科学与技术学院")
    private String department;

    @Schema(description = "教师个人简介",example = "具有10年教学经验，专注于人工智能领域的研究和教学。")
    private String bio;
}