package com.gpnu.resource.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResourceType {

    VIDEO(1, "视频"),
    DOCUMENT(2, "文档"),
    IMAGE(3, "图片");

    @EnumValue
    @JsonValue
    private final int code;
    private final String desc;

    // 根据文件后缀判断大类
    public static ResourceType fromFileFormat(String format) {
        if (format == null) return null;
        String lower = format.toLowerCase();
        return switch (lower) {
            case "mp4", "avi", "wmv", "flv", "mkv", "mov" -> VIDEO;
            case "pdf", "doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "md" -> DOCUMENT;
            case "png", "jpg", "jpeg", "gif", "bmp", "svg", "webp" -> IMAGE;
            default -> null;
        };
    }




    public static String getContentType(String fileFormat) {
        if (fileFormat == null) return "application/octet-stream";
        return switch (fileFormat.toLowerCase()) {
            case "png"  -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif"  -> "image/gif";
            case "webp" -> "image/webp";
            case "pdf"  -> "application/pdf";
            case "doc"  -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "ppt"  -> "application/vnd.ms-powerpoint";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "xls"  -> "application/vnd.ms-excel";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "txt"  -> "text/plain";
            case "mp4"  -> "video/mp4";
            case "mov"  -> "video/quicktime";
            case "mkv"  -> "video/x-matroska";
            default     -> "application/octet-stream";
        };
    }


    @JsonCreator
    public static ResourceType fromCode(Integer code) throws IllegalAccessException {
       if(code==null){
           throw new IllegalAccessException("参数不能为空");
       }
        for (ResourceType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        throw new IllegalAccessException("未知的资源类型: " + code);
    }
}