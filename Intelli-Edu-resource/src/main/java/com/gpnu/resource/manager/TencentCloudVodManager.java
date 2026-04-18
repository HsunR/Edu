package com.gpnu.resource.manager;

import cn.hutool.core.io.FileUtil;
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
import org.springframework.beans.factory.annotation.Value; // 引入Value注解
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.io.File; // 仍可能需要File类，但此处不是输入
import java.io.InputStream; // 引入 InputStream

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
    private String region; // 这个region会在upload方法中用到

    /**
     * 上传视频文件到腾讯云VOD (通过 InputStream)
     *
     * @param inputStream   本地视频文件的输入流
     * @param contentLength 文件内容长度
     * @param fileName      原始文件名 (将作为MediaName上传到VOD)
     * @param coverFilePath 视频封面文件路径 (可选)
     * @param procedureName 预设的任务流名称 (可选，用于自动转码、截图等)
     * @return VodUploadResponse 包含 FileId, MediaUrl 等信息
     * @throws BusinessException 如果上传失败
     */
    public VodUploadResponse uploadVideo(InputStream inputStream, long contentLength, String fileName, String coverFilePath, String procedureName) throws BusinessException {
        try {
            VodUploadRequest request = new VodUploadRequest();
            // 修正：将 InputStream 和 contentLength 设置到 request 中，以便 VodUploadClient 使用
            // 注意：VodUploadClient 的 VodUploadRequest 没有直接接受 InputStream 的 setter。
            // 通常SDK内部会处理 InputStream 到临时文件的转换或直接流式上传。
            // 鉴于您提供的VodUploadRequest源码只有 setMediaFilePath(String)，
            // 这意味着您需要先将 InputStream 写入一个临时文件，然后将临时文件路径传递给 setMediaFilePath。
            // **这与我们之前希望避免临时文件的目标有所冲突，是SDK本身的限制。**
            // **所以，为了兼容您提供的SDK源码，我们必须在TencentCloudVodManager内部创建临时文件。**

            File tempFile = null;
            try {
                String fileExtension = "";
                if (fileName != null && fileName.contains(".")) {
                    fileExtension = "." + FileUtil.getSuffix(fileName);
                }
                tempFile = File.createTempFile("vod_upload_", fileExtension);
                FileUtil.writeFromStream(inputStream, tempFile); // 将 InputStream 写入临时文件

                request.setMediaFilePath(tempFile.getAbsolutePath()); // 设置本地临时文件路径
                request.setMediaName(fileName); // 设置媒体名称

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

                log.info("Starting VOD upload for file: {} from temp file: {}", fileName, tempFile.getAbsolutePath());

                // 修正：确保 upload 方法调用语法正确，第一个参数为 region
                VodUploadResponse response = vodUploadClient.upload(region, request);

                log.info("VOD upload successful. FileId: {}, MediaUrl: {}", response.getFileId(), response.getMediaUrl());
                return response;
            } finally {
                if (tempFile != null) {
                    FileUtil.del(tempFile); // 确保删除临时文件
                    log.info("Deleted temporary VOD upload file: {}", tempFile.getAbsolutePath());
                }
            }
        } catch (TencentCloudSDKException e) {
            log.error("Failed to upload video to VOD (TencentCloudSDKException): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "视频上传到点播服务失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to upload video to VOD (General Exception): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "视频上传到点播服务失败，系统内部错误");
        }
    }

    /**
     * 【新方法】申请腾讯云VOD视频上传，获取预签名URL和会话Key。
     * 调用 VOD API 客户端的 ApplyUpload 接口。
     *
     * @param mediaName        媒体名称 (e.g., "my_video.mp4")
     * @param coverType        封面类型 (可选，如 "jpg", "png")
     * @param procedureName    VOD预设的任务流名称 (可选，用于上传后自动转码)
     * @param classId          VOD分类ID (可选)
     * @param sourceContext    来源上下文，透传信息
     * @param sessionContext   会话上下文，透传信息
     * @return ApplyUploadResponse 包含 VodSessionKey, MediaUploadUrls 等信息
     * @throws BusinessException 如果获取失败
     */
    public ApplyUploadResponse applyVodUpload(String mediaName, String mediaType, String coverType, String procedureName, Long classId, String sourceContext, String sessionContext) throws BusinessException {
        try {
            ApplyUploadRequest request = new ApplyUploadRequest();
            request.setMediaName(mediaName);
            request.setMediaType(mediaType); // 媒体类型，通常是文件后缀，如 "mp4", "mov"

            if (coverType != null && !coverType.isEmpty()) {
                request.setCoverType(coverType);
            }
            if (procedureName != null && !procedureName.isEmpty()) {
                request.setProcedure(procedureName);
            }
            if (classId != null) {
                request.setClassId(classId);
            }
            if (sourceContext != null && !sourceContext.isEmpty()) {
                request.setSourceContext(sourceContext);
            }
            if (sessionContext != null && !sessionContext.isEmpty()) {
                request.setSessionContext(sessionContext);
            }

            // 设置子应用ID
            if (vodClientConfig.getAppId() != null && vodClientConfig.getAppId() != 0) {
                request.setSubAppId(vodClientConfig.getAppId());
            }

            log.info("Applying VOD upload for media: {} (Type: {})", mediaName, mediaType);
            // 调用 VOD API 客户端的 ApplyUpload 方法
            ApplyUploadResponse response = vodClient.ApplyUpload(request);

            log.info("Applied VOD upload successfully. VodSessionKey: {}, RequestId: {}", response.getVodSessionKey(), response.getRequestId());
            return response;
        } catch (TencentCloudSDKException e) {
            log.error("Failed to apply VOD upload (TencentCloudSDKException): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "申请视频上传失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to apply VOD upload (General Exception): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "申请视频上传失败，系统内部错误");
        }
    }



    /**
     * 【新方法】确认腾讯云VOD文件上传完成。
     * 调用 VOD API 客户端的 CommitUpload 接口。
     *
     * @param vodSessionKey 上传会话的唯一标识，取申请上传接口的返回值 VodSessionKey
     * @return CommitUploadResponse 确认上传的响应
     * @throws BusinessException 如果确认失败
     */
    public CommitUploadResponse commitVodUpload(String vodSessionKey) throws BusinessException { // 修正参数为 vodSessionKey
        try {
            CommitUploadRequest request = new CommitUploadRequest();
            request.setVodSessionKey(vodSessionKey); // 修正：使用 setVodSessionKey

            // 设置子应用ID
            if (vodClientConfig.getAppId() != null && vodClientConfig.getAppId() != 0) {
                request.setSubAppId(vodClientConfig.getAppId());
            }

            log.info("Committing VOD upload for VodSessionKey: {}", vodSessionKey);
            // 调用 VOD API 客户端的 CommitUpload 方法
            CommitUploadResponse response = vodClient.CommitUpload(request);

            log.info("VOD upload committed successfully. FileId: {}, MediaUrl: {}", response.getFileId(), response.getMediaUrl());
            return response;
        } catch (TencentCloudSDKException e) {
            log.error("Failed to commit VOD upload (TencentCloudSDKException): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "确认视频上传失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to commit VOD upload (General Exception): {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "确认视频上传失败，系统内部错误");
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
            if (vodClientConfig.getAppId() != null && vodClientConfig.getAppId() != 0) {
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