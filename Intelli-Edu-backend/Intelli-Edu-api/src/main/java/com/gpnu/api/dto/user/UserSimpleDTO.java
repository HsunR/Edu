package com.gpnu.api.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserSimpleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String name;
    private String avatarUrl;
    private Integer userType;       // 1=学生 2=教师 3=管理员
}