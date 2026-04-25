// @ts-ignore
/* eslint-disable */
import request from "../utils/request";

/** 创建课程  */
export async function createCourse(data) {
    return request({
        url: `/course/courses`,
        method: "POST",
        data
    });
}

/** 更新课程基本信息  */
export async function updateCourse(courseId, data) {
    return request({
        url: `/course/courses/${courseId}`,
        method: "PUT",
        data
    });
}

/** 发布课程  */
export async function publishCourse(courseId) {
    return request({
        url: `/course/courses/${courseId}/publish`,
        method: "PUT"
    });
}

/** 发布课程  */
export async function archiveCourse(courseId) {
    return request({
        url: `/course/courses/${courseId}/archive`,
        method: "PUT"
    });
}

/** 删除课程  */
export async function deleteCourse(courseId) {
    return request({
        url: `/course/courses/${courseId}`,
        method: "DELETE"
    });
}

/** 我教的课程列表  */
export async function teachingCourse() {
    return request({
        url: `/course/courses/teaching`,
        method: "GET"
    });
}

/** 查看课程下的所有班级  */
export async function courseClasses(courseId) {
    return request({
        url: `/course/courses/${courseId}/classes`,
        method: "GET"
    });
}

/** 浏览公开课程  */
export async function openingCourse(data) {
    return request({
        url: `/course/courses`,
        method: "GET",
        data
    });
}

/** 课程详情  */
export async function courseDetail(courseId) {
    return request({
        url: `/course/courses/${courseId}`,
        method: "GET"
    });
}


/** 获取分类树  */
export async function categories() {
    return request({
        url: `/course/categories`,
        method: "GET"
    });
}