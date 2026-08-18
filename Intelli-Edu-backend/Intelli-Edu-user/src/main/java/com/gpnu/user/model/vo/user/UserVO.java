package com.gpnu.user.model.vo.user;

import com.gpnu.user.model.enums.SexType;
import com.gpnu.user.model.enums.UserType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "用户信息VO对象")
public class UserVO  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @Schema(description = "用户id", example = "12345")
    private Long userId;

    /**
     * 用户姓名
     */
    @Schema(description = "用户姓名", example = "张三")
    private String name;

    /**
     * 用户身份
     */
    @Schema(description = "用户身份，1学生，2教师", example = "1")
    private UserType userType;


    /**
     * 用户性别（默认1男，0女）
     */
    @Schema(description = "用户性别，1男，0女", example = "1")
    private SexType sex;


    /**
     * 用户头像路径地址
     */
    @Schema(description = "用户头像URL地址", example = "http://example.com/avatar.jpg")
    private String avatarUrl;

    /**
     * 用户个性签名
     */
    @Schema(description = "用户个性签名", example = "热爱编程，喜欢分享")
    private String personalSignature;

    /**
     * 用户学校
     */
    @Schema(description = "用户学校", example = "广东技术师范大学")
    private String school;


}