# 01 - Store 层重构

## 一、当前问题

### 1.1 双 Store 体系冲突

项目中存在两套完全独立的 Store：

| 旧版 `src/store/` | 新版 `src/stores/` |
|---|---|
| `store/modules/user.js` - Options API, JS | `stores/user.ts` - Composition API, TS |
| `store/modules/app.js` - Options API, JS | `stores/app.ts` - Composition API, TS |
| `store/modules/permission.js` - Options API, JS | ❌ 无对应新版 |
| `store/modules/settings.js` - Options API, JS | ❌ 无对应新版 |
| `store/modules/tagsView.js` - Options API, JS | ❌ 无对应新版 |

**致命问题**：两个 `user` Store 注册了相同的 Pinia ID `"user"`，会导致状态冲突。

### 1.2 旧版 Store 具体问题

| 文件 | 问题 |
|------|------|
| `store/modules/user.js` | 使用旧版 API `authController.login()`，手动 `localStorage.setItem("userId")`，与 `utils/auth.ts` 的 Token 管理不兼容 |
| `store/modules/app.js` | 功能与 `stores/app.ts` 完全重复 |
| `store/modules/permission.js` | 使用 `defineStore` 但未导入，依赖全局自动导入；`generateRoutes` 返回 Promise 但内部是同步逻辑 |
| `store/modules/settings.js` | 手动读写 `localStorage`，应该用持久化插件 |
| `store/modules/tagsView.js` | 大量 `new Promise(resolve => ...)` 包装同步操作，完全不需要 |

### 1.3 新版 Store 已有但需改进

| 文件 | 需改进 |
|------|--------|
| `stores/user.ts` | ✅ 基本完善，需补充 `fetchUserInfo` 在登录后自动调用 |
| `stores/app.ts` | ✅ 基本完善 |
| `stores/course.ts` | ⚠️ `isCourseOwner` 在 computed 中调用 `useUserStore()`，应改为外部传入 |
| `stores/exam.ts` | ✅ 基本完善，倒计时逻辑合理 |
| `stores/index.ts` | ⚠️ 缺少 `exam`、`permission`、`settings`、`tagsView` 的导出 |

---

## 二、重构目标

```
src/stores/
├── index.ts              # 统一导出所有 Store
├── user.ts               # ✅ 保留，微调
├── app.ts                # ✅ 保留，微调
├── course.ts             # 🔄 微调
├── exam.ts               # ✅ 保留
├── permission.ts         # 🆕 从旧版迁移 + 重写
├── settings.ts           # 🆕 从旧版迁移 + 重写 + 持久化
└── tagsView.ts           # 🆕 从旧版迁移 + 重写
```

---

## 三、详细重构方案

### 3.1 `stores/user.ts` - 微调

**变更点**：
- 登录成功后自动调用 `fetchUserInfo()`
- `logout` 时清除所有相关 localStorage

```typescript
// src/stores/user.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, logout as logoutApi } from '@/api/user/auth'
import { getUserInfo, updateUserInfo as updateUserInfoApi, updateAvatar as updateAvatarApi } from '@/api/user/user'
import { getToken, setToken, removeToken, getRefreshToken, setRefreshToken, removeRefreshToken } from '@/utils/auth'
import type { LoginRequest, UserDetailVO, UserUpdateRequest } from '@/api/user/types'
import { UserType } from '@/types/enums'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const refreshToken = ref(getRefreshToken() || '')
  const userInfo = ref<UserDetailVO | null>(null)

  const userType = computed(() => userInfo.value?.type)
  const isStudent = computed(() => userType.value === UserType.Student)
  const isTeacher = computed(() => userType.value === UserType.Teacher)
  const isAdmin = computed(() => userType.value === UserType.Admin)
  const isLoggedIn = computed(() => !!token.value)

  async function login(data: LoginRequest) {
    const res = await loginApi(data)
    token.value = res.accessToken
    refreshToken.value = res.refreshToken
    setToken(res.accessToken)
    setRefreshToken(res.refreshToken)
    await fetchUserInfo()
  }

  async function fetchUserInfo() {
    userInfo.value = await getUserInfo()
  }

  async function updateUserInfo(data: UserUpdateRequest) {
    await updateUserInfoApi(data)
    await fetchUserInfo()
  }

  async function updateAvatar(avatarUrl: string) {
    await updateAvatarApi(avatarUrl)
    await fetchUserInfo()
  }

  async function logout() {
    try {
      await logoutApi()
    } finally {
      token.value = ''
      refreshToken.value = ''
      userInfo.value = null
      removeToken()
      removeRefreshToken()
    }
  }

  function resetState() {
    token.value = ''
    refreshToken.value = ''
    userInfo.value = null
  }

  return {
    token,
    refreshToken,
    userInfo,
    userType,
    isStudent,
    isTeacher,
    isAdmin,
    isLoggedIn,
    login,
    fetchUserInfo,
    updateUserInfo,
    updateAvatar,
    logout,
    resetState
  }
})
```

