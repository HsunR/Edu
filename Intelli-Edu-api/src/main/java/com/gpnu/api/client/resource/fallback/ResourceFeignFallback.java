package com.gpnu.api.client.resource.fallback;


import com.gpnu.api.client.resource.ResourceFeignClient;
import com.gpnu.api.dto.resource.ResourceSimpleDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class ResourceFeignFallback implements FallbackFactory<ResourceFeignClient> {

    @Override
    public ResourceFeignClient create(Throwable cause) {
        log.error("查询资源服务出现异常", cause);
        return new ResourceFeignClient(){
            @Override
            public ResourceSimpleDTO getResourceSimple(Long resourceId) {
                return null;
            }

            @Override
            public List<ResourceSimpleDTO> getResourceSimpleBatch(List<Long> resourceIds) {
                return Collections.emptyList();
            }
        };
    };
}
