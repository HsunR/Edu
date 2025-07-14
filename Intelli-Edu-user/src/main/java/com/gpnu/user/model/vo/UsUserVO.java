package com.gpnu.user.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UsUserVO implements Serializable {

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
    private String type;

    /**
     * openID微信或者qq
     */
    private String openId;

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
