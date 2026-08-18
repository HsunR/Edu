package com.gpnu.api.dto.exam;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExamSimpleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long examId;
    private String examName;
    private Long classId;
    private Long courseId;
    private Integer status;

}
