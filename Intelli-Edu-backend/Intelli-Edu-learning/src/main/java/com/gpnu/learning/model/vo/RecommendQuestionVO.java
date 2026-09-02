package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "推荐练习题")
public class RecommendQuestionVO implements Serializable {

    private Long questionId;
    private Integer questionType;
    private String stem;
    private Double score;
    private String reason;
    private List<Long> pointIds = new ArrayList<>();
}
