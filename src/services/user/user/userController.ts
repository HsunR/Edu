// @ts-ignore
/* eslint-disable */
import request from "../../../utils/request";

/** 删除用户 根据用户ID删除用户 POST /user/deleteUser */
export async function deleteUser(
  body: API.UsUserDeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(`/user/deleteUser`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 获取用户信息 根据用户ID获取用户信息 GET /user/getUserInfo/${param0} */
// export async function getUserInfo(
//   // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
//   params: API.getUserInfoParams,
//   options?: { [key: string]: any }
// ) {
//   const { userId } = params;
//   return request<API.BaseResponseUsUserVO>(`/user/getUserInfo/${userId}`, {
//     method: "GET",
//     ...(options || {}),
//   });
// }
export function getUserInfo(userId) {
  return request({
    url: `/user/user/getUserInfo/${userId}`,
  })
}

/** 多条件查询 多条件查询 POST /user/list */
export async function listUsers(
  body: API.UsUserQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageUsUserVO>(`/user/list`, {
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
  return request<API.BaseResponseUsUserVO>(`/user/user/update`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
