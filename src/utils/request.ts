import axios, { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage, ElMessageBox, ElLoading } from 'element-plus'
import { getToken, getRefreshToken, setToken, setRefreshToken, removeToken } from '@/utils/auth'
import { tansParams, blobValidate } from '@/utils/ruoyi'
import { saveAs } from 'file-saver'
import JSONbig from 'json-bigint'
import errorCode from '@/utils/errorCode'

const JSONbigString = JSONbig({ storeAsString: true })

export let isRelogin = { show: false }

const service: AxiosInstance = axios.create({
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

let isRefreshing = false
let pendingRequests: Array<(token: string) => void> = []

service.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const isToken = (config.headers || {}).isToken === false

  if (config.data instanceof FormData) {
    config.headers['Content-Type'] = 'multipart/form-data'
  } else if (!config.headers['Content-Type']) {
    config.headers['Content-Type'] = 'application/json'
  }

  const token = getToken()
  if (token && !isToken) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
})

service.interceptors.response.use(
  (response: AxiosResponse) => {
    if (response.request.responseType === 'blob' || response.request.responseType === 'arraybuffer') {
      return response.data
    }
    return response.data
  },
  async (error) => {
    const originalRequest = error.config
    if (error.response?.status === 401 && !originalRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve) => {
          pendingRequests.push((token: string) => {
            originalRequest.headers.Authorization = `Bearer ${token}`
            resolve(service(originalRequest))
          })
        })
      }
      originalRequest._retry = true
      isRefreshing = true
      try {
        const refreshToken = getRefreshToken()
        if (!refreshToken) {
          throw new Error('No refresh token')
        }
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
        originalRequest.headers.Authorization = `Bearer ${data.accessToken}`
        return service(originalRequest)
      } catch {
        if (!isRelogin.show) {
          isRelogin.show = true
          ElMessageBox.confirm('登录状态已过期，请重新登录', '系统提示', {
            confirmButtonText: '重新登录',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            isRelogin.show = false
            removeToken()
            location.href = '/login'
          }).catch(() => {
            isRelogin.show = false
          })
        }
        return Promise.reject(new Error('无效的会话，或者会话已过期，请重新登录。'))
      } finally {
        isRefreshing = false
      }
    }

    let message = error.message
    if (message === 'Network Error') {
      message = '后端接口连接异常'
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时'
    } else if (message.includes('Request failed with status code')) {
      message = `系统接口${message.substr(message.length - 3)}异常`
    }
    ElMessage({ message, type: 'error', duration: 5000 })
    return Promise.reject(error)
  }
)

let downloadLoadingInstance: ReturnType<typeof ElLoading.service>

export function download(url: string, params: any, filename: string, config?: any) {
  downloadLoadingInstance = ElLoading.service({
    text: '正在下载数据，请稍候',
    background: 'rgba(0, 0, 0, 0.7)'
  })
  return service.post(url, params, {
    transformRequest: [(params) => tansParams(params)],
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    responseType: 'blob',
    ...config
  }).then(async (data: any) => {
    const isBlob = blobValidate(data)
    if (isBlob) {
      const blob = new Blob([data])
      saveAs(blob, filename)
    } else {
      const resText = await data.text()
      const rspObj = JSON.parse(resText)
      const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode['default']
      ElMessage.error(errMsg)
    }
    downloadLoadingInstance.close()
  }).catch((r: any) => {
    console.error(r)
    ElMessage.error('下载文件出现错误，请联系管理员！')
    downloadLoadingInstance.close()
  })
}

export default service
