package com.gpnu.user.model.dto.ususer;

import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;


import java.io.Serializable;

/**
 * 用户注册请求DTO
 */
@Data
@GroupSequence({RegisterRequest.class, SendRegisterCodeRequest.MobileGroup.class, SendRegisterCodeRequest.EmailGroup.class}) // 定义校验顺序，结合SendRegisterCodeRequest的Group
public class RegisterRequest implements Serializable {
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 20, message = "用户姓名长度必须在2到20之间")
    private String name;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20之间")
    private String password;

    @NotNull(message = "注册类型不能为空")
    private Integer registerType;

    // 根据registerType，mobile或email至少有一个需要被校验


    private String mobile;

    private String email;

    @NotBlank(message = "验证码不能为空")
    private String code;




}