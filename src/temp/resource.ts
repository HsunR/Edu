// @ts-ignore
/* eslint-disable */
import request from "../utils/request";

/** 生成图片资源的预签名URL  */
export async function uploadImagePresign(data) {
    return request({
        url: `/resource/resources/presign/image`,
        method: "POST",
        data
    });
}

/** 生成文档资源的预签名URL  */
export async function uploadDocumentPresign(data) {
    return request({
        url: `/resource/resources/presign/document`,
        method: "POST",
        data
    });
}

/** 生成视频资源的预签名URL  */
export async function uploadVideoPresign(data) {
    return request({
        url: `/resource/resources/presign/video`,
        method: "POST",
        data
    });
}


/** 确认资源上传完成  */
export async function uploadConfirm(data) {
    return request({
        url: `/resource/resources/confirm`,
        method: "POST",
        data
    });
}

/** 确认视频资源上传完成  */
export async function uploadConfirmVideo(data) {
    return request({
        url: `/resource/resources/confirm/video`,
        method: "POST",
        data
    });
}

/** 获取资源详情  */
export async function getResource(resourceId) {
    return request({
        url: `/resource/resources/${resourceId}`,
        method: "get"
    });
}

/** 分页查询我的资源  */
export async function getResourceList(data) {
    return request({
        url: `/resource/resources`,
        method: "get",
        data
    });
}

/** 删除资源  */
export async function deleteResource(resourceId) {
    return request({
        url: `/resource/resources/${resourceId}`,
        method: "delete"
    });
}