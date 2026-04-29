// @ts-ignore
/* eslint-disable */
import request from "../utils/request";

/**
 *  获取用户信息 (查看他人信息)
 * **/
export function getUserInfo(userId) {
  return request({
    url: `/user/users/${userId}`,
    method: 'get'
  })
}

/**
 *  多条件查询
 * **/
export function listUsers(data) {
  return request({
    url: `/user/users`,
    method: 'get',
    data: data,
    headers: {
      'Content-Type': 'application/json'
    }
  })
}


/** 更新用户信息  */
export function updateUserInfo(data) {
  return request({
    url: `/user/users/me`,
    method: 'put',
    data: data
  })
}

/**
 * 删除
 * **/
export function deleteUser(userId) {
  return request({
    url: `/user/users/${userId}`,
    method: 'delete'
  })
}


/** 更新用户头像  */
export function updateUserAvator(avatarUrl) {
  return request({
    url: `/user/users/me/avatar`,
    method: 'put',
    data: avatarUrl
  })
}

/**
 *  获取当前登录用户信息
 * **/
export function getUserMe() {
  return request({
    url: `/user/users/me`,
    method: 'get'
  })
}

/** 更新当前用户档案信息  */
export function updateUserProfile(data) {
  return request({
    url: `/user/users/me/profile`,
    method: 'put',
    data: data
  })
}

/** 修改密码  */
export function updateUserPassword(data) {
  return request({
    url: `/user/users/me/password`,
    method: 'put',
    data: data
  })
}