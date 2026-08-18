package com.gpnu.course.model.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "课程创建请求对象")
public class CourseCreateRequest implements Serializable {


    private static final long serialVersionUID = 1L;

    @NotBlank(message = "课程名称不能为空")
    @Size(max = 50,message = "课程名称长度不能超过50")
    @Schema(description = "课程名称",example = "Java编程入门")
    String courseName;

    @Schema(description = "课程简介",example = "本课程适合零基础学员，系统讲解Java编程基础知识")
    String description;

    @Size(max=500)
    @Schema(description = "课程封面URL",example = "https://example.com/course-cover.jpg")
    String coverUrl;

    @Schema(description = "课程分类ID",example = "12345")
    Long   categoryId;

    @Schema(description = "课程是否已公开，0=私有 1=公开（未入班可浏览目录）",example = "1")
    Integer isPublic;

}
