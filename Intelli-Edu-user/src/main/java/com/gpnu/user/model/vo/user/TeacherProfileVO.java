package com.gpnu.user.model.vo.user;

import lombok.Data;

@Data
public class TeacherProfileVO {
    private String teacherNo;  //教师工号
    private String title;
    private String department;
    private String bio;  //教师简介
}