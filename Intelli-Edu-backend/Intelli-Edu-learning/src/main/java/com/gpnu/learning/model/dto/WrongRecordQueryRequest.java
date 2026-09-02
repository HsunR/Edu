package com.gpnu.learning.model.dto;

import com.gpnu.common.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "错题本分页查询")
public class WrongRecordQueryRequest extends PageRequest {

    @Schema(description = "班级ID")
    private Long classId;

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "题型")
    private Integer questionType;

    @Schema(description = "是否已解决：0未解决 1已解决")
    private Integer isResolved;
}
