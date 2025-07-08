package com.gpnu.model.dto.userModel.ususer;

import com.baomidou.mybatisplus.annotation.TableId;
import com.gpnu.common.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

@Data
public class UsUserQueryRequest extends PageRequest  implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
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


}
