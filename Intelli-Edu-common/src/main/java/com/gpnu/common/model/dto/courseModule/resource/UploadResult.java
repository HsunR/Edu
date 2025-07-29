package com.gpnu.common.model.dto.courseModule.resource;

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
     * 资源类型
     */
    private String type;

    /**
     * 资源链接
     */
    private String resourceLink;

    /**
     * 资源大小
     */
    private Long resourceSize;

}
