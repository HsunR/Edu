package com.gpnu.course.model.dto.chapter;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "章节排序项")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "章节id不能为空")
    @Schema(description = "章节id", required = true)
    private Long id ;

    @NotNull(message = "排序索引不能为空")
    @Min(0)
    @Schema(description = "排序索引，0开始", example = "0", required = true)
    private Integer orderIndex;

}
