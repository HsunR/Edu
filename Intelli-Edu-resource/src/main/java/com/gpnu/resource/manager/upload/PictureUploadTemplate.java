package com.gpnu.resource.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.resource.manager.BaseUploadTemplate;
import com.gpnu.resource.model.dto.pic.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // 假设输入源是MultipartFile

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PictureUploadTemplate extends BaseUploadTemplate<MultipartFile, UploadPictureResult> {



    /**
     * 校验图片输入源
     * @param inputSource MultipartFile 图片文件
     */
    @Override
    protected void validateInputSource(MultipartFile inputSource) {
        if (inputSource == null || inputSource.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片文件不能为空");
        }
        // 进一步校验文件类型是否为图片，例如：
        String originalFilename = inputSource.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename).toLowerCase();
        List<String> imageSuffixes = List.of("jpg", "jpeg", "png", "gif", "bmp", "webp");
        if (!imageSuffixes.contains(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的图片文件格式");
        }
        // TODO: 校验文件大小等(以后再根据业务考虑实现)
    }

    /**
     * 获取原始文件名
     * @param inputSource MultipartFile 图片文件
     * @return 原始文件名
     */
    @Override
    protected String getOriginalFilename(MultipartFile inputSource) {
        return inputSource.getOriginalFilename();
    }

    /**
     * 将 MultipartFile 处理成临时文件
     * @param inputSource MultipartFile 图片文件
     * @param tempFile 临时文件对象
     * @throws IOException 如果文件写入失败
     */
    @Override
    protected void processSourceToTempFile(MultipartFile inputSource, File tempFile) throws IOException {
        inputSource.transferTo(tempFile);
    }

    /**
     * 执行图片上传到COS并处理结果
     * @param originalFilename 原始文件名
     * @param uploadFileName COS上的文件名
     * @param tempFile 临时文件
     * @return UploadPictureResult 图片上传结果
     * @throws Exception COS上传或处理失败
     */
    @Override
    protected UploadPictureResult doUploadAndBuildResult(String originalFilename, String uploadFileName, File tempFile) throws Exception {
        PicOperations picOperations = new PicOperations();
        picOperations.setIsPicInfo(1); // 返回原图信息
        List<PicOperations.Rule> rules = new ArrayList<>();

        // 图片压缩（转成 webp 格式）
        String webpKey = FileUtil.mainName(uploadFileName) + ".webp";
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setRule("imageMogr2/format/webp");
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setFileId(webpKey);
        rules.add(compressRule);

        // 缩略图处理（仅对 > 20KB的图片生成缩略图）
        if (tempFile.length() > 20 * 1024) {
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            // 拼接缩略图的路径，通常命名为 _thumbnail 或 _thumb
            String thumbnailKey = FileUtil.mainName(uploadFileName) + "_thumbnail." + FileUtil.getSuffix(originalFilename);
            thumbnailRule.setFileId(thumbnailKey);
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 128, 128)); // 缩放规则
            rules.add(thumbnailRule);
        }

        picOperations.setRules(rules);

        // 调用CosManager进行图片上传和处理
        PutObjectResult putObjectResult = cosManager.putPictureObject(uploadFileName, tempFile);

        // 获取图片信息对象
        ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
        // 获取图片处理结果
        ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
        List<CIObject> objectList = processResults.getObjectList();

        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        uploadPictureResult.setResourceUuid(uploadFileName); // COS上的唯一键
        uploadPictureResult.setResourceName(originalFilename);
        uploadPictureResult.setResourceLink(cosClientConfig.getHost() + "/" + uploadFileName); // 原始图URL
        uploadPictureResult.setResourceSize(tempFile.length());
        uploadPictureResult.setType(imageInfo.getFormat());
        uploadPictureResult.setPicWidth(imageInfo.getWidth());
        uploadPictureResult.setPicHeight(imageInfo.getHeight());
        uploadPictureResult.setPicScale(NumberUtil.round(imageInfo.getWidth() * 1.0 / imageInfo.getHeight(), 2).doubleValue());

        // 处理压缩图和缩略图的URL
        if (CollUtil.isNotEmpty(objectList)) {
            CIObject compressCiObject = objectList.get(0); // 第一个通常是压缩图
            uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + compressCiObject.getKey()); // URL指向压缩后的图片
            uploadPictureResult.setResourceSize(compressCiObject.getSize().longValue());
            uploadPictureResult.setPicWidth(compressCiObject.getWidth());
            uploadPictureResult.setPicHeight(compressCiObject.getHeight());
            uploadPictureResult.setType(compressCiObject.getFormat());

            if (objectList.size() > 1) { // 如果有生成缩略图
                CIObject thumbnailCiObject = objectList.get(1);
                uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());
            } else {
                // 如果没有独立生成缩略图，默认缩略图URL和压缩图URL一致
                uploadPictureResult.setThumbnailUrl(uploadPictureResult.getResourceLink());
            }
        }
        return uploadPictureResult;
    }

    @Override
    public void deleteObject(String key) {
        // 删除文档文件的逻辑
        try {
            cosManager.deleteObject(key);
        } catch (Exception e) {
            log.error("删除文档文件失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除文档文件失败，请稍后再试");
        }
    }
}