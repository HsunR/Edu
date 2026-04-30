// @ts-ignore
/* eslint-disable */
import request from "../../utils/request";

/** 添加章  */
export async function addChapters(courseId, data) {
    return request({
        url: `/course/courses/${courseId}/chapters`,
        method: "POST",
        data
    });
}

/** 更新章标题  */
export async function updateChapters(chapterId, data) {
    return request({
        url: `/course/chapters/${chapterId}`,
        method: "PUT",
        data
    });
}

/** 批量调整章排序  */
export async function orderChapters(courseId, data) {
    return request({
        url: `/course/courses/${courseId}/chapters/order`,
        method: "PUT",
        data
    });
}


/** 删除章  */
export async function deleteChapters(chapterId) {
    return request({
        url: `/course/chapters/${chapterId}`,
        method: "DELETE"
    });
}

