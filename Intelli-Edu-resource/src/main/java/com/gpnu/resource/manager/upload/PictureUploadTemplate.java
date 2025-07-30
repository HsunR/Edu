package com.gpnu.resource.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.resource.manager.BaseUploadTemplate; // 引入 BaseUploadTemplate
import com.gpnu.resource.model.dto.pic.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream; // 输入源就是 InputStream
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class PictureUploadTemplate extends BaseUploadTemplate<UploadPictureResult> {


    /**
     * 校验图片输入源
     * @param inputStream 输入流
     * @param originalFilename 原始文件名
     * @param contentLength 文件内容长度
     */
    @Override
    protected void validateInputSource(InputStream inputStream, String originalFilename, long contentLength) {
        if (inputStream == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片文件输入流不能为空");
        }
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原始文件名不能为空");
        }
        if (contentLength <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件内容长度必须大于0");
        }

        String suffix = FileUtil.getSuffix(originalFilename).toLowerCase();
        List<String> imageSuffixes = List.of("jpg", "jpeg", "png", "gif", "bmp", "webp");
        if (!imageSuffixes.contains(suffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的图片文件格式");
        }
        // TODO: 校验文件大小限制（通过 contentLength）
        // 例如：if (contentLength > MAX_IMAGE_SIZE_BYTES) { throw new BusinessException(...); }
    }


    /**
     * 执行图片上传到COS并处理结果
     * @param originalFilename 原始文件名
     * @param uploadFileName COS上的文件名 (resource_uuid)
     * @param inputStream 输入流
     * @param contentLength 文件内容长度
     * @return UploadPictureResult 图片上传结果
     * @throws Exception COS上传或处理失败
     */
    @Override
    protected UploadPictureResult doUploadAndBuildResult(String originalFilename, String uploadFileName, InputStream inputStream, long contentLength) throws Exception {
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
        if (contentLength > 20 * 1024) { // 使用 contentLength 判断文件大小
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            String thumbnailKey = FileUtil.mainName(uploadFileName) + "_thumbnail." + FileUtil.getSuffix(originalFilename);
            thumbnailRule.setFileId(thumbnailKey);
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 128, 128)); // 缩放规则
            rules.add(thumbnailRule);
        }

        picOperations.setRules(rules);

        // 调用CosManager进行图片上传和处理，直接使用 InputStream 重载方法
        PutObjectResult putObjectResult = cosManager.putPictureObject(uploadFileName, inputStream, contentLength, picOperations);

        // 获取图片信息对象
        // 注意：ImageInfo 通常来自 PutObjectResult 的 CiUploadResult.getOriginalInfo().getImageInfo()，
        // 这个 CiUploadResult 是在 PutObjectResult 返回的。
        // 使用 InputStream 上传，CI（图片内容识别）处理会异步进行，其结果会包含在 PutObjectResult 中。
        ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
        // 获取图片处理结果
        ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
        List<CIObject> objectList = processResults.getObjectList();

        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        uploadPictureResult.setResourceUuid(uploadFileName); // COS上的唯一键
        uploadPictureResult.setResourceName(originalFilename);
        uploadPictureResult.setResourceLink(cosClientConfig.getHost() + "/" + uploadFileName); // 原始图URL
        uploadPictureResult.setResourceSize(contentLength); // 使用 contentLength
        uploadPictureResult.setType(imageInfo.getFormat());
        uploadPictureResult.setPicWidth(imageInfo.getWidth());
        uploadPictureResult.setPicHeight(imageInfo.getHeight());
        uploadPictureResult.setPicScale(NumberUtil.round(imageInfo.getWidth() * 1.0 / imageInfo.getHeight(), 2).doubleValue());

        // 处理压缩图和缩略图的URL
        if (CollUtil.isNotEmpty(objectList)) {
            CIObject compressCiObject = objectList.get(0); // 第一个通常是压缩图
            uploadPictureResult.setResourceLink(cosClientConfig.getHost() + "/" + compressCiObject.getKey()); // URL指向压缩后的图片
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

    /**
     * 删除云存储文件
     */
    @Override
    public void deleteObject(String key) throws BusinessException {
        // 删除COS文件的逻辑
        try {
            cosManager.deleteObject(key);
        } catch (Exception e) {
            log.error("删除COS文件失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除COS文件失败，请稍后再试");
        }
    }
}