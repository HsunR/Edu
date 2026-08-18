package com.gpnu.course.model.dto.section;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "课程节点创建请求对象")
public class SectionCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "课程节点标题不能为空")
    @Size(max = 50,message = "课程节点长度不能超过50")
    @Schema(description = "课程节点标题", example = "1-1 JAVA的发展历史")
    private String title;

    @Schema(description = "是否免费预览,0-否，1-是",example = "0")
    private Integer isFree;


}
