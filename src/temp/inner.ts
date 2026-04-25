// @ts-ignore
/* eslint-disable */
import request from "../utils/request";

/** 获取课程简要信息  */
export async function getCoursesInfo(courseId) {
    return request({
        url: `/course/inner/courses/${courseId}`,
        method: "GET"
    });
}

/** 批量获取课程简要信息  */
export async function getBatchCoursesInfo(data) {
    return request({
        url: `/course/inner/courses/batch`,
        method: "POST",
        data
    });
}


/** 校验学生是否在某班级中  */
export async function checkMemberInClass(classId) {
    return request({
        url: `/course/inner/classes/${classId}/check-member`,
        method: "GET"
    });
}
