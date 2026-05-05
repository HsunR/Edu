package com.gpnu.course.model.vo.section;

import com.gpnu.api.dto.resource.ResourceSimpleDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class SectionDetailVO extends SectionVO {

    private static final long serialVersionUID = 1L;

    private List<ResourceSimpleDTO> resourceDetails;

}
