package com.gpnu.resource.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gpnu.auth.resource.context.UserContextHolder;
import com.gpnu.resource.model.dto.PresignRequest;
import com.gpnu.resource.model.dto.ResourceQueryRequest;
import com.gpnu.resource.model.dto.UploadConfirmRequest;
import com.gpnu.resource.model.dto.VideoConfirmRequest;
import com.gpnu.resource.model.enums.ResourceType;
import com.gpnu.resource.model.vo.PresignedUrlVO;
import com.gpnu.resource.model.vo.ResourceDetailVO;
import com.gpnu.resource.model.vo.ResourceVO;
import com.gpnu.resource.model.vo.VodPresignedUrlVO;
import com.gpnu.resource.service.IRsResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resources")
@Tag(name = "资源管理", description = "提供资源的预签名URL生成、上传确认、查询和删除等功能")
public class ResourceController {


    @Resource
    private IRsResourceService resourceService;

    // --- 预签名 ---

    @PostMapping("/presign/image")
    @Operation(summary = "生成图片资源的预签名URL", description = "根据请求参数生成用于上传图片资源的预签名URL")
    public PresignedUrlVO presignImage(@RequestBody @Validated PresignRequest request) {
        Long userId = UserContextHolder.getUserId();
        return resourceService.generateCosPresignedUrl(userId, request, ResourceType.IMAGE);
    }

    @PostMapping("/presign/document")
    @Operation(summary = "生成文档资源的预签名URL", description = "根据请求参数生成用于上传文档资源的预签名URL")
    public PresignedUrlVO presignDocument(@RequestBody @Validated PresignRequest request) {
        Long userId = UserContextHolder.getUserId();
        return resourceService.generateCosPresignedUrl(userId, request, ResourceType.DOCUMENT);
    }

    @PostMapping("/presign/video")
    @Operation(summary = "生成视频资源的预签名URL", description = "根据请求参数生成用于上传视频资源的预签名URL")
    public VodPresignedUrlVO presignVideo(@RequestBody @Validated PresignRequest request) {
        Long userId = UserContextHolder.getUserId();
        return resourceService.generateVodPresignedUrl(userId, request);
    }

    // --- 确认上传 ---

    @PostMapping("/confirm")
    @Operation(summary = "确认资源上传完成", description = "客户端上传完成后调用此接口，确认资源已成功上传并持久化相关信息")
    public ResourceVO confirmUpload(@RequestBody @Validated UploadConfirmRequest request) {
        return resourceService.confirmCosUpload(request);
    }

    @PostMapping("/confirm/video")
    @Operation(summary = "确认视频资源上传完成", description = "客户端上传完成后调用此接口，确认视频资源已成功上传并持久化相关信息")
    public ResourceDetailVO confirmVideoUpload(@RequestBody @Validated VideoConfirmRequest request) {
        return resourceService.confirmVodUpload(request);
    }

    // --- 查询/删除 ---

    @GetMapping("/{resourceId}")
    @Operation(summary = "获取资源详情", description = "根据资源ID获取资源的详细信息")
    public ResourceDetailVO getResource(@PathVariable Long resourceId) {
        return resourceService.getResourceDetail(resourceId);
    }

    @GetMapping
    @Operation(summary = "分页查询我的资源", description = "根据查询条件分页查询当前用户上传的资源列表")
    public Page<ResourceVO> listMyResources(ResourceQueryRequest request) {
        Long userId = UserContextHolder.getUserId();
        return resourceService.listResources(userId, request);
    }

    @DeleteMapping("/{resourceId}")
    @Operation(summary = "删除资源", description = "根据资源ID删除资源，只有资源所有者可以删除")
    public void deleteResource(@PathVariable Long resourceId) {
        Long userId = UserContextHolder.getUserId();
        resourceService.deleteResource(userId, resourceId);
    }
}