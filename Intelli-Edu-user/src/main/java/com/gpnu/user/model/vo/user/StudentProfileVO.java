package com.gpnu.user.model.vo.user;

import lombok.Data;

@Data
public class StudentProfileVO {
    private String studentNo;
    private String grade;
    private String major;
    private Integer enrollmentYear;
}