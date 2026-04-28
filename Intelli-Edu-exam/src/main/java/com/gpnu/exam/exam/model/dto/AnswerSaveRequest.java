package com.gpnu.exam.exam.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;

@Data
@Schema(description = "保存答案请求")
public class AnswerSaveRequest implements Serializable {


    private static final long serialVersionUID = 1L;

    @Schema(description = "学生答案内容")
    private String answerContent;
}
