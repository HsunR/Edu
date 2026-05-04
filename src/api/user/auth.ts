import request from '@/api/request'
import type { LoginRequest, LoginResult, SendLoginCodeRequest, RegisterRequest, SendRegisterCodeRequest } from './types'

export function login(data: LoginRequest) {
  return request.post<LoginResult>('/api/user/auth/login', data)
}

export function sendLoginCode(data: SendLoginCodeRequest) {
  return request.post('/api/user/auth/login/send-code', data)
}

export function logout() {
  return request.post('/api/user/auth/logout')
}

export function refreshTokenApi(refreshToken: string) {
  return request.post<LoginResult>('/api/user/auth/refresh-token', null, {
    params: { refreshToken }
  })
}

export function register(data: RegisterRequest) {
  return request.post('/api/user/auth/register', data)
}

export function sendRegisterCode(data: SendRegisterCodeRequest) {
  return request.post('/api/user/auth/register/send-code', data)
}
