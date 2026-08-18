package com.gpnu.resource.controller;

import com.gpnu.api.dto.resource.ResourceSimpleDTO;
import com.gpnu.resource.service.IRsResourceService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inner/resources")
@Hidden
public class InnerResourceController {

    @Resource
    private IRsResourceService resourceService;

    @GetMapping("/{resourceId}")
    public ResourceSimpleDTO getResourceSimple(@PathVariable Long resourceId) {
        return resourceService.getResourceSimple(resourceId);
    }

    @PostMapping("/batch")
    public List<ResourceSimpleDTO> getResourceSimpleBatch(@RequestBody List<Long> resourceIds) {
        return resourceService.getResourceSimpleBatch(resourceIds);
    }
}