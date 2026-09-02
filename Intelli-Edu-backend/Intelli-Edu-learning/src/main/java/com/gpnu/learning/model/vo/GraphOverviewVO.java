package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "课程图谱概览（知识点树 + 掌握度）")
public class GraphOverviewVO implements Serializable {

    private Long courseId;
    private Long classId;
    private Integer weakThreshold;
    private List<GraphPointVO> points = new ArrayList<>();
}
