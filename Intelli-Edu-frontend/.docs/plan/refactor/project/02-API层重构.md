# 02 - API 层重构

## 一、当前问题

### 1.1 重复文件

| 旧版文件（需删除） | 新版文件（保留） | 重复内容 |
|---|---|---|
| `api/user/login.ts` | `api/user/auth.ts` | login, logout, register, sendLoginCode, refreshToken, sendRegisterCode |
| `api/course/chapters.ts` | `api/course/chapter.ts` | addChapter, updateChapter, deleteChapter, reorderChapters |

### 1.2 风格不一致

| 风格 | 示例文件 | 调用方式 | 路径前缀 |
|------|---------|---------|---------|
| 旧式 | `api/user/login.ts` | `request({ url: '/user/auth/login', method: 'post', data })` | 无 `/api` 前缀 |
| 旧式 | `api/course/chapters.ts` | `request({ url: '/course/inner/...', method: 'GET' })` | 无 `/api` 前缀 |
| 新式 | `api/user/auth.ts` | `request.post('/api/user/auth/login', data)` | 有 `/api` 前缀 |
| 自动生成 | `api/ai/*.ts` | `request<API.XXX>({ url: '/ai/...', method: 'POST', data })` | 无 `/api` 前缀 |

### 1.3 AI 模块问题

| 文件 | 问题 |
|------|------|
| `api/ai/chatListController.ts` | `import { url } from "inspector"` 引入 Node.js 模块，浏览器端报错 |
| `api/ai/typings.d.ts` | 使用 `declare namespace API` 全局声明，与项目 ES Module 风格不一致 |
| `api/ai/aiCourseController.ts` | `doChatByStream` 用 GET 请求发送聊天数据，应该用 POST |
| `api/ai/fileUploadController.ts` | `body: {}` 无类型定义 |
| `api/ai/index.ts` | `@ts-ignore` + `eslint-disable` |

### 1.4 System 模块问题

| 文件 | 问题 |
|------|------|
| `api/system/user.ts` | 与 `api/user/user.ts` 功能重叠；`deptTreeSelect` 与 `role.ts` 中同名函数重复 |
| `api/system/role.ts` | 所有参数类型为 `any`，无返回类型 |
| `api/system/dept.ts` | 同上 |
| `api/system/menu.ts` | 同上 |
| `api/system/notice.ts` | 同上 |

### 1.5 类型问题

| 文件 | 问题 |
|------|------|
| `api/user/types.ts` | `registerType` 用中文字面量；`sex`/`status` 用中文；`loginType` 字符串 vs `number` 不一致 |
| `api/course/types.ts` | `SectionResourceVO` 用 `[key: string]: any`；`isPublic`/`isFree` 用 `0 | 1` |
| `api/exam/types.ts` | `PaperVO.sections` 是 `any[]`；魔法数字 `0 | 1 | 2 | 3 | 4` |
| `api/resource/types.ts` | `uploadStatus` 混用数字和字符串类型 |

### 1.6 `api/index.ts` 聚合文件问题

- `@ts-ignore` + `eslint-disable`
- 命名不一致（`coResourceController` vs `CourseController`）
- 缺少 AI、system、exam、knowledge 模块导出
- 无类型定义

---

## 二、重构目标

```
src/api/
├── request.ts              # 🔄 从 utils/request.ts 迁移（详见 03-请求层重构）
├── user/
│   ├── index.ts            # 导出
│   ├── auth.ts             # ✅ 保留，补充类型
│   ├── user.ts             # ✅ 保留，补充类型
│   └── types.ts            # 🔄 消除中文字面量，统一枚举
├── course/
│   ├── index.ts            # 导出
│   ├── course.ts           # ✅ 保留
│   ├── chapter.ts          # ✅ 保留
│   ├── section.ts          # ✅ 保留
│   ├── class.ts            # ✅ 保留
│   └── types.ts            # 🔄 消除 any，统一枚举
├── exam/
│   ├── index.ts            # 导出
│   ├── exam.ts             # 🔄 重命名 index.ts -> exam.ts，拆分大文件
│   └── types.ts            # 🔄 消除 any，统一枚举
├── knowledge/
│   ├── index.ts            # 导出
│   ├── point.ts            # ✅ 保留
│   └── types.ts            # ✅ 保留
├── resource/
│   ├── index.ts            # 导出
│   ├── resource.ts         # ✅ 保留
│   └── types.ts            # 🔄 统一类型
├── ai/
│   ├── index.ts            # 🔄 重写导出
│   ├── chat.ts             # 🆕 重写 chatListController.ts
│   ├── course.ts           # 🆕 重写 aiCourseController.ts
│   ├── upload.ts           # 🆕 重写 fileUploadController.ts
│   └── types.ts            # 🆕 替换 typings.d.ts
├── system/
│   ├── index.ts            # 🆕 新增导出
│   ├── user.ts             # 🔄 补充类型
│   ├── role.ts             # 🔄 补充类型
│   ├── dept.ts             # 🔄 补充类型
│   ├── menu.ts             # 🔄 补充类型
│   ├── notice.ts           # 🔄 补充类型
│   └── types.ts            # 🆕 新增类型定义
└── index.ts                # 🔄 重写聚合导出
```

