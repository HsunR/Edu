package com.gpnu.user.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.gpnu.user.model.enums.SexType;
import com.gpnu.user.model.enums.UserStatus;
import com.gpnu.user.model.enums.UserType;
import lombok.Data;

import java.io.Serializable;
import java.time.OffsetDateTime;

@TableName("us_user")
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long userId;

    private String name;

    private String password;

    private UserType userType;

    private String avatarUrl;

    private String email;

    private String mobile;

    private String openId;

    private SexType sex;

    private String school;

    private String personalSignature;

    private UserStatus status;

    @Version
    private Integer version;

    @TableLogic
    private Integer isDeleted;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}