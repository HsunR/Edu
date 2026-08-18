package com.gpnu.course.model.dto.section;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "章节资源添加请求对象")
public class SectionResourceAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;


    @NotNull(message = "资源id不能为空")
    @Schema(description = "资源id，视频资源是视频id，文档资源是文档id，图片资源是图片id",example = "123456")
    private Long resourceId;

    @NotBlank(message = "资源类型不能为空")
    @Schema(description = "资源类型，VIDEO：视频，DOCUMENT：文档，IMAGE：图片",example = "VIDEO")
    private String resourceType;

}
