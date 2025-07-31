package com.gpnu.resource.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.common.model.dto.resourceModule.resource.UploadResult;
import com.gpnu.resource.manager.BaseUploadTemplate; // 引入改造后的 BaseUploadTemplate
import com.qcloud.cos.model.PutObjectResult;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream; // 输入源现在是 InputStream
import java.util.List;

@Service
@Slf4j
public class DocumentUploadTemplate extends BaseUploadTemplate<UploadResult> { // 泛型 T 被移除，R 固定为 UploadResult


    /**
     * 校验文档输入源 (针对 InputStream)
     * 实现 BaseUploadTemplate 中的抽象方法.
     *
     * @param inputStream      输入流
     * @param originalFilename 原始文件名
     * @param contentLength    文件内容长度
     */
    @Override
    protected void validateInputSource(InputStream inputStream, String originalFilename, long contentLength) {
        if (inputStream == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文档文件输入流不能为空");
        }
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原始文件名不能为空");
        }
        if (contentLength <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件内容长度必须大于0");
        }

        String suffix = FileUtil.getSuffix(originalFilename).toLowerCase();
        List<String> documentSuffixes = List.of("doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "md", "txt");
        if (!documentSuffixes.contains(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的文档文件格式");
        }
        // TODO: 校验文件大小限制（通过 contentLength），例如：
        // if (contentLength > MAX_DOCUMENT_SIZE_BYTES) { throw new BusinessException(...); }
    }


    /**
     * 执行文档上传到COS并处理结果
     * 实现 BaseUploadTemplate 中的抽象方法.
     *
     * @param originalFilename 原始文件名
     * @param uploadFileName   COS上的文件名 (resource_uuid)
     * @param inputStream      文件输入流
     * @param contentLength    文件内容长度
     * @return UploadResult 文档上传结果
     * @throws Exception COS上传失败
     */
    @Override
    protected UploadResult doUploadAndBuildResult(String originalFilename, String uploadFileName, InputStream inputStream, long contentLength) throws Exception {
        // 调用CosManager进行通用文件上传，直接使用 InputStream 重载方法.
        PutObjectResult putObjectResult = cosManager.putObject(uploadFileName, inputStream, contentLength);

        UploadResult uploadResult = new UploadResult();
        uploadResult.setResourceUuid(uploadFileName); // COS上的唯一键 (resource_uuid)
        uploadResult.setResourceName(originalFilename);
        uploadResult.setResourceLink(cosClientConfig.getHost() + "/" + uploadFileName); // 直接可访问的URL
        uploadResult.setResourceSize(contentLength); // 使用 contentLength，而不是临时文件大小
        uploadResult.setType(FileUtil.getSuffix(originalFilename)); // 文件后缀作为格式

        // 可以在这里根据putObjectResult添加COS特有的ETag等信息
        // uploadResult.setEtag(putObjectResult.getETag());

        return uploadResult;
    }

    /**
     * 删除云存储文件
     * 实现 BaseUploadTemplate 中的抽象方法.
     */
    @Override
    public void deleteObject(String key) throws BusinessException {
        // 删除COS文件的逻辑
        try {
            cosManager.deleteObject(key);
        } catch (Exception e) {
            log.error("删除文档文件失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除文档文件失败，请稍后再试");
        }
    }
}