# 前端 AI 开发指南 - Vue 开发规范与最佳实践

## 1. 项目目录结构

```
├── public/                    # 静态资源
│   └── vite.svg
├── src/
│   ├── api/                   # API 接口封装
│   │   ├── user.ts
│   │   ├── course.ts
│   │   ├── exam.ts
│   │   ├── resource.ts
│   │   ├── knowledge.ts
│   │   └── ai.ts
│   ├── assets/                # 图片、字体等资源
│   │   ├── images/
│   │   └── styles/
│   ├── components/            # 公共组件
│   │   ├── UploadButton.vue
│   │   ├── MarkdownViewer.vue
│   │   ├── VideoPlayer.vue
│   │   └── Pagination.vue
│   ├── composables/           # 组合式函数
│   │   ├── useUser.ts
│   │   ├── usePermission.ts
│   │   └── useUpload.ts
│   ├── layouts/               # 布局组件
│   │   ├── DefaultLayout.vue
│   │   ├── TeacherLayout.vue
│   │   └── BlankLayout.vue
│   ├── router/                # 路由配置
│   │   ├── index.ts
│   │   └── guard.ts
│   ├── stores/                # Pinia 状态管理
│   │   ├── user.ts
│   │   ├── app.ts
│   │   └── tabs.ts
│   ├── types/                 # TypeScript 类型定义
│   │   ├── common.ts
│   │   ├── enums.ts
│   │   ├── user.ts
│   │   ├── course.ts
│   │   ├── exam.ts
│   │   ├── resource.ts
│   │   ├── knowledge.ts
│   │   └── ai.ts
│   ├── utils/                 # 工具函数
│   │   ├── request.ts         # Axios 封装
│   │   ├── sse.ts             # SSE 流式处理
│   │   ├── format.ts          # 格式化工具
│   │   └── validate.ts        # 表单校验
│   ├── views/                 # 页面组件
│   │   ├── home/
│   │   ├── login/
│   │   ├── register/
│   │   ├── profile/
│   │   ├── courses/           # 学生端课程
│   │   ├── exams/             # 学生端考试
│   │   ├── ai-chat/
│   │   └── teacher/           # 教师端页面
│   │       ├── courses/
│   │       ├── questions/
│   │       ├── papers/
│   │       ├── exams/
│   │       └── knowledge/
│   ├── App.vue
│   └── main.ts
├── .env.development
├── .env.production
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

## 2. 命名规范

### 2.1 文件命名

| 类型 | 规范 | 示例 |
|------|------|------|
| 组件 | PascalCase | `CourseCard.vue`, `UserProfile.vue` |
| 组合式函数 | camelCase，use 前缀 | `useUser.ts`, `usePermission.ts` |
| API 模块 | camelCase | `user.ts`, `course.ts` |
| 类型定义 | camelCase | `user.ts`, `course.ts` |
| 工具函数 | camelCase | `request.ts`, `format.ts` |
| 页面目录 | kebab-case | `my-courses/`, `question-banks/` |

### 2.2 组件命名

```vue
<!-- 单文件组件 -->
<!-- CourseList.vue -->
<template>
  <div class="course-list">
    <course-card v-for="course in courses" :key="course.id" :course="course" />
  </div>
</template>

<script setup lang="ts">
import CourseCard from './CourseCard.vue'
</script>
```

### 2.3 变量命名

```typescript
// 响应式数据
const courseList = ref<CourseVO[]>([])
const currentCourse = ref<CourseVO | null>(null)
const loading = ref(false)

// 计算属性
const isTeacher = computed(() => userStore.userType === UserType.TEACHER)
const filteredCourses = computed(() =>
  courseList.value.filter(c => c.status === CourseStatus.PUBLISHED)
)

// 函数
const fetchCourseList = async () => { }
const handleSubmit = async () => { }
const onPageChange = (page: number) => { }
```

## 3. Vue 3 组合式 API 规范

### 3.1 组件结构顺序

```vue
<script setup lang="ts">
// 1. 导入（按类型分组）
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { courseApi } from '@/api/course'
import type { CourseVO } from '@/types/course'

// 2. 类型定义
interface Props {
  courseId: number
}

// 3. Props / Emits
const props = defineProps<Props>()
const emit = defineEmits<{
  update: [course: CourseVO]
}>()

// 4. 注入（依赖）
const router = useRouter()
const userStore = useUserStore()

