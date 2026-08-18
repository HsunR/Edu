package com.gpnu.user.model.dto.ususer;

import com.gpnu.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户姓名
     */
    private String name;



    /**
     * 用户身份
     */
    private String userType;



    /**
     * 用户性别（默认1男，0女）
     */
    private Integer sex;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户手机号码
     */
    private String mobile;


    /**
     * 用户学校
     */
    private String school;

    /**
     * 用户状态
     */
    private Integer status;
}