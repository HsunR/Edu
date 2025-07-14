// @ts-ignore
/* eslint-disable */
import request from "../utils/request";

/**
 *  获取用户信息
 * **/
export function getUserInfo(userId) {
  return request({
    url: `/user/user/getUserInfo/${userId}`,
  })
}

/**
 * 删除
 * **/
export function deleteUser(data) {
  return request({
    url: '/user/user/deleteUser',
    method: 'post',
    data
  })
}

/** 多条件查询 多条件查询 POST /user/list */
export async function listUsers(
  body: API.UsUserQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageUsUserVO>("/user/user/list", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 更新用户信息 根据用户ID更新用户信息 POST /user/update */
export async function updateUserInfo(
  body: API.UsUserUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUsUserVO>("/user/user/update", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
