package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "推荐知识点")
public class RecommendPointVO implements Serializable {

    private Long pointId;
    private String pointName;
    private Integer masteryLevel;
    private String reason;
}
