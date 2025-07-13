import request from '@/utils/request'
import { pa } from 'element-plus/es/locales.mjs';

// 登录
export function login(data) {
  return request({
    url: "/user/auth/login",
    method: "post",
    data: data,
  });
}

// 注册
export function register(data) {
  return request({
    url: "/user/auth/register",
    method: "post",
    data: data,
  });
}

// 退出
export function logout() {
  return request({
    url: "/user/auth/logout",
    method: "post",
  });
}

// 发送登录验证码
export function getLoginCode(data) {
  return request({
    url: "/user/auth/login/send-code",
    method: "post",
    data
  });
}

// 发送注册验证码
export function getRegisterCode(data) {
  return request({
    url: "/user/auth/register/send-code",
    method: "post",
    data
  });
}

// 刷新Access Token
export function RefreshToken(refreshToken) {
  return request({
    url: `/user/auth/refresh-token?refreshToken=${refreshToken}`,
    method: "post",
  });
}
