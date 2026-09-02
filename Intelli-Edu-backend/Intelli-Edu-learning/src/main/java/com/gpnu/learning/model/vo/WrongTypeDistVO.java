package com.gpnu.learning.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "错因类型分布")
public class WrongTypeDistVO implements Serializable {

    private String wrongType;
    private Integer count;
}
