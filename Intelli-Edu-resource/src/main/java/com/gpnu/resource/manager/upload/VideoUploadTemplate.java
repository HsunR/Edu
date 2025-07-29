package com.gpnu.resource.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.resource.manager.BaseUploadTemplate;
import com.gpnu.resource.manager.TencentCloudVodManager;
import com.gpnu.common.model.dto.courseModule.resource.UploadResult;
import com.qcloud.vod.model.VodUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 视频文件上传模板
 */
@Service
@Slf4j
public class VideoUploadTemplate extends BaseUploadTemplate<MultipartFile, UploadResult> { // 假设返回通用UploadResult

    // 注入新的VOD管理器
    @Resource
    private TencentCloudVodManager tencentCloudVodManager;

    @Override
    protected void validateInputSource(MultipartFile inputSource) {
        if (inputSource == null || inputSource.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "视频文件不能为空");
        }
        String originalFilename = inputSource.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "视频文件名不能为空");
        }
        String suffix = FileUtil.getSuffix(originalFilename).toLowerCase();
        List<String> videoSuffixes = List.of("mp4", "avi", "mov", "wmv", "flv", "mkv", "rmvb"); // 常见视频后缀
        if (!videoSuffixes.contains(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的视频文件格式");
        }
        // TODO: 校验文件大小、时长等，可以根据业务需求自定义
        // 例如：if (inputSource.getSize() > MAX_VIDEO_SIZE) { throw new BusinessException(...); }
    }

    @Override
    protected String getOriginalFilename(MultipartFile inputSource) {
        return inputSource.getOriginalFilename();
    }

    @Override
    protected void processSourceToTempFile(MultipartFile inputSource, File tempFile) throws IOException {
        inputSource.transferTo(tempFile);
    }

    @Override
    protected UploadResult doUploadAndBuildResult(String originalFilename, String uploadFileName, File tempFile) throws Exception {
        // 调用腾讯云VOD管理器进行视频上传
        // originalFilename 将作为 MediaName 传递给 VOD
        VodUploadResponse vodResponse = tencentCloudVodManager.uploadVideo(
                tempFile.getAbsolutePath(), // 本地文件路径
                originalFilename,       // 原始文件名作为MediaName
                null,                   // 封面文件路径，可选，这里简化为null
                "YourDefaultProcedure"  // 您的VOD预设任务流名称，例如：TaskFlow-200000000-Default
                                        // 可以在VOD控制台的“任务流设置”中创建，用于自动转码等
        );

        UploadResult uploadResult = new UploadResult();
        uploadResult.setResourceUuid(vodResponse.getFileId()); // 文件键设置为VOD的FileId
        uploadResult.setResourceName(originalFilename);
        uploadResult.setResourceLink(vodResponse.getMediaUrl()); // VOD返回的媒体播放URL
        uploadResult.setResourceSize(FileUtil.size(tempFile)); // 临时文件大小
        uploadResult.setType(FileUtil.getSuffix(originalFilename)); //资源类型

        // TODO: 根据VOD回调通知更新数据库中的视频信息，以获取转码完成后的真实播放URL、封面图URL、时长等
        // 这里返回的URL是VOD上传响应中提供的URL，可能不是转码完成后的最终URL。
        // 生产环境建议通过异步回调机制来更新这些信息。

        return uploadResult;
    }


    @Override
    public void deleteObject(String key) {
        tencentCloudVodManager.deleteVodMedia(key);
    }
}