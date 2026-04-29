// @ts-ignore
/* eslint-disable */
import request from "../utils/request";

export function login(data) {
  return request({
    url: '/user/auth/login',
    method: 'post',
    withCredentials: true,
    data
  })
}

/**
 * 退出登录
 * **/
export function logout() {
  return request({
    url: '/user/auth/logout',
    method: 'post'
  })
}

/**
 * 注册
 * **/
export function register(data) {
  return request({
    url: '/user/auth/register',
    method: 'post',
    data
  })
}

/** 发送登录验证码 POST /auth/login/send-code */
export function sendLoginCode(data) {
  return request({
    url: '/user/auth/login/send-code',
    method: 'post',
    data
  })
}

/** 刷新Access Token POST /auth/refresh-token */
export function refreshToken(data) {
  return request({
    url: '/user/auth/refresh-token',
    method: 'post',
    data
  })
}

/** 发送注册验证码 POST /auth/register/send-code */
export function sendRegisterCode(data) {
  return request({
    url: '/user/auth/register/send-code',
    method: 'post',
    data
  })
}
