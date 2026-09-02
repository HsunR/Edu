package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "推荐学习章节")
public class RecommendSectionVO implements Serializable {

    private Long sectionId;
    private String sectionTitle;
    private Long pointId;
    private String pointName;
    private String reason;
}
