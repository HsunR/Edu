# 05 - Composables 层重构

## 一、当前问题

项目中没有 `composables` 目录，大量可复用逻辑分散在组件中：

| 逻辑 | 当前位置 | 重复次数 |
|------|---------|---------|
| 验证码倒计时 | `loginNew.vue` | 1 |
| 加载状态管理 | 各组件 `ref(false)` | 20+ |
| 分页查询 | 各页面组件 | 10+ |
| 文件上传 | `FileUpload/index.vue` + `ImageUpload/index.vue` | 2 |
| 权限判断 | `directive/permission/` + `plugins/auth.js` + `utils/permission.js` | 3 |
| 消息提示 | 各组件 `ElMessage.xxx()` | 50+ |
| 复制文本 | `directive/common/copyText.js` | 1 |

---

## 二、重构目标

```
src/composables/
├── index.ts              # 统一导出
├── useLoading.ts         # 加载状态管理
├── useTable.ts           # 分页查询
├── useCountdown.ts       # 验证码倒计时
├── useUpload.ts          # 文件上传
├── usePermission.ts      # 权限判断
├── useMessage.ts         # 消息提示封装
└── useClipboard.ts       # 剪贴板操作
```

---

## 三、详细设计

### 3.1 `useLoading.ts`

```typescript
import { ref, type Ref } from 'vue'

export function useLoading(initial = false) {
  const loading: Ref<boolean> = ref(initial)

  const startLoading = () => { loading.value = true }
  const stopLoading = () => { loading.value = false }

  async function withLoading<T>(fn: () => Promise<T>): Promise<T> {
    try {
      startLoading()
      return await fn()
    } finally {
      stopLoading()
    }
  }

  return { loading, startLoading, stopLoading, withLoading }
}
```

**使用示例**：
```typescript
// 旧版
const loading = ref(false)
const submit = async () => {
  loading.value = true
  try {
    await someApi()
  } finally {
    loading.value = false
  }
}

// 新版
const { loading, withLoading } = useLoading()
const submit = () => withLoading(() => someApi())
```

### 3.2 `useTable.ts`

```typescript
import { ref, reactive, type Ref } from 'vue'
import type { PageRequest, PageResult } from '@/types/api'

interface UseTableOptions<T, Q extends PageRequest> {
  fetchFn: (params: Q) => Promise<PageResult<T>>
  defaultParams?: Partial<Q>
  defaultPageSize?: number
  immediate?: boolean
}

export function useTable<T, Q extends PageRequest = PageRequest>(
  options: UseTableOptions<T, Q>
) {
  const { fetchFn, defaultParams = {}, defaultPageSize = 10, immediate = false } = options

  const loading = ref(false)
  const data: Ref<T[]> = ref([])
  const total = ref(0)

  const params = reactive<Q>({
    current: 1,
    pageSize: defaultPageSize,
    ...defaultParams,
  } as Q)

  async function fetchData() {
    loading.value = true
    try {
      const result = await fetchFn(params)
      data.value = result.records
      total.value = result.total
    } finally {
      loading.value = false
    }
  }

  function reset() {
    params.current = 1
    Object.assign(params, { ...defaultParams, current: 1, pageSize: defaultPageSize })
    fetchData()
  }

  function handlePageChange(page: number) {
    params.current = page
    fetchData()
  }

  function handleSizeChange(size: number) {
    params.pageSize = size
    params.current = 1
    fetchData()
  }

  if (immediate) {
    fetchData()
  }

  return {
    loading,
    data,
    total,
    params,
    fetchData,
    reset,
    handlePageChange,
    handleSizeChange,
  }
}
```

**使用示例**：
```typescript
const { loading, data, total, params, fetchData, reset, handlePageChange } = useTable({
  fetchFn: getCourseList,
  defaultParams: { courseName: '' },
  immediate: true,
})
```

### 3.3 `useCountdown.ts`

```typescript
import { ref, computed, onUnmounted } from 'vue'

export function useCountdown(defaultSeconds = 60) {
  const remaining = ref(0)
  const isCounting = ref(false)
  let timer: ReturnType<typeof setInterval> | null = null

  function start(seconds = defaultSeconds) {
    remaining.value = seconds
    isCounting.value = true

    timer = setInterval(() => {
      remaining.value--
      if (remaining.value <= 0) {
        stop()
      }
    }, 1000)
  }

  function stop() {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
    remaining.value = 0
    isCounting.value = false
  }

  const buttonText = computed(() =>
    isCounting.value ? `${remaining.value}秒后重新获取` : '获取验证码'
  )

  onUnmounted(stop)

  return { remaining, isCounting, buttonText, start, stop }
}
```

