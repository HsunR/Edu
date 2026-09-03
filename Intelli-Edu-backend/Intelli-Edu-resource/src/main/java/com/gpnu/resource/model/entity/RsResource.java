package com.gpnu.resource.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.time.OffsetDateTime;

import com.gpnu.resource.model.enums.ResourceType;
import com.gpnu.resource.model.enums.UploadStatus;
import lombok.Data;


/**
 * <p>
 * 资源表
 * </p>
 *
 * @author chenxingdong
 * @since 2026-04-17
 */
@Data
@TableName("rs_resource")
public class RsResource implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 主键ID
     */
    @TableId(value = "resource_id", type = IdType.ASSIGN_ID)
    private Long resourceId;

    /**
     * 原始文件名
     */
    private String resourceName;

    /**
     * 大类：1=视频 2=文档 3=图片
     */
    private ResourceType resourceType;

    /**
     * 具体格式：pdf/docx/mp4
     */
    private String fileFormat;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * MinIO 对象键
     */
    private String storageKey;

    /**
     * 访问地址
     */
    private String accessUrl;

    /**
     * 上传者 userId
     */
    private Long uploaderId;

    /**
     * 1=待确认 2=成功 3=失败
     */
    private UploadStatus uploadStatus;

    /**
     * 版本号
     */
    @Version
    private Integer version;

    /**
     * 逻辑删除标识
     */
    @TableLogic
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private OffsetDateTime createdAt;

    /**
     * 更新时间
     */
    private OffsetDateTime updatedAt;


}