### 3.2 `stores/app.ts` - 微调

**变更点**：无重大变更，保持现有结构。

### 3.3 `stores/course.ts` - 微调

**变更点**：将 `isCourseOwner` 中的 `useUserStore()` 调用改为外部参数或保持（Pinia 允许 store 间引用，但需注意循环依赖）。

```typescript
// 保持现有结构，isCourseOwner 的实现可以保留
// 因为 Pinia 的 store 间引用是官方推荐的模式
```

### 3.4 `stores/permission.ts` - 🆕 从旧版迁移重写

**旧版问题**：
- 使用 Options API
- `generateRoutes` 返回 Promise 但内部是同步逻辑
- `filterAsyncRouter` 等函数暴露在模块顶层
- 缺少类型定义

**新版设计**：

```typescript
// src/stores/permission.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import router, { constantRoutes, dynamicRoutes } from '@/router'

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref<RouteRecordRaw[]>([])
  const addRoutes = ref<RouteRecordRaw[]>([])
  const sidebarRouters = ref<RouteRecordRaw[]>([])
  const isRoutesGenerated = ref(false)

  function generateRoutes() {
    addRoutes.value = dynamicRoutes
    routes.value = [...constantRoutes, ...dynamicRoutes]
    sidebarRouters.value = [...routes.value]
    isRoutesGenerated.value = true

    dynamicRoutes.forEach((route) => router.addRoute(route))
    return dynamicRoutes
  }

  return {
    routes,
    addRoutes,
    sidebarRouters,
    isRoutesGenerated,
    generateRoutes
  }
})
```

### 3.5 `stores/settings.ts` - 🆕 从旧版迁移 + 持久化

**旧版问题**：
- 手动 `JSON.parse(localStorage.getItem('layout-setting'))`
- `changeSetting` 使用 `this.hasOwnProperty(key)` 不安全
- 缺少类型定义

**新版设计**：

```typescript
// src/stores/settings.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useDark, useToggle } from '@vueuse/core'
import defaultSettings from '@/settings'

const isDark = useDark()
const toggleDark = useToggle(isDark)

export const useSettingsStore = defineStore('settings', () => {
  const theme = ref(defaultSettings.theme || '#409EFF')
  const sideTheme = ref(defaultSettings.sideTheme || 'theme-dark')
  const showSettings = ref(defaultSettings.showSettings)
  const topNav = ref(defaultSettings.topNav)
  const tagsView = ref(defaultSettings.tagsView)
  const tagsIcon = ref(defaultSettings.tagsIcon)
  const fixedHeader = ref(defaultSettings.fixedHeader)
  const sidebarLogo = ref(defaultSettings.sidebarLogo)
  const dynamicTitle = ref(defaultSettings.dynamicTitle)
  const footerVisible = ref(defaultSettings.footerVisible)
  const footerContent = ref(defaultSettings.footerContent)
  const isDarkMode = ref(isDark.value)

  function changeSetting(key: string, value: unknown) {
    switch (key) {
      case 'theme': theme.value = value as string; break
      case 'sideTheme': sideTheme.value = value as string; break
      case 'topNav': topNav.value = value as boolean; break
      case 'tagsView': tagsView.value = value as boolean; break
      case 'tagsIcon': tagsIcon.value = value as boolean; break
      case 'fixedHeader': fixedHeader.value = value as boolean; break
      case 'sidebarLogo': sidebarLogo.value = value as boolean; break
      case 'dynamicTitle': dynamicTitle.value = value as boolean; break
      case 'footerVisible': footerVisible.value = value as boolean; break
    }
  }

  function toggleTheme() {
    isDarkMode.value = !isDarkMode.value
    toggleDark()
  }

  return {
    theme, sideTheme, showSettings, topNav, tagsView, tagsIcon,
    fixedHeader, sidebarLogo, dynamicTitle, footerVisible, footerContent,
    isDarkMode,
    changeSetting, toggleTheme
  }
}, {
  persist: {
    key: 'layout-setting',
    paths: ['theme', 'sideTheme', 'topNav', 'tagsView', 'tagsIcon',
            'fixedHeader', 'sidebarLogo', 'dynamicTitle', 'footerVisible']
  }
})
```

### 3.6 `stores/tagsView.ts` - 🆕 从旧版迁移重写

