package com.gpnu.knowledge.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "知识点绑定章节请求")
public class SectionBindRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotEmpty(message = "章节ID列表不能为空")
    @Schema(description = "章节ID列表", example = "[101, 102, 103]")
    private List<Long> sectionIds;
}
