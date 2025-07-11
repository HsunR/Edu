package com.gpnu.resource.model.dto.pic;

import com.gpnu.resource.model.dto.resource.UploadResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 图片上传结果VO
 */
@EqualsAndHashCode(callSuper = true) // 确保继承父类的equals和hashCode
@Data
@NoArgsConstructor
public class UploadPictureResult extends UploadResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片比例 (宽度/高度)
     */
    private Double picScale;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;

    // TODO: 如果需要，可以添加原始图的URL或Key，但通常URL就是原图URL
    // private String originalUrl;
}