package com.gpnu.resource.model.dto.resource;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 资源在cos服务器上的唯一标识
     */
    private String resourceUuid;

    /**
     * 资源名称(原始文件名或者上传后的文件名)
     */
    private String resourceName;

    /**
     * 资源类型(根据情况决定)
     */
    private String resourceType;

    /**
     * 资源链接
     */
    private String resourceLink;

    /**
     * 资源大小
     */
    private Long resourceSize;

}
