package com.gpnu.resource.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gpnu.api.dto.resource.ResourceSimpleDTO;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.exception.ThrowUtils;
import com.gpnu.resource.constants.ResourceConstants;
import com.gpnu.resource.manager.MinioManager;
import com.gpnu.resource.mapper.RsResourceMapper;
import com.gpnu.resource.mapper.RsVideoMetaMapper;
import com.gpnu.resource.model.dto.PresignRequest;
import com.gpnu.resource.model.dto.ResourceQueryRequest;
import com.gpnu.resource.model.dto.UploadConfirmRequest;
import com.gpnu.resource.model.entity.RsResource;
import com.gpnu.resource.model.entity.RsVideoMeta;
import com.gpnu.resource.model.enums.ResourceType;
import com.gpnu.resource.model.enums.UploadStatus;
import com.gpnu.resource.model.vo.*;
import com.gpnu.resource.service.IRsResourceService;
import io.minio.StatObjectResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ResourceServiceImpl extends ServiceImpl<RsResourceMapper, RsResource>
        implements IRsResourceService {

    @Resource
    private MinioManager minioManager;

    @Resource
    private RsVideoMetaMapper videoMetaMapper;

    // ==================== 预签名 ====================

    @Override
    @Transactional
    public PresignedUrlVO generatePresignedUrl(Long userId, PresignRequest request, ResourceType resourceType) {
        // 1. 校验文件格式
        String fileName = request.getFileName();
        String fileFormat = FileUtil.getSuffix(fileName).toLowerCase();
        validateFileFormat(fileFormat, resourceType);

        long maxSize = ResourceConstants.getMaxSize(resourceType.getCode());
        if (request.getFileSize() == null || request.getFileSize() <= 0 || request.getFileSize() > maxSize) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不合法");
        }

        // 2. 生成 MinIO object key
        String storageKey = buildStorageKey(resourceType, fileFormat);

        // 3. 生成浏览器可用的预签名 URL
        String presignedUrl = minioManager.generatePresignedUploadUrl(storageKey);

        // 4. 预创建资源记录（upload_status=待确认）
        RsResource resource = new RsResource();
        resource.setResourceName(fileName);
        resource.setResourceType(resourceType);
        resource.setFileFormat(fileFormat);
        resource.setFileSize(request.getFileSize());
        resource.setStorageKey(storageKey);
        resource.setUploaderId(userId);
        resource.setUploadStatus(UploadStatus.PENDING);
        baseMapper.insert(resource);

        if (ResourceType.VIDEO.equals(resourceType)) {
            RsVideoMeta videoMeta = new RsVideoMeta();
            videoMeta.setResourceId(resource.getResourceId());
            videoMetaMapper.insert(videoMeta);
        }

        // 5. 构建返回
        String accessUrl = minioManager.getFileAccessUrl(storageKey);

        PresignedUrlVO vo = new PresignedUrlVO();
        vo.setResourceId(resource.getResourceId());
        vo.setUploadUrl(presignedUrl);
        vo.setStorageKey(storageKey);
        vo.setAccessUrl(accessUrl);
        vo.setExpiresIn(minioManager.getPresignExpirySeconds());

        log.info("Generated MinIO presigned URL for user={}, resource={}, key={}",
                userId, resource.getResourceId(), storageKey);
        return vo;
    }

    // ==================== 确认上传 ====================

    @Override
    public ResourceVO confirmUpload(Long userId, UploadConfirmRequest request) {
        RsResource resource = confirmUploadInternal(userId, request.getResourceId(), false);
        return buildResourceVO(resource);
    }

    @Override
    public ResourceDetailVO confirmVideoUpload(Long userId, UploadConfirmRequest request) {
        RsResource resource = confirmUploadInternal(userId, request.getResourceId(), true);
        RsVideoMeta videoMeta = videoMetaMapper.selectById(resource.getResourceId());
        return buildResourceDetailVO(resource, videoMeta);
    }

    private RsResource confirmUploadInternal(Long userId, Long resourceId, boolean videoExpected) {
        // 1. 查询资源记录
        RsResource resource = getResourceOrThrow(resourceId);
        if (!UploadStatus.PENDING.equals(resource.getUploadStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该资源不处于待确认状态");
        }

        if (!resource.getUploaderId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权确认他人的资源");
        }

        if (videoExpected != ResourceType.VIDEO.equals(resource.getResourceType())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "资源类型与确认接口不匹配");
        }

        // 2. 从 MinIO 查实际文件大小并校验
        StatObjectResponse metadata = minioManager.getObjectMetadata(resource.getStorageKey());

        long actualSize = metadata.size();
        long maxSize = ResourceConstants.getMaxSize(resource.getResourceType().getCode());
        if (actualSize <= 0 || actualSize > maxSize || !Long.valueOf(actualSize).equals(resource.getFileSize())) {
            minioManager.deleteObject(resource.getStorageKey());
            resource.setUploadStatus(UploadStatus.FAILED);
            baseMapper.updateById(resource);
            throw new BusinessException(ErrorCode.PARAMS_ERROR,
                    "上传文件大小与申请信息不一致或超过限制");
        }

        // 3. 更新资源记录（带乐观锁，防止并发重复确认）
        resource.setFileSize(actualSize);
        resource.setAccessUrl(minioManager.getFileAccessUrl(resource.getStorageKey()));
        resource.setUploadStatus(UploadStatus.SUCCESS);
        int affected = baseMapper.updateById(resource);
        if (affected == 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "资源状态已变更，请勿重复确认");
        }

        log.info("MinIO upload confirmed, resource={}, size={}", resource.getResourceId(), actualSize);
        return resource;
    }

    // ==================== 查询 ====================

    @Override
    public ResourceDetailVO getResourceDetail(Long resourceId) {
        RsResource resource = getResourceOrThrow(resourceId);

        RsVideoMeta videoMeta = null;
        if (ResourceType.VIDEO.getCode() == resource.getResourceType().getCode()) {
            videoMeta = videoMetaMapper.selectById(resourceId);
        }

        return buildResourceDetailVO(resource, videoMeta);
    }

    @Override
    public Page<ResourceVO> listResources(Long userId, ResourceQueryRequest request) {
        LambdaQueryWrapper<RsResource> wrapper = new LambdaQueryWrapper<>();

        // 只查自己的资源
        wrapper.eq(RsResource::getUploaderId, userId);

        // 条件筛选
        wrapper.like(request.getResourceName() != null,
                RsResource::getResourceName, request.getResourceName());
        wrapper.eq(request.getResourceType() != null,
                RsResource::getResourceType, request.getResourceType());
        wrapper.eq(request.getFileFormat() != null,
                RsResource::getFileFormat, request.getFileFormat());
        wrapper.eq(request.getUploadStatus() != null,
                RsResource::getUploadStatus, request.getUploadStatus());
        wrapper.ge(request.getCreatedFrom() != null,
                RsResource::getCreatedAt, request.getCreatedFrom());
        wrapper.le(request.getCreatedTo() != null,
                RsResource::getCreatedAt, request.getCreatedTo());

        // 按创建时间倒序
        wrapper.orderByDesc(RsResource::getCreatedAt);

        Page<RsResource> page = this.page(
                new Page<>(request.getCurrent(), request.getPageSize()), wrapper);

        // 转换 VO
        Page<ResourceVO> voPage = new Page<>(request.getCurrent(), request.getPageSize(), page.getTotal());
        List<ResourceVO> voList = page.getRecords().stream()
                .map(this::buildResourceVO)
                .toList();
        voPage.setRecords(voList);

        return voPage;
    }

    // ==================== 删除 ====================

    @Override
    @Transactional
    public void deleteResource(Long userId, Long resourceId) {
        RsResource resource = getResourceOrThrow(resourceId);

        // 校验是否是自己的资源
        if (!resource.getUploaderId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权删除他人的资源");
        }

        // 1. 删除对象存储文件
        if (UploadStatus.SUCCESS.equals(resource.getUploadStatus())
                && resource.getStorageKey() != null) {
            try {
                minioManager.deleteObject(resource.getStorageKey());
            } catch (Exception e) {
                log.warn("Failed to delete MinIO object, resource={}, key={}, will proceed with db deletion",
                        resourceId, resource.getStorageKey(), e);

            }
        }

        // 2. 逻辑删除资源记录
        this.removeById(resourceId);

        // 3. 如果是视频，删除视频元数据
        if (ResourceType.VIDEO.getCode() == resource.getResourceType().getCode()) {
            videoMetaMapper.deleteById(resourceId);
        }

        log.info("Resource deleted, userId={}, resourceId={}", userId, resourceId);
    }

    @Override
    public ResourceSimpleDTO getResourceSimple(Long resourceId) {
        RsResource resource = getById(resourceId);

        ThrowUtils.throwIf(resource == null, ErrorCode.NOT_FOUND_ERROR, "资源不存在");
        ResourceSimpleDTO resourceSimpleDTO = new ResourceSimpleDTO();
        BeanUtils.copyProperties(resource, resourceSimpleDTO);
        return resourceSimpleDTO;
    }

    @Override
    public List<ResourceSimpleDTO> getResourceSimpleBatch(List<Long> resourceIds) {

        List<RsResource> resources = baseMapper.selectByIds(resourceIds);
        if(resources.isEmpty()){
            return List.of();
        }

        //
        List<ResourceSimpleDTO> resourceSimpleDTOS = new ArrayList<>(resources.size());
        for (RsResource resource : resources) {
            ResourceSimpleDTO resourceSimpleDTO = new ResourceSimpleDTO();
            BeanUtils.copyProperties(resource, resourceSimpleDTO);
            resourceSimpleDTOS.add(resourceSimpleDTO);
        }
        return resourceSimpleDTOS;

    }


    @Transactional
    @Override
    public void cleanExpiredUploadResources() {
        OffsetDateTime expireTime = OffsetDateTime.now().minusHours(1);
        int batchSize = 100;

        int totalSuccess = 0;
        int totalFailed = 0;
        int batchNo = 0;

        while (true) {
            batchNo++;

            List<RsResource> expiredResources = this.list(new LambdaQueryWrapper<RsResource>()
                    .in(RsResource::getUploadStatus, UploadStatus.FAILED, UploadStatus.PENDING)
                    .lt(RsResource::getCreatedAt, expireTime)
                    .last("LIMIT " + batchSize)
            );

            if (expiredResources.isEmpty()) {
                break;
            }

            int batchSuccess = 0;
            int batchFailed = 0;

            for (RsResource resource : expiredResources) {
                try {
                    cleanSingleExpiredUploadResource(resource);
                    batchSuccess++;
                } catch (Exception e) {
                    batchFailed++;
                    log.error(
                            "Failed to clean expired upload resource, resourceId={}, resourceType={}, storageKey={}",
                            resource.getResourceId(),
                            resource.getResourceType(),
                            resource.getStorageKey(),
                            e
                    );
                }
            }

            totalSuccess += batchSuccess;
            totalFailed += batchFailed;

            log.info(
                    "Clean expired upload resources batch finished, batchNo={}, batchSize={}, success={}, failed={}",
                    batchNo,
                    expiredResources.size(),
                    batchSuccess,
                    batchFailed
            );

            if (expiredResources.size() < batchSize) {
                break;
            }
        }

        log.info(
                "Clean expired upload resources finished, totalSuccess={}, totalFailed={}",
                totalSuccess,
                totalFailed
        );
    }




    // ==================== 私有方法 ====================

    /**
     * 查询资源，不存在则抛异常
     */
    private RsResource getResourceOrThrow(Long resourceId) {
        RsResource resource = baseMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "资源不存在");
        }
        return resource;
    }

    /**
     * 校验文件格式是否在白名单中
     */
    private void validateFileFormat(String fileFormat, ResourceType expectedType) {
        if (fileFormat == null || fileFormat.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名缺少后缀");
        }

        ResourceType actualType = ResourceType.fromFileFormat(fileFormat);
        if (actualType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的文件格式：" + fileFormat);
        }

        if (actualType != expectedType) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,
                    "文件格式与上传类型不匹配，期望" + expectedType.getDesc() + "，实际为" + actualType.getDesc());
        }
    }

    /**
     * 生成 MinIO 对象键
     * 格式：{类型目录}/{日期}_{16位随机串}.{后缀}
     * 示例：images/20260417_a3f8b2c1d9e4f7g6.png
     */
    private String buildStorageKey(ResourceType resourceType, String fileFormat) {
        String directory = switch (resourceType) {
            case IMAGE -> "images";
            case DOCUMENT -> "documents";
            case VIDEO -> "videos";
        };
        String uuid = RandomUtil.randomString(16);
        String datePart = DateUtil.format(new Date(), "yyyyMMdd");
        return String.format("%s/%s_%s.%s", directory, datePart, uuid, fileFormat);
    }

    /**
     * 构建 ResourceVO
     */
    private ResourceVO buildResourceVO(RsResource resource) {
        ResourceVO vo = new ResourceVO();
        BeanUtils.copyProperties(resource, vo);
        return vo;
    }

    /**
     * 构建 ResourceDetailVO（含视频扩展信息）
     */
    private ResourceDetailVO buildResourceDetailVO(RsResource resource, RsVideoMeta videoMeta) {
        ResourceDetailVO vo = new ResourceDetailVO();
        BeanUtils.copyProperties(resource, vo);

        if (videoMeta != null) {
            VideoMetaVO metaVO = new VideoMetaVO();
            BeanUtils.copyProperties(videoMeta, metaVO);
            vo.setVideoMeta(metaVO);
        }

        return vo;
    }


    private void cleanSingleExpiredUploadResource(RsResource resource) {
        Long resourceId = resource.getResourceId();

        cleanExpiredMinioResource(resource);

        if (ResourceType.VIDEO.equals(resource.getResourceType())) {
            videoMetaMapper.deleteById(resourceId);
        }


        boolean removed = this.removeById(resourceId);
        if (!removed) {
            log.warn("Expired resource db record remove returned false, resourceId={}", resourceId);
        }

        log.info("Expired upload resource cleaned, resourceId={}, type={}",
                resourceId, resource.getResourceType());
    }

    private void cleanExpiredMinioResource(RsResource resource) {
        String storageKey = resource.getStorageKey();

        if (storageKey == null || storageKey.isBlank()) {
            log.info("Expired MinIO resource has empty storageKey, skip object delete, resourceId={}",
                    resource.getResourceId());
            return;
        }

        try {
            minioManager.deleteObject(storageKey);
            log.info("Deleted expired MinIO object, resourceId={}, storageKey={}",
                    resource.getResourceId(), storageKey);
        } catch (Exception e) {

            log.warn("Failed to delete expired MinIO object, resourceId={}, storageKey={}",
                    resource.getResourceId(), storageKey, e);
        }
    }

}
