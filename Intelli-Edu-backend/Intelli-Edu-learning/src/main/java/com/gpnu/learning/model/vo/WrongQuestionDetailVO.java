package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "班级单题错误详情")
public class WrongQuestionDetailVO implements Serializable {

    private Long classId;
    private Long questionId;
    @Schema(description = "题型 0单选 1多选 2判断 3填空 4简答")
    private Integer questionType;
    @Schema(description = "难度 1-5")
    private Integer difficulty;
    @Schema(description = "题干摘要")
    private String stem;
    private List<WrongStudentBriefVO> students = new ArrayList<>();
    private List<WrongTypeDistVO> wrongTypeDistribution = new ArrayList<>();
}