// 5. 状态（ref / reactive）
const courseList = ref<CourseVO[]>([])
const loading = ref(false)

// 6. 计算属性
const hasCourses = computed(() => courseList.value.length > 0)

// 7. 方法
const fetchData = async () => {
  loading.value = true
  try {
    const res = await courseApi.listPublicCourses({ current: 1, pageSize: 10 })
    courseList.value = res.records
  } finally {
    loading.value = false
  }
}

const goToDetail = (courseId: number) => {
  router.push(`/courses/${courseId}`)
}

// 8. 生命周期
onMounted(() => {
  fetchData()
})
</script>
```

### 3.2 组合式函数封装

```typescript
// composables/usePermission.ts
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { UserType } from '@/types/enums'

export function usePermission() {
  const userStore = useUserStore()

  const isStudent = computed(() => userStore.userType === UserType.STUDENT)
  const isTeacher = computed(() => userStore.userType === UserType.TEACHER)
  const isAdmin = computed(() => userStore.userType === UserType.ADMIN)

  const checkRole = (roles: UserType[]) => roles.includes(userStore.userType)

  return {
    isStudent,
    isTeacher,
    isAdmin,
    checkRole
  }
}
```

```typescript
// composables/useCrud.ts
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface UseCrudOptions<T, Q> {
  listApi: (params: Q) => Promise<PageResult<T>>
  deleteApi?: (id: number) => Promise<void>
  createApi?: (data: any) => Promise<T>
  updateApi?: (id: number, data: any) => Promise<T>
}

export function useCrud<T, Q extends PageRequest>(options: UseCrudOptions<T, Q>) {
  const list = ref<T[]>([])
  const total = ref(0)
  const loading = ref(false)
  const queryParams = reactive<Q>({ current: 1, pageSize: 10 } as Q)

  const fetchList = async () => {
    loading.value = true
    try {
      const res = await options.listApi(queryParams)
      list.value = res.records
      total.value = res.total
    } finally {
      loading.value = false
    }
  }

  const handleDelete = async (id: number) => {
    if (!options.deleteApi) return
    await ElMessageBox.confirm('确定删除吗？', '提示')
    await options.deleteApi(id)
    ElMessage.success('删除成功')
    fetchList()
  }

  return {
    list,
    total,
    loading,
    queryParams,
    fetchList,
    handleDelete
  }
}
```

## 4. 路由配置

```typescript
// router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { UserType } from '@/types/enums'

const routes = [
  {
    path: '/',
    component: () => import('@/layouts/DefaultLayout.vue'),
    children: [
      { path: '', component: () => import('@/views/home/Home.vue') },
      {
        path: 'courses',
        children: [
          { path: '', component: () => import('@/views/courses/CourseList.vue') },
          { path: ':id', component: () => import('@/views/courses/CourseDetail.vue') }
        ]
      },
      {
        path: 'exams',
        meta: { requireAuth: true },
        children: [
          { path: '', component: () => import('@/views/exams/ExamList.vue') },
          { path: ':id/take', component: () => import('@/views/exams/TakeExam.vue') },
          { path: ':id/result', component: () => import('@/views/exams/ExamResult.vue') }
        ]
      },
      {
        path: 'ai-chat',
        component: () => import('@/views/ai-chat/AIChat.vue'),
        meta: { requireAuth: true }
      },
      {
        path: 'profile',
        component: () => import('@/views/profile/UserProfile.vue'),
        meta: { requireAuth: true }
      },
      // 教师端
      {
        path: 'teacher',
        meta: { requireAuth: true, roles: [UserType.TEACHER, UserType.ADMIN] },
        children: [
          { path: 'courses', component: () => import('@/views/teacher/courses/CourseManage.vue') },
          { path: 'questions', component: () => import('@/views/teacher/questions/QuestionManage.vue') },
          { path: 'papers', component: () => import('@/views/teacher/papers/PaperManage.vue') },
          { path: 'exams', component: () => import('@/views/teacher/exams/ExamManage.vue') },
          { path: 'knowledge', component: () => import('@/views/teacher/knowledge/KnowledgeManage.vue') }
        ]
      }
    ]
  },
  { path: '/login', component: () => import('@/views/login/Login.vue') },
  { path: '/register', component: () => import('@/views/register/Register.vue') },
  { path: '/403', component: () => import('@/views/error/403.vue') },
  { path: '/:pathMatch(.*)*', component: () => import('@/views/error/404.vue') }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
```

```typescript
// router/guard.ts
import router from './index'
import { useUserStore } from '@/stores/user'

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = localStorage.getItem('accessToken')

  // 设置页面标题
  if (to.meta.title) {
    document.title = to.meta.title as string
  }

  // 需要登录
  if (to.meta.requireAuth && !token) {
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    return
  }

  // 角色权限校验
  if (to.meta.roles && !to.meta.roles.includes(userStore.userType)) {
    next('/403')
    return
  }

  next()
})
```

## 5. Pinia Store 规范

```typescript
// stores/user.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { UserType } from '@/types/enums'

