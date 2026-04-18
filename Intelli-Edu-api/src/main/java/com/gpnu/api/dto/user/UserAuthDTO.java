package com.gpnu.api.dto.user;


import lombok.Data;


import java.io.Serializable;

@Data
public class UserAuthDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String name;
    private String password;
    private Integer userType;
    private Integer status;         // 1=正常 0=禁用
    private String email;
    private String mobile;
    private String openId;
}

