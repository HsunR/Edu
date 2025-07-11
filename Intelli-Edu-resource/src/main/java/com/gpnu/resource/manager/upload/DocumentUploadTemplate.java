package com.gpnu.resource.manager.upload;

import cn.hutool.core.io.FileUtil;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.resource.model.dto.resource.UploadResult;
import com.qcloud.cos.model.PutObjectResult;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component // 标记为Spring组件
@Slf4j
public class DocumentUploadTemplate extends BaseUploadTemplate<MultipartFile, UploadResult> {

    /**
     * 校验文档输入源
     * @param inputSource MultipartFile 文档文件
     */
    @Override
    protected void validateInputSource(MultipartFile inputSource) {
        if (inputSource == null || inputSource.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文档文件不能为空");
        }
        // 进一步校验文件类型是否为支持的文档格式，例如：
        String originalFilename = inputSource.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename).toLowerCase();
        List<String> documentSuffixes = List.of("doc", "docx", "xls", "xlsx", "ppt", "pptx", "pdf", "md", "txt");
        if (!documentSuffixes.contains(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的文档文件格式");
        }
        // TODO: 校验文件大小等
    }

    /**
     * 获取原始文件名
     * @param inputSource MultipartFile 文档文件
     * @return 原始文件名
     */
    @Override
    protected String getOriginalFilename(MultipartFile inputSource) {
        return inputSource.getOriginalFilename();
    }

    /**
     * 将 MultipartFile 处理成临时文件
     * @param inputSource MultipartFile 文档文件
     * @param tempFile 临时文件对象
     * @throws IOException 如果文件写入失败
     */
    @Override
    protected void processSourceToTempFile(MultipartFile inputSource, File tempFile) throws IOException {
        inputSource.transferTo(tempFile);
    }

    /**
     * 执行文档上传到COS并处理结果
     * @param originalFilename 原始文件名
     * @param uploadFileName COS上的文件名
     * @param tempFile 临时文件
     * @return UploadResult 文档上传结果
     * @throws Exception COS上传失败
     */
    @Override
    protected UploadResult doUploadAndBuildResult(String originalFilename, String uploadFileName, File tempFile) throws Exception {
        // 调用CosManager进行通用文件上传
        PutObjectResult putObjectResult = cosManager.putObject(uploadFileName, tempFile);

        UploadResult uploadResult = new UploadResult();
        uploadResult.setResourceUuid(uploadFileName); // COS上的唯一键 (resource_uuid)
        uploadResult.setResourceName(originalFilename);
        uploadResult.setResourceLink(cosClientConfig.getHost() + "/" + uploadFileName); // 直接可访问的URL
        uploadResult.setResourceSize(FileUtil.size(tempFile));
        uploadResult.setResourceType(FileUtil.getSuffix(originalFilename)); // 文件后缀作为格式

        // 可以在这里根据putObjectResult添加COS特有的ETag等信息
        // uploadResult.setEtag(putObjectResult.getETag());

        return uploadResult;
    }
}