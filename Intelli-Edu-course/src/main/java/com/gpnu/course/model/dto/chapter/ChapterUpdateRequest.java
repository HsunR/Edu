package com.gpnu.course.model.dto.chapter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "章节更新请求对象")
public class ChapterUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "章节标题", example = "第一章：课程介绍")
    @Size(max = 50,message = "章节标题长度不能超过50")
    private String title;
}
