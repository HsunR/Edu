package com.gpnu.common.model.dto.userModel.ususer;

import lombok.Data;

import java.io.Serializable;

@Data
public class UsUserDeleteRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;


}
