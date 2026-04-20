package com.gpnu.course.model.dto.section;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "小节更新请求对象")
public class SectionUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Size(max = 50)
    @Schema(description = "小节标题",example = "1-1 计算机网络发展")
    private String title;

    @Schema(description = "是否免费预览,0-否，1-是",example = "0")
    private Integer isFree;

}
