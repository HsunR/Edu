package com.gpnu.exam.paper.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "试卷题目排序请求")
public class PaperQuestionOrderRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    @Schema(description = "排序项列表")
    private List<OrderItem> items;

    @Data
    public static class OrderItem {
        @NotNull
        @Schema(description = "试卷-题目关联ID")
        private Long id;

        @NotNull
        @Schema(description = "新的排序值")
        private Integer orderIndex;

        @Schema(description = "新的节号")
        private Integer sectionIndex;
    }
}
