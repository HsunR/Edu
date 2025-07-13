import request from "@/utils/request";

// 获取用户信息
export function getInfo(userId) {
  return request({
    url: `/user/user/getUserInfo/${userId}`,
    method: "get"
  });
}

// 更新用户信息
export function updateInfo(data) {
  return request({
    url: "/user/user/update",
    method: "post",
    data,
  });
}

// 多条件查询
export function list(data) {
  return request({
    url: "/user/user/list",
    method: "post",
    data,
  });
}

// 删除用户
export function deleteUser(data) {
  return request({
    url: "/user/user/deleteUser",
    method: "post",
    data,
  });
}



// 用户头像上传
export function uploadAvatar(data) {
  return request({
    url: "/system/user/profile/avatar",
    method: "post",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    data: data,
  });
}

