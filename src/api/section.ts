// @ts-ignore
/* eslint-disable */
import request from "../utils/request";

/** 添加节  */
export async function addSections(chapterId, data) {
    return request({
        url: `/course/chapters/${chapterId}/sections`,
        method: "POST",
        data
    });
}

/** 更新节  */
export async function updateSections(sectionId, data) {
    return request({
        url: `/course/sections/${sectionId}`,
        method: "PUT",
        data
    });
}

/** 批量调整节排序  */
export async function orderSections(chapterId, data) {
    return request({
        url: `/course/chapters/${chapterId}/sections/order`,
        method: "PUT",
        data
    });
}


/** 删除节  */
export async function deleteSections(sectionId) {
    return request({
        url: `/course/sections/${sectionId}`,
        method: "DELETE"
    });
}


/** 添加资源到节  */
export async function addResourcesToSections(sectionId, data) {
    return request({
        url: `/course/sections/${sectionId}/resources`,
        method: "POST",
        data
    });
}

/** 移除节内资源  */
export async function deleteSectionsResources(sectionId) {
    return request({
        url: `/course/sections/${sectionId}/resources/{id}`,
        method: "DELETE"
    });
}


/** 调整节内资源顺序  */
export async function orderSectionsResources(sectionId, data) {
    return request({
        url: `/course/sections/${sectionId}/resources/order`,
        method: "PUT",
        data
    });
}

/** 获取节详情  */
export async function getSectionsDetail(sectionId) {
    return request({
        url: `/course/sections/${sectionId}/detail`,
        method: "GET"
    });
}
