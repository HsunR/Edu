package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "学生错题统计")
public class WrongStatsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "按题型统计")
    private List<WrongTypeStatVO> byQuestionType = new ArrayList<>();

    @Schema(description = "按知识点统计")
    private List<WrongPointStatVO> byKnowledgePoint = new ArrayList<>();
}
