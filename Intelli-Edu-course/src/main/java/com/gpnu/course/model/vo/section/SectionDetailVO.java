package com.gpnu.course.model.vo.section;

import com.gpnu.api.dto.resource.ResourceSimpleDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SectionDetailVO extends SectionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ResourceSimpleDTO> resourceDetails;

}