---

## 三、详细重构方案

### 3.1 删除重复文件

| 删除文件 | 原因 |
|---------|------|
| `api/user/login.ts` | 被 `auth.ts` 完全替代 |
| `api/course/chapters.ts` | 被 `chapter.ts` 完全替代 |
| `api/course/inner.ts` | 迁移到 `course.ts` 中（见下文） |

### 3.2 迁移 `inner.ts` 到 `course.ts`

`inner.ts` 中的 API 需要保留，但应该迁移到新风格：

```typescript
// 合并到 src/api/course/course.ts 中

/** 获取课程简要信息 */
export function getCourseBrief(courseId: number) {
  return request.get<any, CourseVO>(`/api/course/courses/${courseId}/brief`)
}

/** 批量获取课程简要信息 */
export function getBatchCourseBrief(data: number[]) {
  return request.post<any, CourseVO[]>('/api/course/courses/batch', data)
}

/** 校验学生是否在某班级中 */
export function checkMemberInClass(classId: number) {
  return request.get<boolean>(`/api/course/classes/${classId}/check-member`)
}
```

> **注意**：API 路径需要与后端确认。如果后端使用 `/api` 前缀，则路径需添加前缀；如果后端在网关层统一添加前缀，则保持原样。本方案假设后端已统一 `/api` 前缀。

### 3.3 重写 AI 模块

#### `api/ai/types.ts`

```typescript
export interface ChatMessage {
  id: number
  conversationId: string
  content: string
  role: 'user' | 'assistant' | 'system'
  tokens: number
  createTime: string
  updateTime: string
}

export interface ChatListVO {
  id: number
  userId: string
  conversationId: string
  conversationTitle: string
  createTime: string
  updateTime: string
  chatMessages: ChatMessage[]
}

export interface ChatListAddRequest {
  userId?: string
  conversationId?: string
  conversationTitle?: string
}

export interface ChatListUpdateRequest {
  id: number
  userId?: string
  conversationId?: string
  conversationTitle?: string
}

export interface ChatStreamParams {
  userPrompt: string
  chatId: string
}

export interface UploadChatParams {
  userPrompt: string
  chatId: string
}
```

#### `api/ai/chat.ts`

```typescript
import request from '@/utils/request'
import type { ChatListVO, ChatListAddRequest, ChatListUpdateRequest } from './types'

export function createChatList(data: ChatListAddRequest) {
  return request.post<boolean>('/api/ai/chatList/createChatList', data)
}

export function getChatListByConversationId(conversationId: string) {
  return request.get<ChatListVO>('/api/ai/chatList/getChatListByConversationId', {
    params: { conversationId }
  })
}

export function getUserChatList(userId: string) {
  return request.get<ChatListVO[]>('/api/ai/chatList/getUserChatList', {
    params: { userId }
  })
}

export function updateChatList(data: ChatListUpdateRequest) {
  return request.post<boolean>('/api/ai/chatList/updateChatList', data)
}
```

#### `api/ai/course.ts`

