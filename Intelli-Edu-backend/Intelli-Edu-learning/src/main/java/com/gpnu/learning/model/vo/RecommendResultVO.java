package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "WMCLR 推荐结果")
public class RecommendResultVO implements Serializable {

    private String scene;
    private String summary;
    private List<RecommendPointVO> points = new ArrayList<>();
    private List<RecommendQuestionVO> questions = new ArrayList<>();
    private List<RecommendSectionVO> sections = new ArrayList<>();
}
