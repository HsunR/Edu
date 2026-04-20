package com.gpnu.course.model.dto.course;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "课程更新请求对象")
public class CourseUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50,message = "课程名称长度不能超过50")
    @Schema(description = "课程名称", example = "Java编程入门")
    private String courseName;


    @Schema(description = "课程描述",example = "本课程将带你从零开始学习Java编程，涵盖基础语法、面向对象编程、常用库和框架等内容，适合初学者入门。")
    private String description;

    @Size(max = 500)
    @Schema(description = "课程封面url",example = "https://example.com/course-cover.jpg")
    private String coverUrl;

    @Schema(description = "课程分类id",example = "123")
    private Long categoryId;

    @Schema(description = "课程是否已公开，0=私有 1=公开（未入班可浏览目录）",example = "1")
    private Integer isPublic;



}
