package com.gpnu.exam.question.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "题库创建请求")
public class QuestionBankCreateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "题库名称不能为空")
    @Size(max = 100, message = "题库名称不能超过100字符")
    @Schema(description = "题库名称", example = "Java基础单选题库")
    private String bankName;

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "所属课程ID")
    private Long courseId;

    @Size(max = 500)
    @Schema(description = "题库描述")
    private String description;
}
