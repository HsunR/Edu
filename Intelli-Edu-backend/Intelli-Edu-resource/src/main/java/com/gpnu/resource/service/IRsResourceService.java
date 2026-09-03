package com.gpnu.resource.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.api.dto.resource.ResourceSimpleDTO;
import com.gpnu.resource.model.dto.PresignRequest;
import com.gpnu.resource.model.dto.ResourceQueryRequest;
import com.gpnu.resource.model.dto.UploadConfirmRequest;
import com.gpnu.resource.model.entity.RsResource;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.resource.model.enums.ResourceType;
import com.gpnu.resource.model.vo.PresignedUrlVO;
import com.gpnu.resource.model.vo.ResourceDetailVO;
import com.gpnu.resource.model.vo.ResourceVO;

import java.util.List;

/**
 * <p>
 * 资源表 服务类
 * </p>
 *
 * @author chenxingdong
 */
public interface IRsResourceService extends IService<RsResource> {

    PresignedUrlVO generatePresignedUrl(Long userId, PresignRequest request, ResourceType resourceType);

    ResourceVO confirmUpload(Long userId, UploadConfirmRequest request);

    ResourceDetailVO confirmVideoUpload(Long userId, UploadConfirmRequest request);

    public ResourceDetailVO getResourceDetail(Long resourceId);

    public void deleteResource(Long userId, Long resourceId);

    public Page<ResourceVO> listResources(Long userId, ResourceQueryRequest request);

    public ResourceSimpleDTO getResourceSimple(Long resourceId);

    public List<ResourceSimpleDTO> getResourceSimpleBatch(List<Long> resourceIds);

    public void cleanExpiredUploadResources();
}
