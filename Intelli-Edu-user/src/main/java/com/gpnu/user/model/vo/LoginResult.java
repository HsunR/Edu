package com.gpnu.user.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录结果VO (也可以用作DTO，看具体场景)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String userType;
    private String accessToken;
    private String refreshToken;
}