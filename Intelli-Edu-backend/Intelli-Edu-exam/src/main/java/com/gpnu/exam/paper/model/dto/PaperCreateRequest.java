package com.gpnu.exam.paper.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "试卷创建请求")
public class PaperCreateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "试卷名称不能为空")
    @Size(max = 200)
    @Schema(description = "试卷名称")
    private String paperName;

    @NotNull(message = "课程ID不能为空")
    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "分节标题", example = "[{\"index\":1,\"title\":\"选择题\"}]")
    private List<Map<String, Object>> sections;
}
