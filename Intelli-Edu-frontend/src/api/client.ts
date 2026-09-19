import axios, { type AxiosRequestConfig } from 'axios'
import type { LoginResult } from '@/types'

interface Envelope<T> {
  code: number
  data: T
  message: string
  requestId?: string
}
interface RequestConfig extends AxiosRequestConfig {
  retried?: boolean
}

export class ApiError extends Error {
  constructor(
    message: string,
    public code?: number,
    public requestId?: string,
  ) {
    super(message)
  }
}

const client = axios.create({ baseURL: import.meta.env.VITE_API_BASE_URL || '', timeout: 20_000 })

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('intelli-access-token')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

let refreshPromise: Promise<void> | null = null

async function refreshAccessToken() {
  if (refreshPromise) return refreshPromise
  refreshPromise = (async () => {
    const refreshToken = localStorage.getItem('intelli-refresh-token')
    if (!refreshToken) throw new Error('没有刷新令牌')
    const response = await axios.post<Envelope<LoginResult>>(
      `${import.meta.env.VITE_API_BASE_URL || ''}/api/user/auth/refresh-token`,
      null,
      { params: { refreshToken } },
    )
    if (response.data.code !== 0)
      throw new ApiError(response.data.message, response.data.code, response.data.requestId)
    localStorage.setItem('intelli-access-token', response.data.data.accessToken)
    localStorage.setItem('intelli-refresh-token', response.data.data.refreshToken)
  })().finally(() => {
    refreshPromise = null
  })
  return refreshPromise
}

export async function request<T>(config: RequestConfig): Promise<T> {
  try {
    const response = await client.request<Envelope<T>>(config)
    const body = response.data
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code === 40100 && config.url !== '/api/user/auth/refresh-token' && !config.retried) {
        try {
          await refreshAccessToken()
          return request<T>({ ...config, retried: true })
        } catch {
          window.dispatchEvent(new Event('auth-expired'))
          throw new ApiError('登录状态已过期', 40100, body.requestId)
        }
      }
      if (body.code !== 0) throw new ApiError(body.message || '请求失败', body.code, body.requestId)
      return body.data
    }
    return body as T
  } catch (error) {
    if (error instanceof ApiError) throw error
    if (axios.isAxiosError(error)) {
      if (error.response?.status === 401 && !config.retried) {
        try {
          await refreshAccessToken()
          return request<T>({ ...config, retried: true })
        } catch {
          window.dispatchEvent(new Event('auth-expired'))
        }
      }
      const body = error.response?.data as Partial<Envelope<unknown>> | undefined
      throw new ApiError(
        body?.message || (error.code === 'ECONNABORTED' ? '请求超时' : '无法连接后端服务'),
        body?.code,
        body?.requestId,
      )
    }
    throw error
  }
}

export default client
