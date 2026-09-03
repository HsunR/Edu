package com.gpnu.resource.constants;

public class ResourceConstants {


    /** 图片最大 2MB */
    public static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024;

    /** 文档最大 20MB */
    public static final long MAX_DOCUMENT_SIZE = 20 * 1024 * 1024;

    /** 视频最大 1GB */
    public static final long MAX_VIDEO_SIZE = 1024L * 1024 * 1024;

    public static long getMaxSize(int resourceType) {
        return switch (resourceType) {
            case 1 -> MAX_VIDEO_SIZE;
            case 2 -> MAX_DOCUMENT_SIZE;
            case 3 -> MAX_IMAGE_SIZE;
            default -> MAX_DOCUMENT_SIZE;
        };
    }

}