**旧版问题**：
- 大量 `new Promise(resolve => ...)` 包装同步操作
- `Object.assign({}, view)` 可用展开运算符替代
- 缺少类型定义

**新版设计**：

```typescript
// src/stores/tagsView.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteLocationNormalizedLoaded } from 'vue-router'

export interface TagView extends Partial<RouteLocationNormalizedLoaded> {
  title?: string
}

export const useTagsViewStore = defineStore('tagsView', () => {
  const visitedViews = ref<TagView[]>([])
  const cachedViews = ref<string[]>([])
  const iframeViews = ref<TagView[]>([])

  function addView(view: TagView) {
    addVisitedView(view)
    addCachedView(view)
  }

  function addVisitedView(view: TagView) {
    if (visitedViews.value.some(v => v.path === view.path)) return
    visitedViews.value.push({ ...view, title: view.meta?.title || 'no-name' })
  }

  function addCachedView(view: TagView) {
    if (cachedViews.value.includes(view.name as string)) return
    if (!view.meta?.noCache) {
      cachedViews.value.push(view.name as string)
    }
  }

  function addIframeView(view: TagView) {
    if (iframeViews.value.some(v => v.path === view.path)) return
    iframeViews.value.push({ ...view, title: view.meta?.title || 'no-name' })
  }

  function delView(view: TagView) {
    delVisitedView(view)
    delCachedView(view)
  }

  function delVisitedView(view: TagView) {
    const idx = visitedViews.value.findIndex(v => v.path === view.path)
    if (idx > -1) visitedViews.value.splice(idx, 1)
    iframeViews.value = iframeViews.value.filter(v => v.path !== view.path)
  }

  function delCachedView(view: TagView) {
    const idx = cachedViews.value.indexOf(view.name as string)
    if (idx > -1) cachedViews.value.splice(idx, 1)
  }

  function delOthersViews(view: TagView) {
    visitedViews.value = visitedViews.value.filter(
      v => v.meta?.affix || v.path === view.path
    )
    iframeViews.value = iframeViews.value.filter(v => v.path === view.path)
    const idx = cachedViews.value.indexOf(view.name as string)
    cachedViews.value = idx > -1 ? [cachedViews.value[idx]] : []
  }

  function delAllViews() {
    visitedViews.value = visitedViews.value.filter(v => v.meta?.affix)
    iframeViews.value = []
    cachedViews.value = []
  }

  function updateVisitedView(view: TagView) {
    const idx = visitedViews.value.findIndex(v => v.path === view.path)
    if (idx > -1) visitedViews.value[idx] = { ...view }
  }

  return {
    visitedViews, cachedViews, iframeViews,
    addView, addVisitedView, addCachedView, addIframeView,
    delView, delVisitedView, delCachedView,
    delOthersViews, delAllViews, updateVisitedView
  }
})
```

### 3.7 `stores/index.ts` - 更新导出

```typescript
// src/stores/index.ts
export { useUserStore } from './user'
export { useAppStore } from './app'
export { useCourseStore } from './course'
export { useExamStore } from './exam'
export { usePermissionStore } from './permission'
export { useSettingsStore } from './settings'
export { useTagsViewStore } from './tagsView'
```

---

## 四、迁移步骤

### Step 1：安装持久化插件

```bash
npm add pinia-plugin-persistedstate
```

### Step 2：在 main.ts 中注册插件

```typescript
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)
```

### Step 3：创建新版 Store 文件

按以下顺序创建：
1. `stores/permission.ts`
2. `stores/settings.ts`
3. `stores/tagsView.ts`
4. 更新 `stores/index.ts`
5. 微调 `stores/user.ts`

### Step 4：更新引用

全局搜索并替换以下导入路径：

| 旧导入 | 新导入 |
|--------|--------|
| `import useUserStore from '@/store/modules/user.js'` | `import { useUserStore } from '@/stores'` |
| `import useAppStore from '@/store/modules/app.js'` | `import { useAppStore } from '@/stores'` |
| `import usePermissionStore from '@/store/modules/permission'` | `import { usePermissionStore } from '@/stores'` |
| `import useSettingsStore from '@/store/modules/settings'` | `import { useSettingsStore } from '@/stores'` |
| `import useTagsViewStore from '@/store/modules/tags-view'` | `import { useTagsViewStore } from '@/stores'` |
| `import store from '@/store'` | 删除（Pinia 不需要根 store） |

### Step 5：删除旧版 Store

删除整个 `src/store/` 目录。

### Step 6：验证

- [ ] 登录流程正常
- [ ] 路由权限正常
- [ ] 设置页面正常
- [ ] 标签导航正常
- [ ] 主题切换正常
