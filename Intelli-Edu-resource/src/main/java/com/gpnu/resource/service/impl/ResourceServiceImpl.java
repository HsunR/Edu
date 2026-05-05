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
import com.gpnu.common.service.RedisService;
import com.gpnu.resource.constants.ResourceConstants;
import com.gpnu.resource.manager.CosManager;
import com.gpnu.resource.manager.TencentCloudVodManager;
import com.gpnu.resource.mapper.RsResourceMapper;
import com.gpnu.resource.mapper.RsVideoMetaMapper;
import com.gpnu.resource.model.dto.PresignRequest;
import com.gpnu.resource.model.dto.ResourceQueryRequest;
import com.gpnu.resource.model.dto.UploadConfirmRequest;
import com.gpnu.resource.model.dto.VideoConfirmRequest;
import com.gpnu.resource.model.entity.RsResource;
import com.gpnu.resource.model.entity.RsVideoMeta;
import com.gpnu.resource.model.enums.ResourceType;
import com.gpnu.resource.model.enums.UploadStatus;
import com.gpnu.resource.model.vo.*;
import com.gpnu.resource.service.IRsResourceService;
import com.qcloud.cos.model.ObjectMetadata;
import com.tencentcloudapi.vod.v20180717.models.ApplyUploadResponse;
import com.tencentcloudapi.vod.v20180717.models.CommitUploadResponse;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ResourceServiceImpl extends ServiceImpl<RsResourceMapper, RsResource>
        implements IRsResourceService {

    @Resource
    private CosManager cosManager;


    @Resource
    private TencentCloudVodManager tencentCloudVodManager;

    @Resource
    private RsVideoMetaMapper videoMetaMapper;

    @Resource
    private RedisService redisService;

    // ==================== 预签名 ====================

    @Override
    @Transactional
    public PresignedUrlVO generateCosPresignedUrl(Long userId, PresignRequest request, ResourceType resourceType) {
        // 1. 校验文件格式
        String fileName = request.getFileName();
        String fileFormat = FileUtil.getSuffix(fileName).toLowerCase();
        validateFileFormat(fileFormat, resourceType);

        long maxSize = ResourceConstants.getMaxSize(resourceType.getCode());
        if (request.getFileSize() == null || request.getFileSize() <= 0 || request.getFileSize() > maxSize) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不合法");
        }

        // 2. 生成 COS key
        String storageKey = buildStorageKey(resourceType, fileFormat);

        // 3. 生成预签名 URL
        String contentType = ResourceType.getContentType(fileFormat);
        URL presignedUrl = cosManager.generatePresignedUploadUrl(
                storageKey, ResourceConstants.COS_PRESIGN_EXPIRY_MINUTES, contentType);

        // 4. 预创建资源记录（upload_status=待确认）
        RsResource resource = new RsResource();
        resource.setResourceName(fileName);
        resource.setResourceType(resourceType);
        resource.setFileFormat(fileFormat);
        resource.setStorageKey(storageKey);
        resource.setUploaderId(userId);
        resource.setUploadStatus(UploadStatus.PENDING);
        baseMapper.insert(resource);

        // 5. 构建返回
        String accessUrl = cosManager.getFileAccessUrl(storageKey);

        PresignedUrlVO vo = new PresignedUrlVO();
        vo.setResourceId(resource.getResourceId());
        vo.setUploadUrl(presignedUrl.toString());
        vo.setStorageKey(storageKey);
        vo.setAccessUrl(accessUrl);
        vo.setExpiresIn(ResourceConstants.COS_PRESIGN_EXPIRY_MINUTES * 60L);

        log.info("Generated COS presigned URL for user={}, resource={}, key={}",
                userId, resource.getResourceId(), storageKey);
        return vo;
    }

    @Override
    @Transactional
    public VodPresignedUrlVO generateVodPresignedUrl(Long userId, PresignRequest request) {
        // 1. 校验文件格式
        String fileName = request.getFileName();
        String fileFormat = FileUtil.getSuffix(fileName).toLowerCase();
        validateFileFormat(fileFormat, ResourceType.VIDEO);

        long maxSize = ResourceConstants.getMaxSize(ResourceType.VIDEO.getCode());
        if (request.getFileSize() == null || request.getFileSize() <= 0 || request.getFileSize() > maxSize) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不合法");
        }

        // 2. 调腾讯云申请上传
        ApplyUploadResponse applyResponse = tencentCloudVodManager.applyVodUpload(
                fileName, fileFormat, null, null, null, null, null);

        // 3. 预创建资源记录
        RsResource resource = new RsResource();
        resource.setResourceName(fileName);
        resource.setResourceType(ResourceType.VIDEO);
        resource.setFileFormat(fileFormat);
        resource.setUploaderId(userId);
        resource.setUploadStatus(UploadStatus.PENDING);
        baseMapper.insert(resource);

        // 4. 预创建视频元数据记录
        RsVideoMeta videoMeta = new RsVideoMeta();
        videoMeta.setResourceId(resource.getResourceId());
        videoMetaMapper.insert(videoMeta);

        // 5. sessionKey 存 Redis
        String redisKey = ResourceConstants.VOD_SESSION_PREFIX + resource.getResourceId();
        redisService.setCacheObject(redisKey, applyResponse.getVodSessionKey(),
                ResourceConstants.VOD_SESSION_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // 6. 构建返回
        VodPresignedUrlVO vo = new VodPresignedUrlVO();
        vo.setResourceId(resource.getResourceId());
        vo.setVodSessionKey(applyResponse.getVodSessionKey());
        // MediaStoragePath 是上传地址
        if (applyResponse.getMediaStoragePath() != null) {
            vo.setMediaUploadUrls(List.of(applyResponse.getMediaStoragePath()));
        }
        if (applyResponse.getCoverStoragePath() != null) {
            vo.setCoverUploadUrl(applyResponse.getCoverStoragePath());
        }
        vo.setExpiresIn(ResourceConstants.VOD_SESSION_EXPIRE_SECONDS);

        log.info("Generated VOD presigned URL for user={}, resource={}", userId, resource.getResourceId());
        return vo;
    }

    // ==================== 确认上传 ====================

    @Override
    @Transactional
    public ResourceVO confirmCosUpload(Long userId,UploadConfirmRequest request) {
        // 1. 查询资源记录
        RsResource resource = getResourceOrThrow(request.getResourceId());
        if (!UploadStatus.PENDING.equals(resource.getUploadStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该资源不处于待确认状态");
        }

        if (!resource.getUploaderId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权确认他人的资源");
        }

        // 2. 从 COS 查实际文件大小并校验
        ObjectMetadata metadata;
        try {
            metadata = cosManager.getObjectMetadata(resource.getStorageKey());
        } catch (Exception e) {
            log.error("Failed to get COS object metadata, key={}", resource.getStorageKey(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件未上传成功，请重试");
        }

        long actualSize = metadata.getContentLength();
        long maxSize = ResourceConstants.getMaxSize(resource.getResourceType().getCode());
        if (actualSize > maxSize) {
            // 超限：删除 COS 文件，标记失败
            cosManager.deleteObject(resource.getStorageKey());
            resource.setUploadStatus(UploadStatus.FAILED);
            baseMapper.updateById(resource);
            throw new BusinessException(ErrorCode.PARAMS_ERROR,
                    "文件大小超过限制，最大允许 " + (maxSize / 1024 / 1024) + "MB");
        }

        // 3. 更新资源记录（带乐观锁，防止并发重复确认）
        resource.setFileSize(actualSize);
        resource.setAccessUrl(cosManager.getFileAccessUrl(resource.getStorageKey()));
        resource.setUploadStatus(UploadStatus.SUCCESS);
        int affected = baseMapper.updateById(resource);
        if (affected == 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "资源状态已变更，请勿重复确认");
        }

        log.info("COS upload confirmed, resource={}, size={}", resource.getResourceId(), actualSize);
        return buildResourceVO(resource);
    }

    @Override
    @Transactional
    public ResourceDetailVO confirmVodUpload(Long userId,VideoConfirmRequest request) {
        // 1. 查询资源记录
        RsResource resource = getResourceOrThrow(request.getResourceId());
        if (!UploadStatus.PENDING.equals(resource.getUploadStatus())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该资源不处于待确认状态");
        }

        if (!resource.getUploaderId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权确认他人的资源");
        }

        // 2. 从 Redis 获取 sessionKey
        String redisKey = ResourceConstants.VOD_SESSION_PREFIX + request.getResourceId();
        String vodSessionKey = redisService.getCacheObject(redisKey);
        if (vodSessionKey == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "上传会话已过期，请重新上传");
        }

        // 3. 调腾讯云确认上传
        CommitUploadResponse commitResponse = tencentCloudVodManager.commitVodUpload(vodSessionKey);

        // 4. 更新资源记录（带乐观锁，防止并发重复确认）
        resource.setAccessUrl(commitResponse.getMediaUrl());
        resource.setStorageKey(commitResponse.getFileId());
        resource.setUploadStatus(UploadStatus.SUCCESS);
        int affected = baseMapper.updateById(resource);
        if (affected == 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "资源状态已变更，请勿重复确认");
        }

        // 5. 更新视频元数据
        RsVideoMeta videoMeta = videoMetaMapper.selectById(request.getResourceId());
        if (videoMeta != null) {
            videoMeta.setVodFileId(commitResponse.getFileId());
            videoMetaMapper.updateById(videoMeta);
        }

        // 6. 用完删除
        redisService.deleteObject(redisKey);

        log.info("VOD upload confirmed, resource={}, fileId={}", resource.getResourceId(), commitResponse.getFileId());
        return buildResourceDetailVO(resource, videoMeta);
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

        // 1. 删除云存储文件
        if (UploadStatus.SUCCESS.equals(resource.getUploadStatus())
                && resource.getStorageKey() != null) {
            try {
                if (ResourceType.VIDEO.getCode() == resource.getResourceType().getCode()) {
                    // 视频：调 VOD 删除
                    tencentCloudVodManager.deleteVodMedia(resource.getStorageKey());
                } else {
                    // 图片/文档：调 COS 删除
                    cosManager.deleteObject(resource.getStorageKey());
                }
            } catch (Exception e) {
                log.warn("Failed to delete cloud storage, resource={}, key={}, will proceed with db deletion",
                        resourceId, resource.getStorageKey(), e);

            }
        }

        // 2. 逻辑删除资源记录
        this.removeById(resourceId);

        // 3. 如果是视频，删除视频元数据
        if (ResourceType.VIDEO.getCode() == resource.getResourceType().getCode()) {
            videoMetaMapper.deleteById(resourceId);
        }

        // 4. 清理可能残留的 Redis session key
        redisService.deleteObject(ResourceConstants.VOD_SESSION_PREFIX + resourceId);

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
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "资源不存在");
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
     * 生成 COS 存储 key
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

        if (ResourceType.VIDEO.equals(resource.getResourceType())) {
            cleanExpiredVodResource(resource);
        } else {
            cleanExpiredCosResource(resource);
        }


        boolean removed = this.removeById(resourceId);
        if (!removed) {
            log.warn("Expired resource db record remove returned false, resourceId={}", resourceId);
        }

        log.info("Expired upload resource cleaned, resourceId={}, type={}",
                resourceId, resource.getResourceType());
    }

    private void cleanExpiredCosResource(RsResource resource) {
        String storageKey = resource.getStorageKey();

        if (storageKey == null || storageKey.isBlank()) {
            log.info("Expired COS resource has empty storageKey, skip COS delete, resourceId={}",
                    resource.getResourceId());
            return;
        }

        try {
            cosManager.deleteObject(storageKey);
            log.info("Deleted expired COS object, resourceId={}, storageKey={}",
                    resource.getResourceId(), storageKey);
        } catch (Exception e) {

            log.warn("Failed to delete expired COS object, resourceId={}, storageKey={}",
                    resource.getResourceId(), storageKey, e);
        }
    }


    private void cleanExpiredVodResource(RsResource resource) {
        Long resourceId = resource.getResourceId();

        // 1. 删除 Redis 中残留的 VOD 上传会话
        String redisKey = ResourceConstants.VOD_SESSION_PREFIX + resourceId;
        try {
            redisService.deleteObject(redisKey);
            log.info("Deleted expired VOD session key, resourceId={}, redisKey={}", resourceId, redisKey);
        } catch (Exception e) {
            log.warn("Failed to delete expired VOD session key, resourceId={}, redisKey={}",
                    resourceId, redisKey, e);
        }

        // 2. 查询视频元数据
        RsVideoMeta videoMeta = videoMetaMapper.selectById(resourceId);

        /*
         * 3. 删除 VOD 媒资。

         */
        String vodFileId = null;
        if (videoMeta != null && videoMeta.getVodFileId() != null && !videoMeta.getVodFileId().isBlank()) {
            vodFileId = videoMeta.getVodFileId();
        } else if (resource.getStorageKey() != null && !resource.getStorageKey().isBlank()) {
            vodFileId = resource.getStorageKey();
        }

        if (vodFileId != null && !vodFileId.isBlank()) {
            try {
                tencentCloudVodManager.deleteVodMedia(vodFileId);
                log.info("Deleted expired VOD media, resourceId={}, vodFileId={}", resourceId, vodFileId);
            } catch (Exception e) {
                log.warn("Failed to delete expired VOD media, resourceId={}, vodFileId={}",
                        resourceId, vodFileId, e);
            }
        } else {
            log.info("Expired VOD resource has no vodFileId, skip VOD media delete, resourceId={}", resourceId);
        }

        // 4. 删除视频元数据
        try {
            int deleted = videoMetaMapper.deleteById(resourceId);
            log.info("Deleted expired video meta, resourceId={}, deleted={}", resourceId, deleted);
        } catch (Exception e) {
            log.warn("Failed to delete expired video meta, resourceId={}", resourceId, e);
        }
    }

}