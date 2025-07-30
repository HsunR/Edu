package com.gpnu.resource.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.model.dto.courseModule.resource.UploadResult;
import com.gpnu.resource.config.CosClientConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;
import java.util.Date;

/**
 * 抽象文件上传模板，封装通用上传逻辑
 * 现在输入源固定为 InputStream
 * @param <R> 上传结果类型 (例如 UploadResult, UploadPictureResult)
 */
@Slf4j
public abstract class BaseUploadTemplate<R extends UploadResult> { // T 泛型已被移除，输入源固定为 InputStream

    @Resource
    protected CosClientConfig cosClientConfig;

    @Resource
    protected CosManager cosManager;

    /**
     * 执行文件上传的模板方法
     *
     * @param inputStream      文件输入流
     * @param originalFilename 文件的原始文件名 (包含后缀)
     * @param contentLength    文件的字节长度
     * @param uploadPathPrefix COS上的上传路径前缀 (例如 "images/", "documents/")
     * @return 上传结果
     */
    public R upload(InputStream inputStream, String originalFilename, long contentLength, String uploadPathPrefix) {
        // 校验输入源
        validateInputSource(inputStream, originalFilename, contentLength);

        String uuid = RandomUtil.randomString(16);
        String fileSuffix = FileUtil.getSuffix(originalFilename);

        String uploadFileName = String.format("%s_%s.%s", DateUtil.format(new Date(), "yyyyMMdd"), uuid, fileSuffix);
        String fullUploadPath = String.format("%s%s", uploadPathPrefix, uploadFileName); // COS Key

        try {
            // 执行具体的上传逻辑和结果封装，由子类实现
            // 直接传递 InputStream 和 contentLength，不再创建临时文件
            return doUploadAndBuildResult(originalFilename, fullUploadPath, inputStream, contentLength);

        } catch (BusinessException e) {
            log.error("文件上传业务异常: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("文件上传处理失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        } finally {
            // 注意：传入的 inputStream 不在此处关闭，由调用方（通常是Dubbo ServiceImpl）负责关闭
            // 或者由 CosManager 内部上传SDK负责关闭
        }
    }

    /**
     * 校验输入源（InputStream 和原始文件名）
     * @param inputStream 输入流
     * @param originalFilename 原始文件名
     * @param contentLength 文件内容长度
     * @throws BusinessException 如果校验失败
     */
    protected abstract void validateInputSource(InputStream inputStream, String originalFilename, long contentLength);

    /**
     * 封装具体的上传逻辑和结果转换
     *
     * @param originalFilename 原始文件名
     * @param uploadFileName   COS上的文件名 (resource_uuid)
     * @param inputStream      文件输入流
     * @param contentLength    文件内容长度
     * @return 上传结果
     * @throws Exception 上传过程中可能发生的异常
     */
    protected abstract R doUploadAndBuildResult(String originalFilename, String uploadFileName, InputStream inputStream, long contentLength) throws Exception;


    /**
     * 删除云存储文件 (抽象方法，子类实现)
     */
    public abstract void deleteObject(String key) throws BusinessException;
}