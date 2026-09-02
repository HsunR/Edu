package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "按知识点错题统计")
public class WrongPointStatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "知识点ID")
    private Long pointId;

    @Schema(description = "知识点名称")
    private String pointName;

    @Schema(description = "错题数量（不同题目数）")
    private Long wrongQuestionCount;

    @Schema(description = "累计答错次数")
    private Long totalWrongTimes;
}
