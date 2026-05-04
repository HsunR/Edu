import axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type AxiosResponse,
  type InternalAxiosRequestConfig
} from 'axios'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { getToken, getRefreshToken, setToken, setRefreshToken, removeToken } from '@/utils/auth'
import { saveAs } from 'file-saver'
import JSONbig from 'json-bigint'
import { ErrorCode } from '@/types/api'

const JSONbigString = JSONbig({ storeAsString: true })

let isRefreshing = false
let pendingRequests: Array<(token: string) => void> = []
let reloginShown = false

interface TypedAxiosInstance extends AxiosInstance {
  get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T>
  post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
  put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T>
  delete<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T>
}

const service: TypedAxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,
  timeout: 30000,
  transformResponse: [
    (data) => {
      if (typeof data === 'string') {
        try {
          return JSONbigString.parse(data)
        } catch {
          return data
        }
      }
      return data
    }
  ]
})

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken()
    const noToken = config.headers?.noToken === true

    if (token && !noToken) {
      config.headers.Authorization = `Bearer ${token}`
    }

    if (config.data instanceof FormData) {
      config.headers['Content-Type'] = 'multipart/form-data'
    } else if (!config.headers['Content-Type']) {
      config.headers['Content-Type'] = 'application/json'
    }

    delete config.headers.noToken

    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  (response: AxiosResponse) => {
    if (response.request.responseType === 'blob' || response.request.responseType === 'arraybuffer') {
      return response.data
    }
    
    const res = response.data
    
    if (res && typeof res === 'object' && 'code' in res && 'data' in res) {
      const { code, data, message } = res
      
      if (code !== ErrorCode.SUCCESS) {
        switch (code) {
          case ErrorCode.NOT_LOGIN_ERROR:
            if (!reloginShown) {
              reloginShown = true
              ElMessageBox.confirm('登录状态已过期，请重新登录', '系统提示', {
                confirmButtonText: '重新登录',
                cancelButtonText: '取消',
                type: 'warning'
              })
                .then(() => {
                  removeToken()
                  location.href = '/login'
                })
                .catch(() => {})
                .finally(() => {
                  reloginShown = false
                })
            }
            break
          case ErrorCode.NO_AUTH_ERROR:
            ElMessage.error(message || '无权限访问')
            break
          case ErrorCode.FORBIDDEN_ERROR:
            ElMessage.error(message || '禁止访问')
            break
          case ErrorCode.NOT_FOUND_ERROR:
            ElMessage.error(message || '请求数据不存在')
            break
          case ErrorCode.PARAMS_ERROR:
            ElMessage.error(message || '请求参数错误')
            break
          case ErrorCode.SYSTEM_ERROR:
            ElMessage.error(message || '系统内部异常')
            break
          case ErrorCode.OPERATION_ERROR:
            ElMessage.error(message || '操作失败')
            break
          default:
            ElMessage.error(message || '请求失败')
        }
        return Promise.reject(new Error(message || '请求失败'))
      }
      
      return data
    }
    
    return res
  },
  async (error) => {
    const { response, config } = error

    if (!response) {
      const msg = error.message
      if (msg === 'Network Error') {
        ElMessage.error('后端接口连接异常')
      } else if (msg.includes('timeout')) {
        ElMessage.error('系统接口请求超时')
      } else {
        ElMessage.error(msg)
      }
      return Promise.reject(error)
    }

    if (response.status === 401 && !config._retry) {
      if (isRefreshing) {
        return new Promise((resolve) => {
          pendingRequests.push((token: string) => {
            config.headers.Authorization = `Bearer ${token}`
            resolve(service(config))
          })
        })
      }

      config._retry = true
      isRefreshing = true

      try {
        const refreshToken = getRefreshToken()
        if (!refreshToken) throw new Error('No refresh token')

        const res = await axios.post(
          `${import.meta.env.VITE_APP_BASE_API}/api/user/auth/refresh-token`,
          null,
          { params: { refreshToken } }
        )

        const data = res.data.data || res.data
        setToken(data.accessToken)
        setRefreshToken(data.refreshToken)

        pendingRequests.forEach(cb => cb(data.accessToken))
        pendingRequests = []

        config.headers.Authorization = `Bearer ${data.accessToken}`
        return service(config)
      } catch {
        if (!reloginShown) {
          reloginShown = true
          ElMessageBox.confirm('登录状态已过期，请重新登录', '系统提示', {
            confirmButtonText: '重新登录',
            cancelButtonText: '取消',
            type: 'warning'
          })
            .then(() => {
              removeToken()
              location.href = '/login'
            })
            .catch(() => {})
            .finally(() => {
              reloginShown = false
            })
        }
        return Promise.reject(new Error('登录已过期，请重新登录'))
      } finally {
        isRefreshing = false
      }
    }

    const statusMessages: Record<number, string> = {
      400: '请求参数错误',
      403: '没有权限访问',
      404: '请求资源不存在',
      500: '服务器内部错误',
      502: '网关错误',
      503: '服务不可用',
    }

    const message = statusMessages[response.status]
      || `系统接口${String(response.status)}异常`
    ElMessage.error(message)

    return Promise.reject(error)
  }
)

declare module 'axios' {
  interface AxiosRequestConfig {
    _retry?: boolean
    noToken?: boolean
  }
}

export function get<T>(url: string, params?: Record<string, unknown>, config?: AxiosRequestConfig): Promise<T> {
  return service.get(url, { params, ...config })
}

export function post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
  return service.post(url, data, config)
}

export function put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
  return service.put(url, data, config)
}

export function del<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  return service.delete(url, config)
}

export function download(url: string, params: Record<string, unknown>, filename: string) {
  const loadingInstance = ElLoading.service({
    text: '正在下载数据，请稍候',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  return service
    .post(url, params, {
      responseType: 'blob',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
    .then(async (data: unknown) => {
      const blob = data as Blob
      if (blob.type && blob.type.includes('application/json')) {
        const text = await blob.text()
        try {
          const error = JSON.parse(text)
          ElMessage.error(error.message || '下载失败')
        } catch {
          ElMessage.error('下载文件出现错误')
        }
      } else {
        const blobObj = new Blob([blob])
        saveAs(blobObj, filename)
      }
    })
    .catch(() => {
      ElMessage.error('下载文件出现错误，请联系管理员')
    })
    .finally(() => {
      loadingInstance.close()
    })
}

export default service
