package com.gpnu.common.model.enums.resource;

import com.gpnu.common.exception.BusinessException;
import com.gpnu.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResourceType {

    MP4(10, "mp4"),
    AVI(11, "avi"),
    WMV(12, "wmv"),
    FLV(13, "flv"),
    MKV(14, "mkv"),
    RMVB(15, "rmvb"),

    //图片
    PNG(20, "png"),
    JPG(21, "jpg"),
    JPEG(22, "jpeg"),
    GIF(23, "gif"),
    BMP(24, "bmp"),
    SVG(25, "svg"),

    //文档
    PDF(30, "pdf"),
    DOC(31, "doc"),
    DOCX(32, "docx"),
    PPT(33, "ppt"),
    PPTX(34, "pptx"),
    XLS(35, "xls"),
    XLSX(36, "xlsx"),
    TXT(37, "txt"),
    MD(38, "md"),

    ;

    private final Integer type;
    private final String description;

    public static ResourceType getByType(Integer type) {
        for (ResourceType resourceType : ResourceType.values()) {
            if (resourceType.getType().equals(type)) {
                return resourceType;
            }
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "资源类型不存在: " + type);
    }
    public static ResourceType getByDescription(String description) {
        for (ResourceType resourceType : ResourceType.values()) {
            if (resourceType.getDescription().equalsIgnoreCase(description)) {
                return resourceType;
            }
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "资源类型描述不存在: " + description);
    }
}