export const useUserStore = defineStore('user', () => {
  // State
  const token = ref(localStorage.getItem('accessToken') || '')
  const userInfo = ref<{
    userId: number
    name: string
    userType: UserType
    avatarUrl?: string
  } | null>(null)

  // Getters
  const isLoggedIn = computed(() => !!token.value)
  const isTeacher = computed(() => userInfo.value?.userType === UserType.TEACHER)
  const isAdmin = computed(() => userInfo.value?.userType === UserType.ADMIN)

  // Actions
  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('accessToken', newToken)
  }

  const setUserInfo = (info: typeof userInfo.value) => {
    userInfo.value = info
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    isTeacher,
    isAdmin,
    setToken,
    setUserInfo,
    logout
  }
})
```

## 6. 表单校验规范

```typescript
// utils/validate.ts
import type { FormRules } from 'element-plus'

/** 手机号校验 */
export const mobileValidator = (rule: any, value: string, callback: Function) => {
  if (!value) {
    callback(new Error('请输入手机号'))
  } else if (!/^1[3-9]\d{9}$/.test(value)) {
    callback(new Error('手机号格式不正确'))
  } else {
    callback()
  }
}

/** 密码强度校验 */
export const passwordValidator = (rule: any, value: string, callback: Function) => {
  if (!value || value.length < 6) {
    callback(new Error('密码长度不能少于6位'))
  } else if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(value)) {
    callback(new Error('密码必须包含字母和数字'))
  } else {
    callback()
  }
}

// 表单规则定义
export const loginRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, validator: passwordValidator, trigger: 'blur' }]
}

export const registerRules: FormRules = {
  name: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, validator: passwordValidator, trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback, source) => {
        if (value !== source.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}
```

## 7. 样式规范

### 7.1 SCSS 变量

```scss
// assets/styles/variables.scss
:root {
  --primary-color: #409eff;
  --success-color: #67c23a;
  --warning-color: #e6a23c;
  --danger-color: #f56c6c;
  --text-primary: #303133;
  --text-regular: #606266;
  --text-secondary: #909399;
  --border-color: #dcdfe6;
  --bg-color: #f5f7fa;
}
```

### 7.2 BEM 命名

```vue
<template>
  <div class="course-card">
    <img class="course-card__cover" :src="course.coverUrl" />
    <div class="course-card__content">
      <h3 class="course-card__title">{{ course.courseName }}</h3>
      <p class="course-card__desc">{{ course.description }}</p>
      <div class="course-card__footer">
        <span class="course-card__teacher">{{ course.teacherName }}</span>
        <el-tag class="course-card__status" :type="statusType">
          {{ statusText }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.course-card {
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);

  &__cover {
    width: 100%;
    height: 160px;
    object-fit: cover;
  }

  &__content {
    padding: 16px;
  }

  &__title {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 8px;
  }

  &__desc {
    color: var(--text-secondary);
    font-size: 14px;
    margin-bottom: 12px;
  }

  &__footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
```

## 8. 性能优化建议

### 8.1 组件懒加载

```typescript
const CourseDetail = () => import('@/views/courses/CourseDetail.vue')
```

### 8.2 列表虚拟滚动

```vue
<template>
  <el-table-v2
    :columns="columns"
    :data="courseList"
    :height="600"
    :row-height="60"
  />
</template>
```

### 8.3 图片懒加载

```vue
<img v-lazy="course.coverUrl" :src="placeholder" />
```

### 8.4 防抖节流

```typescript
import { debounce, throttle } from 'lodash-es'

const search = debounce((keyword: string) => {
  fetchSearchResults(keyword)
}, 300)

const scrollHandler = throttle(() => {
  checkScrollPosition()
}, 100)
```
