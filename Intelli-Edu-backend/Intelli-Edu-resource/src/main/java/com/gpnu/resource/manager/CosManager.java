package com.gpnu.resource.manager;

import cn.hutool.core.io.FileUtil;
import com.gpnu.resource.config.CosClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class CosManager {
    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象 (通过 File)
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        log.info("Uploading object to COS. Bucket: {}, Key: {}, File: {}", cosClientConfig.getBucket(), key, file.getName());
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传对象 (通过 InputStream)
     *
     * @param key  唯一键
     * @param inputStream 文件的输入流
     * @param contentLength 文件内容长度
     */
    public PutObjectResult putObject(String key, InputStream inputStream, long contentLength) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(contentLength);
        // 可以设置Content-Type，如果知道的话
        // objectMetadata.setContentType("application/octet-stream");

        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, inputStream, objectMetadata);
        log.info("Uploading object from InputStream. Bucket: {}, Key: {}, ContentLength: {}", cosClientConfig.getBucket(), key, contentLength);
        return cosClient.putObject(putObjectRequest);
    }


    /**
     * 上传图片对象（附带图片信息）(通过 File)
     *
     * @param key  唯一键
     * @param file 文件
     */
    public PutObjectResult putPictureObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 对图片进行处理（获取基本信息也被视作为一种处理）
        PicOperations picOperations = new PicOperations();
        picOperations.setIsPicInfo(1); // 1 表示返回原图信息
        List<PicOperations.Rule> rules = new ArrayList<>();
        // 图片压缩（转成 webp 格式）
        String webpKey = FileUtil.mainName(key) + ".webp";
        PicOperations.Rule compressRule = new PicOperations.Rule();
        compressRule.setRule("imageMogr2/format/webp");
        compressRule.setBucket(cosClientConfig.getBucket());
        compressRule.setFileId(webpKey);
        rules.add(compressRule);
        // 缩略图处理仅对 > 20KB的图片生成缩略图
        if(file.length() > 20 * 1024){ // 使用文件长度判断
            PicOperations.Rule thumbnailRule = new PicOperations.Rule();
            thumbnailRule.setBucket(cosClientConfig.getBucket());
            String thumbnailKey = FileUtil.mainName(key) + "_thumbnail." + FileUtil.getSuffix(key);
            thumbnailRule.setFileId(thumbnailKey);
            thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 128, 128)); // 缩放规则
            rules.add(thumbnailRule);
        }

        picOperations.setRules(rules);
        putObjectRequest.setPicOperations(picOperations);
        log.info("Uploading picture object with processing (from File). Bucket: {}, Key: {}, File: {}", cosClientConfig.getBucket(), key, file.getName());
        return cosClient.putObject(putObjectRequest);
    }

    /**
     * 上传图片对象（附带图片信息）(通过 InputStream)
     *
     * @param key  唯一键
     * @param inputStream 文件的输入流
     * @param contentLength 文件内容长度
     * @param picOperations 图片处理操作
     */
    public PutObjectResult putPictureObject(String key, InputStream inputStream, long contentLength, PicOperations picOperations) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(contentLength);
        // 可以设置Content-Type，如果知道的话，例如 objectMetadata.setContentType("image/jpeg");

        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, inputStream, objectMetadata);
        if (picOperations != null) {
            putObjectRequest.setPicOperations(picOperations);
        }
        log.info("Uploading picture object with processing (from InputStream). Bucket: {}, Key: {}, ContentLength: {}", cosClientConfig.getBucket(), key, contentLength);
        return cosClient.putObject(putObjectRequest);
    }


    /**
     * 下载对象
     *
     * @param key 唯一键
     */
    public COSObject getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        log.info("Downloading object from COS. Bucket: {}, Key: {}", cosClientConfig.getBucket(), key);
        return cosClient.getObject(getObjectRequest);
    }

    /**
     * 删除对象
     */
    public void deleteObject(String key) {
        log.info("Deleting object from COS. Bucket: {}, Key: {}", cosClientConfig.getBucket(), key);
        cosClient.deleteObject(cosClientConfig.getBucket(),key);
    }

    /**
     * 获取文件在COS上的完整URL
     *
     * @param key 文件在COS上的唯一键
     * @return 可直接访问的URL
     */
    public String getFileAccessUrl(String key) {
        return String.format("%s/%s", cosClientConfig.getHost(), key);
    }

    /**
     * 生成 COS 预签名上传 URL
     *
     * @param key               COS 存储 key
     * @param expirationMinutes 有效期（分钟）
     * @param contentType       限制上传的 Content-Type
     * @return 预签名 URL
     */
    public URL generatePresignedUploadUrl(String key, long expirationMinutes, String contentType) {
        Date expiration = new Date(System.currentTimeMillis() + expirationMinutes * 60 * 1000);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                cosClientConfig.getBucket(), key, HttpMethodName.PUT);
        request.setExpiration(expiration);
        if (contentType != null) {
            request.setContentType(contentType);
        }
        return cosClient.generatePresignedUrl(request);
    }


    /**
     * 获取对象元数据（文件大小、Content-Type 等）
     */
    public ObjectMetadata getObjectMetadata(String key) {
        return cosClient.getObjectMetadata(cosClientConfig.getBucket(), key);
    }
}