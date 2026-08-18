# 前端 AI 开发指南 - API 对接规范

## 1. 统一响应格式

### 1.1 响应结构

所有 API 返回统一格式（由后端 `BaseResponse<T>` 包装）：

```typescript
interface BaseResponse<T> {
  code: number;      // 业务状态码，0 表示成功
  data: T;           // 响应数据
  message: string;   // 提示信息
  requestId: string; // 请求唯一 ID，用于排查问题
}
```

### 1.2 成功响应示例

```json
{
  "code": 0,
  "data": {
    "userId": 123456789,
    "name": "张三",
    "userType": 1
  },
  "message": "ok",
  "requestId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

### 1.3 错误响应示例

```json
{
  "code": 40000,
  "data": null,
  "message": "参数错误：用户名不能为空",
  "requestId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

## 2. 错误码体系

### 2.1 错误码枚举

| 错误码 | 含义 | 前端处理方式 |
|--------|------|-------------|
| 0 | 成功 | 正常处理数据 |
| 40000 | 参数错误 | 表单校验失败，显示具体错误信息 |
| 40100 | 未登录 | 清除 Token，跳转登录页 |
| 40101 | 无权限 | 提示"无权访问"，可隐藏按钮 |
| 40400 | 资源不存在 | 显示 404 页面或空状态 |
| 50000 | 系统内部错误 | 提示"系统繁忙，请稍后重试" |
| 50001 | 操作失败 | 通用操作失败提示 |

### 2.2 HTTP 状态码与业务码关系

- 后端统一返回 HTTP 200，业务错误通过 `code` 字段区分
- 仅当网关熔断、服务不可用时可能返回 HTTP 502/503/504

## 3. 认证流程

### 3.1 JWT Token 机制

```
┌──────────┐                    ┌──────────┐
│  前端     │ ── POST /auth/login ──> │  网关    │
│          │ <── { accessToken, refreshToken } │
│          │                    └──────────┘
│          │  后续请求携带 Header:
│          │  Authorization: Bearer <accessToken>
└──────────┘
```

### 3.2 Token 存储

```typescript
// 使用 localStorage（推荐）
localStorage.setItem('accessToken', loginResult.accessToken)
localStorage.setItem('refreshToken', loginResult.refreshToken)

// 或使用 pinia + localStorage 组合
```

### 3.3 请求头规范

| Header | 说明 | 必填 |
|--------|------|------|
| `Authorization` | `Bearer <accessToken>` | 除白名单接口外必填 |
| `Content-Type` | `application/json` | POST/PUT 必填 |

**白名单接口**（无需 Token）：
- `POST /api/user/auth/login`
- `POST /api/user/auth/register`
- `POST /api/user/auth/register/send-code`
- `POST /api/user/auth/login/send-code`
- `POST /api/user/auth/refresh-token`
- `GET /api/course/courses` （浏览公开课程）
- `GET /api/course/courses/{courseId}` （课程详情）

### 3.4 Token 刷新

```typescript
// Axios 响应拦截器中处理 40100
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config
    if (error.response?.data?.code === 40100 && !originalRequest._retry) {
      originalRequest._retry = true
      try {
        const refreshToken = localStorage.getItem('refreshToken')
        const res = await axios.post('/api/user/auth/refresh-token', {
          refreshToken
        })
        const { accessToken } = res.data.data
        localStorage.setItem('accessToken', accessToken)
        originalRequest.headers['Authorization'] = `Bearer ${accessToken}`
        return axios(originalRequest)
      } catch (refreshError) {
        // 刷新失败，跳转登录
        localStorage.clear()
        window.location.href = '/login'
        return Promise.reject(refreshError)
      }
    }
    return Promise.reject(error)
  }
)
```

## 4. Axios 封装规范

### 4.1 完整封装示例

```typescript
// utils/request.ts
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const request: AxiosInstance = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  (response: AxiosResponse<BaseResponse<any>>) => {
    const { code, message, data } = response.data
    if (code === 0) {
      return data  // 直接返回 data，简化调用
    }
    // 业务错误
    ElMessage.error(message || '请求失败')
    return Promise.reject(new Error(message))
  },
  (error) => {
    const { response } = error
    if (response?.data) {
      const { code, message } = response.data
      if (code === 40100) {
        ElMessage.error('登录已过期，请重新登录')
        // 触发重新登录逻辑
      } else if (code === 40101) {
        ElMessage.error('无权访问')
      } else {
        ElMessage.error(message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    return Promise.reject(error)
  }
)

export default request
```

### 4.2 API 模块封装示例

```typescript
// api/user.ts
import request from '@/utils/request'

export const userApi = {
  // 登录
  login: (data: LoginRequest) =>
    request.post<LoginResult>('/user/auth/login', data),

  // 获取当前用户
  getCurrentUser: () =>
    request.get<UserDetailVO>('/user/users/me'),

  // 更新用户信息
  updateUser: (data: UserUpdateRequest) =>
    request.put('/user/users/me', data),

  // 浏览公开课程（无需登录）
  listPublicCourses: (params: CourseQueryRequest) =>
    request.get<PageResult<CourseVO>>('/course/courses', { params })
}
```

## 5. 分页规范

### 5.1 分页请求参数

```typescript
interface PageRequest {
  current: number   // 当前页码，从 1 开始
  pageSize: number  // 每页大小，默认 10
}

// 带过滤条件的分页请求
interface CourseQueryRequest extends PageRequest {
  keyword?: string      // 关键词搜索
  categoryId?: number   // 分类过滤
  status?: number       // 状态过滤
}
```

### 5.2 分页响应结构

后端使用 MyBatis-Plus 分页，返回结构：

```typescript
interface PageResult<T> {
  records: T[]       // 数据列表
  total: number      // 总记录数
  size: number       // 每页大小
  current: number    // 当前页码
  pages: number      // 总页数
}
```

### 5.3 Element Plus 分页组件对接

```vue
<template>
  <el-table :data="courseList">
    <!-- 列定义 -->
  </el-table>
  <el-pagination
    v-model:current-page="queryParams.current"
    v-model:page-size="queryParams.pageSize"
    :total="total"
    :page-sizes="[10, 20, 50]"
    layout="total, sizes, prev, pager, next"
    @change="fetchData"
  />
</template>

<script setup lang="ts">
const queryParams = reactive<CourseQueryRequest>({
  current: 1,
  pageSize: 10,
  keyword: ''
})

const courseList = ref<CourseVO[]>([])
const total = ref(0)

const fetchData = async () => {
  const res = await userApi.listPublicCourses(queryParams)
  courseList.value = res.records
  total.value = res.total
}
</script>
```

## 6. 文件上传规范

### 6.1 预签名上传流程

本项目使用腾讯云 COS/VOD 存储，前端上传流程：

```
1. 前端 ──POST /api/resource/resources/presign/image──> 后端
2. 前端 <──{ presignedUrl, resourceId }── 后端
3. 前端 ──PUT 文件到 presignedUrl──> 腾讯云 COS
4. 前端 ──POST /api/resource/resources/confirm──> 后端（确认上传完成）
```

### 6.2 上传封装示例

```typescript
// api/resource.ts
export const resourceApi = {
  // 获取预签名 URL
  presignImage: (data: PresignRequest) =>
    request.post<PresignedUrlVO>('/resource/resources/presign/image', data),

  // 直接上传到 COS（不走 Axios 拦截器）
  uploadToCos: (url: string, file: File) =>
    fetch(url, {
      method: 'PUT',
      body: file,
      headers: { 'Content-Type': file.type }
    }),

  // 确认上传完成
  confirmUpload: (data: UploadConfirmRequest) =>
    request.post<ResourceVO>('/resource/resources/confirm', data)
}
```

## 7. SSE 流式响应（AI 聊天）

AI 服务使用 SSE (Server-Sent Events) 返回流式数据：

```typescript
// api/ai.ts
export const aiApi = {
  // 流式聊天
  chatStream: (data: ChatRequest) => {
    return new EventSource('/api/ai/aiCourse/doChatByStream', {
      // 注意：EventSource 不支持自定义 Header，需通过 URL 传参
      // 或使用 fetch + ReadableStream
    })
  }
}

// 推荐使用 fetch + ReadableStream
export async function* chatStreamGenerator(data: ChatRequest) {
  const response = await fetch('/api/ai/aiCourse/doChatByStream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
    },
    body: JSON.stringify(data)
  })

  const reader = response.body?.getReader()
  const decoder = new TextDecoder()

  while (true) {
    const { done, value } = await reader!.read()
    if (done) break
    yield decoder.decode(value, { stream: true })
  }
}
```

## 8. 路由与权限

### 8.1 前端路由结构建议

```typescript
const routes = [
  {
    path: '/',
    component: Layout,
    children: [
      // 公共页面
      { path: '', component: Home },
      { path: 'courses', component: CourseList },      // 公开课程列表
      { path: 'courses/:id', component: CourseDetail }, // 课程详情

      // 需要登录
      { path: 'my-courses', component: MyCourseList, meta: { requireAuth: true } },
      { path: 'profile', component: UserProfile, meta: { requireAuth: true } },

      // 学生端
      { path: 'exams', component: ExamList, meta: { requireAuth: true, roles: [1] } },
      { path: 'exams/:id/take', component: TakeExam, meta: { requireAuth: true, roles: [1] } },

      // 教师端
      { path: 'teacher/courses', component: TeacherCourseList, meta: { requireAuth: true, roles: [2, 3] } },
      { path: 'teacher/exams', component: TeacherExamList, meta: { requireAuth: true, roles: [2, 3] } },

      // AI 辅导
      { path: 'ai-chat', component: AIChat, meta: { requireAuth: true } }
    ]
  },
  { path: '/login', component: Login },
  { path: '/register', component: Register }
]
```

### 8.2 路由守卫

```typescript
// router/guard.ts
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = localStorage.getItem('accessToken')

  if (to.meta.requireAuth && !token) {
    next('/login?redirect=' + to.path)
    return
  }

  if (to.meta.roles && !to.meta.roles.includes(userStore.userType)) {
    next('/403')
    return
  }

  next()
})
```