**使用示例**：
```typescript
// 旧版（loginNew.vue 中的 startCountdown + codeButtonDisabled + codeButtonText + countdown）
const { isCounting, buttonText, start } = useCountdown(60)

const sendCode = async () => {
  await sendLoginCode({ ... })
  start()
}
```

### 3.4 `useUpload.ts`

```typescript
import { ref, computed } from 'vue'
import type { UploadUserFile } from 'element-plus'
import { ElMessage } from 'element-plus'
import { getToken } from '@/utils/auth'

interface UploadOptions {
  accept?: string
  maxSize?: number
  maxCount?: number
}

export function useUpload(options: UploadOptions = {}) {
  const { accept = '', maxSize = 5 * 1024 * 1024, maxCount = 5 } = options

  const fileList = ref<UploadUserFile[]>([])

  const headers = computed(() => ({
    Authorization: `Bearer ${getToken()}`
  }))

  function beforeUpload(file: File) {
    if (maxSize && file.size > maxSize) {
      ElMessage.error(`文件大小不能超过 ${maxSize / 1024 / 1024}MB`)
      return false
    }
    return true
  }

  function handleExceed() {
    ElMessage.error(`最多上传 ${maxCount} 个文件`)
  }

  function handleSuccess(response: any) {
    if (response.code === 200) {
      ElMessage.success('上传成功')
    } else {
      ElMessage.error(response.msg || '上传失败')
    }
  }

  function handleError() {
    ElMessage.error('上传失败')
  }

  return {
    fileList,
    headers,
    beforeUpload,
    handleExceed,
    handleSuccess,
    handleError,
  }
}
```

### 3.5 `usePermission.ts`

```typescript
import { useUserStore } from '@/stores'

export function usePermission() {
  const userStore = useUserStore()

  function hasRole(role: string | string[]): boolean {
    const roles = Array.isArray(role) ? role : [role]
    return roles.includes(userStore.userInfo?.type || '')
  }

  function hasPermission(permission: string | string[]): boolean {
    // 当前项目权限系统未完全实现，预留接口
    return true
  }

  function hasRoleOr(roles: string[]): boolean {
    return roles.some(role => hasRole(role))
  }

  function hasPermissionOr(permissions: string[]): boolean {
    return permissions.some(p => hasPermission(p))
  }

  return { hasRole, hasPermission, hasRoleOr, hasPermissionOr }
}
```

**使用示例**：
```typescript
// 替代旧版 directive/permission/hasRole.js 和 plugins/auth.js
const { hasRole } = usePermission()
if (hasRole('Teacher')) { ... }
```

### 3.6 `useMessage.ts`

```typescript
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'

export function useMessage() {
  function success(message: string) {
    ElMessage.success(message)
  }

  function error(message: string) {
    ElMessage.error(message)
  }

  function warning(message: string) {
    ElMessage.warning(message)
  }

  function info(message: string) {
    ElMessage.info(message)
  }

  function confirm(message: string, title = '提示') {
    return ElMessageBox.confirm(message, title, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
  }

  function notify(message: string, type: 'success' | 'warning' | 'info' | 'error' = 'info') {
    ElNotification({ message, type })
  }

  return { success, error, warning, info, confirm, notify }
}
```

### 3.7 `useClipboard.ts`

```typescript
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

export function useClipboard() {
  const copied = ref(false)

  async function copy(text: string) {
    try {
      await navigator.clipboard.writeText(text)
      copied.value = true
      ElMessage.success('复制成功')
      setTimeout(() => { copied.value = false }, 2000)
    } catch {
      ElMessage.error('复制失败')
    }
  }

  return { copied, copy }
}
```

### 3.8 `composables/index.ts`

```typescript
export { useLoading } from './useLoading'
export { useTable } from './useTable'
export { useCountdown } from './useCountdown'
export { useUpload } from './useUpload'
export { usePermission } from './usePermission'
export { useMessage } from './useMessage'
export { useClipboard } from './useClipboard'
```

---

## 四、迁移步骤

### Step 1：创建 composables 目录和文件

### Step 2：逐步替换组件中的重复逻辑

优先替换：
1. `loginNew.vue` 中的倒计时逻辑 → `useCountdown`
2. 各页面的分页查询 → `useTable`
3. `FileUpload` + `ImageUpload` 的上传逻辑 → `useUpload`
4. `directive/common/copyText.js` → `useClipboard`

### Step 3：删除被替代的旧代码

- `utils/permission.js` → 被 `usePermission` 替代
- `plugins/auth.js` → 被 `usePermission` 替代
- `directive/common/copyText.js` → 被 `useClipboard` 替代

### Step 4：验证

- [ ] 验证码倒计时正常
- [ ] 分页查询正常
- [ ] 文件上传正常
- [ ] 权限判断正常
- [ ] 复制功能正常
