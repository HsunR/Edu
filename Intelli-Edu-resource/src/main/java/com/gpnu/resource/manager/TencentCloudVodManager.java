package com.gpnu.resource.manager;

import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.resource.config.VodClientConfig;
import com.qcloud.vod.VodUploadClient; // 引入用于上传的客户端
import com.qcloud.vod.model.VodUploadRequest; // 引入上传请求模型
import com.qcloud.vod.model.VodUploadResponse; // 引入上传响应模型
import com.tencentcloudapi.common.exception.TencentCloudSDKException; // 引入腾讯云SDK异常
import com.tencentcloudapi.vod.v20180717.VodClient; // 引入VOD API客户端
import com.tencentcloudapi.vod.v20180717.models.*; // 引入VOD相关API模型（如DescribeMediaInfosRequest/Response）
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.io.File; // 确保File类导入

/**
 * 腾讯云VOD点播服务管理器
 * 封装视频上传、媒体查询、删除、获取播放URL等VOD相关操作
 */
@Component
@Slf4j
public class TencentCloudVodManager {

    @Resource
    private VodUploadClient vodUploadClient; // 用于上传，来自com.qcloud.vod包

    @Resource
    private VodClient vodClient; // 用于其他VOD API操作，来自tencentcloudapi.vod.v20180717包

    @Resource
    private VodClientConfig vodClientConfig; // 用于获取应用ID等配置

    @Value("${vod.client.region}") // VOD API请求的地域，例如 ap-shanghai
    private String region;

    /**
     * 上传视频文件到腾讯云VOD
     *
     * @param mediaFilePath 本地视频文件路径
     * @param fileName      原始文件名 (将作为MediaName上传到VOD)
     * @param coverFilePath 视频封面文件路径 (可选)
     * @param procedureName 预设的任务流名称 (可选，用于自动转码、截图等)
     * @return VodUploadResponse 包含 FileId, MediaUrl 等信息
     * @throws BusinessException 如果上传失败
     */
    public VodUploadResponse uploadVideo(String mediaFilePath, String fileName, String coverFilePath, String procedureName) throws BusinessException {
        try {
            VodUploadRequest request = new VodUploadRequest();
            request.setMediaFilePath(mediaFilePath); // 设置本地文件路径

            // MediaName 是 ApplyUploadRequest 的属性，VodUploadRequest 继承了它
            request.setMediaName(fileName); // 修正：设置媒体名称

            if (coverFilePath != null && !coverFilePath.isEmpty()) {
                request.setCoverFilePath(coverFilePath);
            }
            if (procedureName != null && !procedureName.isEmpty()) {
                request.setProcedure(procedureName);
            }

            // 设置子应用ID，从 vodClientConfig 获取 appId
            if (vodClientConfig.getAppId() != null && vodClientConfig.getAppId() != 0) {
                request.setSubAppId(vodClientConfig.getAppId());
            }

            log.info("Starting VOD upload for file: {}", fileName);

            // 修正：确保 upload 方法调用语法正确，参数为 request
            VodUploadResponse response = vodUploadClient.upload(region,request);

            log.info("VOD upload successful. FileId: {}, MediaUrl: {}", response.getFileId(), response.getMediaUrl());
            return response;
        } catch (TencentCloudSDKException e) { // 捕获腾讯云SDK特定的异常
            log.error("Failed to upload video to VOD (TencentCloudSDKException): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "视频上传到点播服务失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to upload video to VOD (General Exception): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "视频上传到点播服务失败，系统内部错误");
        }
    }

    /**
     * 根据FileId获取媒体播放URL（通常转码后会有多个URL，这里获取默认的）
     *
     * @param fileId VOD的媒体文件ID
     * @return 媒体播放URL
     * @throws BusinessException 如果查询失败或无可用URL
     */
    public String getMediaPlayUrl(String fileId) throws BusinessException {
        try {
            DescribeMediaInfosRequest request = new DescribeMediaInfosRequest();
            request.setFileIds(new String[]{fileId});

            // 设置子应用ID
            if (vodClientConfig.getAppId() != null && vodClientConfig.getAppId() != 0) { // 修正: 使用 getAppId()
                request.setSubAppId(vodClientConfig.getAppId());
            }

            DescribeMediaInfosResponse response = vodClient.DescribeMediaInfos(request);

            if (response != null && response.getMediaInfoSet() != null && response.getMediaInfoSet().length > 0) {
                MediaInfo mediaInfo = response.getMediaInfoSet()[0];
                if (mediaInfo.getBasicInfo() != null && mediaInfo.getBasicInfo().getMediaUrl() != null) {
                    return mediaInfo.getBasicInfo().getMediaUrl();
                }
            }
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "未找到视频播放URL");
        } catch (TencentCloudSDKException e) {
            log.error("Failed to get VOD media info for FileId {} (TencentCloudSDKException): {}", fileId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取视频播放URL失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to get VOD media info for FileId {} (General Exception): {}", fileId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取视频播放URL失败，系统内部错误");
        }
    }

    /**
     * 删除VOD中的媒体文件
     *
     * @param fileId VOD的媒体文件ID
     * @throws BusinessException 如果删除失败
     */
    public void deleteVodMedia(String fileId) throws BusinessException {
        try {
            DeleteMediaRequest request = new DeleteMediaRequest();
            request.setFileId(fileId);


            // 设置子应用ID
            if (vodClientConfig.getAppId() != null && vodClientConfig.getAppId() != 0) {
                request.setSubAppId(vodClientConfig.getAppId());
            }

            vodClient.DeleteMedia(request);
            log.info("VOD media deleted successfully. FileId: {}", fileId);
        } catch (TencentCloudSDKException e) {
            log.error("Failed to delete VOD media for FileId {} (TencentCloudSDKException): {}", fileId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除点播视频失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to delete VOD media for FileId {} (General Exception): {}", fileId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除点播视频失败，系统内部错误");
        }
    }

    // TODO: 可以添加更多VOD操作，例如：
    // * ProcessMedia (发起转码、水印、截图等任务)
    // * DescribeTaskDetail (查询任务详情，用于回调处理)
    // * SimpleHlsDrm (简化HLS DRM加密)
    // * etc.
}