package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "班级某知识点掌握度聚合")
public class ClassMasteryPointVO implements Serializable {

    private Long pointId;
    private String pointName;
    private BigDecimal avgMasteryLevel;
    private Integer studentCount;
    private Integer weakStudentCount;
}