```typescript
import request from '@/utils/request'
import type { ChatStreamParams, UploadChatParams } from './types'

export function doChatByStream(params: ChatStreamParams) {
  return request.post<string[]>('/api/ai/aiCourse/doChatByStream', null, { params })
}

export function doChatByStreamMono(params: ChatStreamParams) {
  return request.post<string[]>('/api/ai/aiCourse/doChatByStreamMono', null, { params })
}

export function doChatWithRagByStream(params: ChatStreamParams) {
  return request.post<string[]>('/api/ai/aiCourse/doChatWithRagByStream', null, { params })
}

export function doChatWithToolAndRag(params: ChatStreamParams) {
  return request.post<string[]>('/api/ai/aiCourse/doChatWithToolAndRagByStream', null, { params })
}

export function uploadChat(params: UploadChatParams, data: FormData) {
  return request.post<string[]>('/api/ai/aiCourse/uploadChat', data, {
    headers: { 'Content-Type': 'multipart/form-data' },
    params
  })
}
```

#### `api/ai/upload.ts`

```typescript
import request from '@/utils/request'

export function uploadFile(data: FormData) {
  return request.post<string>('/api/ai/upload', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
```

#### `api/ai/index.ts`

```typescript
export * from './types'
export * from './chat'
export * from './course'
export * from './upload'
```

### 3.4 补充 System 模块类型

#### `api/system/types.ts` - 🆕

```typescript
export interface SystemUser {
  userId: number
  deptId: number
  userName: string
  nickName: string
  email: string
  phonenumber: string
  sex: string
  status: string
  createTime: string
  dept: { deptId: number; deptName: string }
  roles: Array<{ roleId: number; roleName: string; roleKey: string }>
}

export interface SystemRole {
  roleId: number
  roleName: string
  roleKey: string
  roleSort: number
  dataScope: string
  menuCheckStrictly: boolean
  deptCheckStrictly: boolean
  status: string
  createTime: string
}

export interface SystemDept {
  deptId: number
  parentId: number
  ancestors: string
  deptName: string
  orderNum: number
  leader: string
  status: string
  createTime: string
  children: SystemDept[]
}

export interface SystemMenu {
  menuId: number
  menuName: string
  parentId: number
  orderNum: number
  path: string
  component: string
  isFrame: number
  isCache: number
  menuType: string
  visible: string
  status: string
  perms: string
  icon: string
  createTime: string
  children: SystemMenu[]
}

export interface SystemNotice {
  noticeId: number
  noticeTitle: string
  noticeType: string
  noticeContent: string
  status: string
  createBy: string
  createTime: string
}
```

### 3.5 重写 `api/index.ts` 聚合文件

```typescript
// src/api/index.ts
export * as userApi from './user'
export * as courseApi from './course'
export * as examApi from './exam'
export * as knowledgeApi from './knowledge'
export * as resourceApi from './resource'
export * as aiApi from './ai'
export * as systemApi from './system'
```

> **注意**：旧版 `api/index.ts` 使用 `import * as X from './module'` + `export default { X }` 的模式，新版改为命名导出，更符合 Tree Shaking 要求。如果旧版 Store 依赖 `api.index.ts` 的 default export，需要在迁移 Store 时同步更新。

---

## 四、迁移步骤

### Step 1：删除重复文件

```
删除: src/api/user/login.ts
删除: src/api/course/chapters.ts
```

### Step 2：迁移 inner.ts 到 course.ts

1. 将 `inner.ts` 中的函数迁移到 `course.ts`，改用新式调用风格
2. 删除 `src/api/course/inner.ts`
3. 更新 `course/index.ts` 导出（移除 `inner` 的导出，因为已合并到 `course.ts`）

### Step 3：重写 AI 模块

1. 创建 `api/ai/types.ts`
2. 创建 `api/ai/chat.ts`
3. 创建 `api/ai/course.ts`
4. 创建 `api/ai/upload.ts`
5. 重写 `api/ai/index.ts`
6. 删除 `api/ai/chatListController.ts`
7. 删除 `api/ai/aiCourseController.ts`
8. 删除 `api/ai/fileUploadController.ts`
9. 删除 `api/ai/typings.d.ts`

### Step 4：补充 System 模块类型

1. 创建 `api/system/types.ts`
2. 更新 `api/system/*.ts` 添加类型注解
3. 创建 `api/system/index.ts`

### Step 5：重写 `api/index.ts`

### Step 6：更新所有引用

搜索项目中所有 `import` 旧版 API 的地方，更新为新版导入路径。

### Step 7：验证

- [ ] 登录/注册 API 正常
- [ ] 课程 CRUD API 正常
- [ ] AI 聊天 API 正常
- [ ] 系统管理 API 正常
