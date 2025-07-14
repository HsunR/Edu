// @ts-ignore
/* eslint-disable */
import request from "../../../utils/request";

/** 用户登录 POST /auth/login */
export async function login(
  body: API.LoginRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLoginResult>(`/user/auth/login`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 发送登录验证码 POST /auth/login/send-code */
export async function sendLoginCode(
  body: API.SendLoginCodeRequest,
  options?: { [key: string]: any }
) {
  return request<any>(`/user/auth/login/send-code`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 用户注销 POST /auth/logout */
export async function logout(options?: { [key: string]: any }) {
  return request<any>(`/user/auth/logout`, {
    method: "POST",
    ...(options || {}),
  });
}

/** 刷新Access Token POST /auth/refresh-token */
export async function refreshToken(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.refreshTokenParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLoginResult>(`/user/auth/refresh-token`, {
    method: "POST",
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 用户注册 POST /auth/register */
export async function register(
  body: API.RegisterRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean>(`/user/auth/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}

/** 发送注册验证码 POST /auth/register/send-code */
export async function sendRegisterCode(
  body: API.SendRegisterCodeRequest,
  options?: { [key: string]: any }
) {
  return request<any>(`/user/auth/register/send-code`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    data: body,
    ...(options || {}),
  });
}
