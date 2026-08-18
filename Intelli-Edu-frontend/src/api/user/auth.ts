import request from '@/api/request'
import type { AxiosRequestConfig } from 'axios'
import type { LoginRequest, LoginResult, SendLoginCodeRequest, RegisterRequest, SendRegisterCodeRequest } from './types'

export function login(data: LoginRequest, config?: AxiosRequestConfig) {
  return request.post<LoginResult>('/api/user/auth/login', data, config)
}

export function sendLoginCode(data: SendLoginCodeRequest, config?: AxiosRequestConfig) {
  return request.post('/api/user/auth/login/send-code', data, config)
}

export function logout(config?: AxiosRequestConfig) {
  return request.post('/api/user/auth/logout', undefined, config)
}

export function refreshTokenApi(refreshToken: string, config?: AxiosRequestConfig) {
  return request.post<LoginResult>('/api/user/auth/refresh-token', null, {
    params: { refreshToken },
    ...config
  })
}

export function register(data: RegisterRequest, config?: AxiosRequestConfig) {
  return request.post('/api/user/auth/register', data, config)
}

export function sendRegisterCode(data: SendRegisterCodeRequest, config?: AxiosRequestConfig) {
  return request.post('/api/user/auth/register/send-code', data, config)
}
