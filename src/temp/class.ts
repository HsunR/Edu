// @ts-ignore
/* eslint-disable */
import request from "../utils/request";

/** 创建班级  */
export async function addClass(data) {
    return request({
        url: `/classes`,
        method: "POST",
        data
    });
}

/** 更新班级信息  */
export async function updateClass(classId, data) {
    return request({
        url: `/classes/${classId}`,
        method: "PUT",
        data
    });
}

/** 班级成员列表  */
export async function classMembers(classId, data) {
    return request({
        url: `/classes/${classId}/members`,
        method: "GET",
        data
    });
}


/** 移除学生  */
export async function deleteClassMember(classId, memberId) {
    return request({
        url: `/classes/${classId}/members/${memberId}`,
        method: "DELETE"
    });
}

/** 通过邀请码加入班级  */
export async function joinClassByInvite(data) {
    return request({
        url: `/classes/join`,
        method: "POST",
        data
    });
}

/** 退出班级  */
export async function quitClass(classId) {
    return request({
        url: `/classes/${classId}/quit`,
        method: "POST"
    });
}


/** 我加入的班级列表  */
export async function myJoinClasses() {
    return request({
        url: `/classes/my`,
        method: "GET"
    });
}