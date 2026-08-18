package com.gpnu.knowledge.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "知识点绑定题目请求")
public class QuestionBindRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "题目ID列表不能为空")
    @Schema(description = "题目ID列表", example = "[201, 202, 203]")
    private List<Long> questionIds;
}
