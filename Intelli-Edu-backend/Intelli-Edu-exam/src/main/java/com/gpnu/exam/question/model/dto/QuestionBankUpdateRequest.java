package com.gpnu.exam.question.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "题库更新请求")
public class QuestionBankUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = 100, message = "题库名称不能超过100字符")
    @Schema(description = "题库名称")
    private String bankName;

    @Size(max = 500)
    @Schema(description = "题库描述")
    private String description;
}
