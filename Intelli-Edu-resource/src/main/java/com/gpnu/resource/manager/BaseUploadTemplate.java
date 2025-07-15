package com.gpnu.resource.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.resource.config.CosClientConfig;


import com.gpnu.resource.model.dto.resource.UploadResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Date;

/**
 * 抽象文件上传模板，封装通用上传逻辑
 * @param <T> 输入源类型 (例如 MultipartFile)
 * @param <R> 上传结果类型 (例如 UploadResult, UploadPictureResult)
 */
@Slf4j
public abstract class BaseUploadTemplate<T, R extends UploadResult> {

    @Resource
    protected CosClientConfig cosClientConfig; // 保护类型，子类可访问

    @Resource
    protected CosManager cosManager; // 保护类型，子类可访问

    /**
     * 校验输入源
     * @param inputSource 输入源
     * @throws BusinessException 如果校验失败
     */
    protected abstract void validateInputSource(T inputSource);

    /**
     * 获取原始文件名
     * @param inputSource 输入源
     * @return 原始文件名
     */
    protected abstract String getOriginalFilename(T inputSource);

    /**
     * 将输入源处理成临时文件
     * @param inputSource 输入源
     * @param tempFile 临时文件对象
     * @throws Exception 文件处理过程中可能发生的异常
     */
    protected abstract void processSourceToTempFile(T inputSource, File tempFile) throws Exception;

    /**
     * 封装具体的上传逻辑和结果转换
     * @param originalFilename 原始文件名
     * @param uploadFileName COS上的文件名
     * @param tempFile 临时文件
     * @return 上传结果
     * @throws Exception 上传过程中可能发生的异常
     */
    protected abstract R doUploadAndBuildResult(String originalFilename, String uploadFileName, File tempFile) throws Exception;

    /**
     * 执行文件上传的模板方法
     * @param inputSource 输入源
     * @param uploadPathPrefix COS上的上传路径前缀 (例如 "images/", "documents/")
     * @return 上传结果
     */
    public R upload(T inputSource, String uploadPathPrefix) {
        // 校验输入源
        validateInputSource(inputSource);

        String uuid = RandomUtil.randomString(16);
        String originalFilename = getOriginalFilename(inputSource);
        String fileSuffix = FileUtil.getSuffix(originalFilename);

        // 拼接上传到COS的完整文件名 (resource_uuid)
        // 格式：日期_UUID.后缀，例如：20250711_abcdef1234567890.jpg
        String uploadFileName = String.format("%s_%s.%s", DateUtil.format(new Date(), "yyyyMMdd"), uuid, fileSuffix);
        String fullUploadPath = String.format("%s%s", uploadPathPrefix, uploadFileName); // COS Key

        File tempFile = null;
        try {
            // 创建临时文件
            tempFile = File.createTempFile("upload-", "." + fileSuffix);
            // 将输入源内容写入临时文件
            processSourceToTempFile(inputSource, tempFile);

            // 执行具体的上传逻辑和结果封装，由子类实现
            return doUploadAndBuildResult(originalFilename, fullUploadPath, tempFile);

        } catch (BusinessException e) {
            log.error("文件上传业务异常: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("文件上传处理失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            // 删除临时文件
            deleteTempFile(tempFile);
        }
    }

    /**
     * 删除临时文件
     * @param file 临时文件对象
     */
    private void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                log.error("Failed to delete temporary file: {}", file.getAbsolutePath());
                // 可以在这里记录到日志系统或报警，或者使用JVM退出钩子进行清理
            }
        }
    }

    /**
     * 删除云存储文件
     */
    public abstract void deleteObject(String key) ;
}