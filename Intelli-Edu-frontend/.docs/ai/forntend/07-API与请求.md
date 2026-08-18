# API 接口与请求封装

## Axios 封装

**文件**：`src/utils/request.js`

### 基础配置

```javascript
const service = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_API,  // /api
  timeout: 10000,
  transformResponse: [
    function (data) {
      try {
        return JSONbigString.parse(data);  // 大整数处理
      } catch (err) {
        return data;
      }
    }
  ]
})
```

### 请求拦截器

- 自动添加 `Authorization: Bearer <token>` 请求头
- 自动设置 `Content-Type`
- 支持 FormData 上传

### 响应处理

当前响应拦截器被注释掉了，需要各业务自行处理响应状态码。

### 下载方法

```javascript
import { download } from '@/utils/request'

// 通用下载
download(url, params, filename)
```

## API 模块结构

### 手写 API（src/api/）

当前项目主要使用手写的 API 层：

```
src/api/
├── index.ts              # API 统一出口
├── ai/                   # AI 服务接口
│   ├── index.ts
│   ├── aiCourseController.ts
│   ├── chatListController.ts
│   ├── fileUploadController.ts
│   └── typings.d.ts      # AI 模块类型定义
├── course/               # 课程服务接口
│   ├── chapters.ts       # 章节管理
│   ├── class.ts          # 班级管理
│   ├── course.ts         # 课程管理
│   ├── inner.ts          # 内部接口
│   └── section.ts        # 小节管理
├── resource/             # 资源服务接口
│   └── resource.ts
└── user/                 # 用户服务接口
    ├── login.ts          # 登录认证
    └── user.ts           # 用户管理
```

### API 统一出口

**文件**：`src/api/index.ts`

```typescript
import * as userController from "./user/user";
import * as authController from "./user/login";
import * as coResourceController from "./resource/resource";
import * as CourseController from "./course/course";
import * as ChapterController from "./course/chapters";
import * as SectionController from "./course/section";
import * as InnerController from "./course/inner";
import * as ClassController from "./course/class";

export default {
  authController,
  userController,
  coResourceController,
  CourseController,
  ChapterController,
  SectionController,
  InnerController,
  ClassController
};
```

**注意**：`src/api/ai/index.ts` 中定义了 AI 模块的导出（`fileUploadController`、`chatListController`、`aiCourseController`），但 `src/api/index.ts` 主入口暂未将其导出。如需使用 AI 接口，可直接从 `src/api/ai` 导入。

### 使用示例

```javascript
import api from '@/api/index.js'

const { CourseController, userController, authController } = api

// 获取课程列表
const res = await CourseController.getCourseList(params)

// 用户登录
const res = await authController.login({ username, password })

// 获取用户信息
const res = await userController.getUserInfo(userId)
```

## OpenAPI 自动生成

**配置文件**：`openapi.config.js`

项目配置了通过 `@umijs/openapi` 从后端 Swagger 文档自动生成 TypeScript API 代码：

```javascript
// 用户服务
generateService({
  schemaPath: 'http://localhost:8890/api/user/v3/api-docs/default',
  serversPath: './src/services/user',
  apiPrefix: "'/user'",
})

// 资源服务
generateService({
  schemaPath: 'http://localhost:8890/api/resource/v3/api-docs/default',
  serversPath: './src/services/resource',
  apiPrefix: "'/resource'",
})

// AI 服务
generateService({
  schemaPath: 'http://localhost:8890/api/ai/v3/api-docs/default',
  serversPath: './src/services/ai',
  apiPrefix: "'/ai'",
})
```

### 生成命令

```bash
npm run openapi
```

**注意**：当前 `src/services/` 目录不存在，说明尚未执行过代码生成，或生成的代码被移除了。

## 后端服务代理

开发环境通过 Vite 代理连接多个微服务：

| 代理路径 | 目标服务 | 端口 |
|---------|---------|------|
| `/api/ai` | AI 服务 | 8896 |
| `/api/user/users` | 用户服务 | 8891 |
| `/api/user/auth` | 认证服务 | 8891 |
| `/api/resource/resources` | 资源服务 | 8892 |
| `/api/course` | 课程服务 | 8893 |

## 认证流程

1. 用户调用 `authController.login()` 登录（接口：`POST /user/auth/login`）
2. 后端返回 `accessToken`
3. 前端将 Token 存入 Cookie（`Admin-Token`）
4. 将 `userId` 存入 localStorage
5. 自动调用 `getInfo()` 和 `getLoginUser()` 获取用户信息
6. 后续请求自动携带 `Authorization: Bearer <token>`

**登录接口额外功能**：
- 支持 `withCredentials: true`
- 支持验证码登录（`sendLoginCode`）
- 支持注册（`register`）
- 支持 Token 刷新（`refreshToken`）

## 请求工具函数

**文件**：`src/utils/request.js`

| 导出 | 说明 |
|------|------|
| `service` | Axios 实例（默认导出） |
| `download(url, params, filename)` | 文件下载 |
| `isRelogin` | 重新登录标志 |

## 认证工具

**文件**：`src/utils/auth.js`

| 函数 | 说明 |
|------|------|
| `getToken()` | 从 Cookie 获取 Token |
| `setToken(token)` | 设置 Token 到 Cookie |
| `removeToken()` | 移除 Cookie 中的 Token |
