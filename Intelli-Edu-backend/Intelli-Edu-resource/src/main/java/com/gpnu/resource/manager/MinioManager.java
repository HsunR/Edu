package com.gpnu.resource.manager;

import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import com.gpnu.resource.config.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.SetBucketPolicyArgs;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Small MinIO gateway used by the resource domain service.
 */
@Component
@Slf4j
public class MinioManager implements ApplicationRunner {

    private final MinioClient minioClient;
    private final MinioClient minioPresignClient;
    private final MinioProperties properties;

    public MinioManager(
            MinioClient minioClient,
            @Qualifier("minioPresignClient") MinioClient minioPresignClient,
            MinioProperties properties) {
        this.minioClient = minioClient;
        this.minioPresignClient = minioPresignClient;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (properties.isAutoCreateBucket()) {
            initializeBucket();
        }
    }

    public void initializeBucket() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(properties.getBucket()).build());
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(properties.getBucket()).build());
                log.info("Created MinIO bucket: {}", properties.getBucket());
            }

            if (properties.isPublicRead()) {
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(properties.getBucket())
                                .config(buildPublicReadPolicy())
                                .build());
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize MinIO bucket " + properties.getBucket(), e);
        }
    }

    public String generatePresignedUploadUrl(String key) {
        try {
            return minioPresignClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Http.Method.PUT)
                            .bucket(properties.getBucket())
                            .object(key)
                            .expiry(properties.getPresignExpiryMinutes(), TimeUnit.MINUTES)
                            .build());
        } catch (Exception e) {
            log.error("Failed to create MinIO presigned upload URL, key={}", key, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "生成文件上传地址失败");
        }
    }

    public StatObjectResponse getObjectMetadata(String key) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(key)
                            .build());
        } catch (Exception e) {
            log.error("Failed to read MinIO object metadata, key={}", key, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件未上传成功，请重试");
        }
    }

    public void deleteObject(String key) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(key)
                            .build());
        } catch (Exception e) {
            log.error("Failed to delete MinIO object, key={}", key, e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除存储文件失败");
        }
    }

    public String getFileAccessUrl(String key) {
        String endpoint = trimTrailingSlash(properties.getPublicEndpoint());
        return endpoint + "/" + properties.getBucket() + "/" + key;
    }

    public long getPresignExpirySeconds() {
        return TimeUnit.MINUTES.toSeconds(properties.getPresignExpiryMinutes());
    }

    private String buildPublicReadPolicy() {
        return """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"AWS": ["*"]},
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(properties.getBucket());
    }

    private String trimTrailingSlash(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("minio.public-endpoint must not be blank");
        }
        return value.endsWith("/") ? value.substring(0, value.length() - 1) : value;
    }
}
