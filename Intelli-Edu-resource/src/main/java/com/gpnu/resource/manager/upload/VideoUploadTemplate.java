package com.gpnu.resource.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.resource.manager.BaseUploadTemplate; // 引入 BaseUploadTemplate
import com.gpnu.resource.manager.TencentCloudVodManager; // 引入 TencentCloudVodManager
import com.gpnu.common.model.dto.courseModule.resource.UploadResult; // 确保引入的是 intelli_edu_model 模块的 UploadResult
import com.qcloud.vod.model.VodUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.io.InputStream; // 输入源是 InputStream
import java.util.List;

/**
 * 视频文件上传模板
 */
@Service
@Slf4j
public class VideoUploadTemplate extends BaseUploadTemplate<UploadResult> { // 泛型 T 已移除，R 固定为 UploadResult

    // 注入VOD管理器
    @Resource
    private TencentCloudVodManager tencentCloudVodManager;

    /**
     * 校验视频输入源
     * @param inputStream 输入流
     * @param originalFilename 原始文件名
     * @param contentLength 文件内容长度
     */
    @Override
    protected void validateInputSource(InputStream inputStream, String originalFilename, long contentLength) {
        if (inputStream == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "视频文件输入流不能为空");
        }
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原始文件名不能为空");
        }
        if (contentLength <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件内容长度必须大于0");
        }

        String suffix = FileUtil.getSuffix(originalFilename).toLowerCase();
        List<String> videoSuffixes = List.of("mp4", "avi", "mov", "wmv", "flv", "mkv", "rmvb", "webm"); // 常见视频后缀
        if (!videoSuffixes.contains(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的视频文件格式");
        }
        // TODO: 校验文件大小限制（通过 contentLength）
        // 例如：if (contentLength > MAX_VIDEO_SIZE_BYTES) { throw new BusinessException(...); }
    }


    /**
     * 执行视频上传到VOD并处理结果
     * @param originalFilename 原始文件名
     * @param uploadFileName VOD服务内部的文件名 (对于VOD，uploadFileName 实际上是VOD的MediaName)
     * @param inputStream 输入流
     * @param contentLength 文件内容长度
     * @return UploadResult 视频上传结果
     * @throws Exception VOD上传或处理失败
     */
    @Override
    protected UploadResult doUploadAndBuildResult(String originalFilename, String uploadFileName, InputStream inputStream, long contentLength) throws Exception {
        // 定义VOD任务流名称。
        // 这是在VOD控制台配置的预设任务流，用于自动转码、截图等。
        String vodProcedureName = "YourDefaultProcedure"; // <-- **重要：请替换为您实际配置的VOD任务流名称**

        log.info("开始VOD上传，并指定任务流: {}", vodProcedureName);

        // 调用腾讯云VOD管理器上传视频，直接使用 InputStream
        // uploadFileName 在这里作为 VOD 的 MediaName 传递
        VodUploadResponse vodResponse = tencentCloudVodManager.uploadVideo(
                inputStream,       // 视频文件输入流
                contentLength,     // 文件内容长度
                originalFilename,  // 原始文件名作为MediaName
                null,              // 封面文件路径，可选
                vodProcedureName   // VOD任务流名称
        );

        UploadResult uploadResult = new UploadResult();
        uploadResult.setResourceUuid(vodResponse.getFileId()); // 文件键设置为VOD的FileId
        uploadResult.setResourceName(originalFilename);
        uploadResult.setResourceLink(vodResponse.getMediaUrl()); // VOD返回的媒体播放URL
        uploadResult.setResourceSize(contentLength); // 使用 contentLength
        uploadResult.setType(FileUtil.getSuffix(originalFilename)); // 资源类型（例如 mp4）

        log.info("VOD上传初步响应成功: FileId={}, MediaUrl={}, RequestId={}",
                vodResponse.getFileId(), vodResponse.getMediaUrl(), vodResponse.getRequestId());

        // --- 生产环境重要注意事项 ---
        // 视频转码是异步过程。最终播放URL、封面图、时长等需通过VOD回调通知更新数据库。

        return uploadResult;
    }

    /**
     * 删除云存储文件 (视频)
     */
    @Override
    public void deleteObject(String key) throws BusinessException {
        // 删除VOD视频
        try {
            tencentCloudVodManager.deleteVodMedia(key);
        } catch (Exception e) {
            log.error("删除VOD视频失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除VOD视频失败，请稍后再试");
        }
    }
}