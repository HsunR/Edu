package com.gpnu.exam.exam.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "批阅请求")
public class GradeRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "得分不能为空")
    @Schema(description = "得分")
    private BigDecimal score;

    @Schema(description = "评语")
    private String comment;
}
