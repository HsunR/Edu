package com.gpnu.api.client.resource;

import com.gpnu.api.client.resource.fallback.ResourceFeignFallback;
import com.gpnu.api.dto.resource.ResourceSimpleDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "Intelli-Edu-resource", path = "/api/resource/inner/resources",
        fallbackFactory = ResourceFeignFallback.class)
public interface ResourceFeignClient {
    /**
     * 根据资源ID获取资源的简单信息
     *
     * @param resourceId 资源ID
     * @return 资源的简单信息DTO
     */
    @GetMapping("/{resourceId}")
    ResourceSimpleDTO getResourceSimple(@PathVariable("resourceId") Long resourceId);

    /**
     * 批量获取资源的简单信息
     *
     * @param resourceIds 资源ID列表
     * @return 资源的简单信息DTO列表
     */
    @PostMapping("/batch")
    List<ResourceSimpleDTO> getResourceSimpleBatch(@RequestBody List<Long> resourceIds);

}
