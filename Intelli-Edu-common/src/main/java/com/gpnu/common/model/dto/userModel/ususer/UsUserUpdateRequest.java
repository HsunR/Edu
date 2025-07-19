package com.gpnu.common.model.dto.userModel.ususer;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UsUserUpdateRequest implements Serializable {

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
     * 用户密码
     */
    private String password;

    /**
     * 用户性别（默认1男，0女）
     */
    private Integer sex;


    /**
     * 用户头像路径地址
     */
    private String headPortrait;

    /**
     * 用户个性签名
     */
    private String personalSignature;


    /**
     * 用户学校
     */
    private String school;


}
