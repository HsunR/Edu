package com.gpnu.resource.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.api.dto.resource.ResourceSimpleDTO;
import com.gpnu.resource.model.dto.PresignRequest;
import com.gpnu.resource.model.dto.ResourceQueryRequest;
import com.gpnu.resource.model.dto.UploadConfirmRequest;
import com.gpnu.resource.model.dto.VideoConfirmRequest;
import com.gpnu.resource.model.entity.RsResource;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gpnu.resource.model.enums.ResourceType;
import com.gpnu.resource.model.vo.PresignedUrlVO;
import com.gpnu.resource.model.vo.ResourceDetailVO;
import com.gpnu.resource.model.vo.ResourceVO;
import com.gpnu.resource.model.vo.VodPresignedUrlVO;

import java.util.List;

/**
 * <p>
 * 资源表 服务类
 * </p>
 *
 * @author chenxingdong
 * @since 2026-04-11
 */
public interface IRsResourceService extends IService<RsResource> {

    public PresignedUrlVO generateCosPresignedUrl(Long userId, PresignRequest request, ResourceType resourceType);

    public VodPresignedUrlVO generateVodPresignedUrl(Long userId, PresignRequest request);

    public ResourceVO confirmCosUpload(UploadConfirmRequest request);

    public ResourceDetailVO confirmVodUpload(VideoConfirmRequest request);

    public ResourceDetailVO getResourceDetail(Long resourceId);

    public void deleteResource(Long userId, Long resourceId);

    public Page<ResourceVO> listResources(Long userId, ResourceQueryRequest request);

    public ResourceSimpleDTO getResourceSimple(Long resourceId);

    public List<ResourceSimpleDTO> getResourceSimpleBatch(List<Long> resourceIds);
}
