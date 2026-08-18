package com.gpnu.exam.paper.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "试卷更新请求")
public class PaperUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(max = 200)
    @Schema(description = "试卷名称")
    private String paperName;

    @Schema(description = "分节标题")
    private List<Map<String, Object>> sections;
}
