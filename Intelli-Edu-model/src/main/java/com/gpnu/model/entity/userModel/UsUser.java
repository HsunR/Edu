package com.gpnu.model.entity.userModel;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户详情表
 * @TableName us_user
 */
@TableName(value ="us_user")
@Data
public class UsUser implements Serializable {
    /**
     * 用户id
     */
    @TableId
    private String userId;

    /**
     * 用户姓名
     */
    private String name;

    /**
     * 用户密码
     */
    private String password;

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


    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}