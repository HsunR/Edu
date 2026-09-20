import axios, { type AxiosRequestConfig } from 'axios'
import JSONbig from 'json-bigint'
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

const json = JSONbig({ storeAsString: true })
export const parseJsonResponse = (data: unknown) => {
  if (typeof data !== 'string' || !data.trim()) return data
  try {
    return json.parse(data)
  } catch {
    return data
  }
}

export function normalizePageMetadata<T>(data: T): T {
  if (!data || typeof data !== 'object') return data
  const page = data as Record<string, unknown>
  if (
    !Array.isArray(page.records) ||
    typeof page.current !== 'number' ||
    typeof page.size !== 'number'
  )
    return data
  const reportedTotal = typeof page.total === 'number' ? page.total : 0
  const offset = Math.max(0, page.current - 1) * page.size
  // 部分后端分页查询未执行 count，total 会错误地返回 0；至少保证当前页数据与翻页入口自洽。
  const minimumTotal = offset + page.records.length + (page.records.length === page.size ? 1 : 0)
  page.total = Math.max(reportedTotal, minimumTotal)
  page.pages = page.size ? Math.ceil((page.total as number) / page.size) : 0
  return data
}

const client = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '',
  timeout: 20_000,
  transformResponse: [parseJsonResponse],
})

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
      { params: { refreshToken }, transformResponse: [parseJsonResponse] },
    )
    if (response.data.code !== 0)
      throw new ApiError(response.data.message, response.data.code, response.data.requestId)
    localStorage.setItem('intelli-access-token', response.data.data.accessToken)
    localStorage.setItem('intelli-refresh-token', response.data.data.refreshToken)
    window.dispatchEvent(
      new CustomEvent('auth-token-refreshed', { detail: response.data.data.accessToken }),
    )
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
      return normalizePageMetadata(body.data)
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
        body?.message ||
          (error.code === 'ECONNABORTED'
            ? '请求超时，请稍后重试'
            : error.response
              ? `请求失败（${error.response.status}），请稍后重试`
              : '暂时无法连接，请检查网络或稍后重试'),
        body?.code,
        body?.requestId,
      )
    }
    throw error
  }
}

export default client
