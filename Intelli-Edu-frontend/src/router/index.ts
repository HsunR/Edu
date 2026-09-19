import { createRouter, createWebHistory } from 'vue-router'
import { useSessionStore } from '@/stores/session'
import type { UserRole } from '@/types'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    eyebrow?: string
    roles?: UserRole[]
    public?: boolean
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
      meta: { title: '登录', public: true },
    },
    {
      path: '/',
      component: () => import('@/layouts/AppShell.vue'),
      children: [
        {
          path: '',
          name: 'dashboard',
          component: () => import('@/views/DashboardView.vue'),
          meta: { title: '工作台', eyebrow: 'OVERVIEW' },
        },
        {
          path: 'courses',
          name: 'courses',
          component: () => import('@/views/CoursesView.vue'),
          meta: { title: '课程浏览', eyebrow: 'COURSES' },
        },
        {
          path: 'my-classes',
          name: 'my-classes',
          component: () => import('@/views/MyCoursesView.vue'),
          meta: { title: '我的班级', eyebrow: 'CLASSES', roles: ['Student'] },
        },
        {
          path: 'courses/:id',
          name: 'course-detail',
          component: () => import('@/views/LearningView.vue'),
          meta: { title: '课程详情', eyebrow: 'COURSE' },
        },
        {
          path: 'teaching',
          name: 'teaching',
          component: () => import('@/views/TeachingView.vue'),
          meta: { title: '课程管理', eyebrow: 'TEACHING', roles: ['Teacher'] },
        },
        {
          path: 'exams',
          name: 'exams',
          component: () => import('@/views/ExamsView.vue'),
          meta: { title: '考试与作业', eyebrow: 'EXAMS' },
        },
        {
          path: 'resources',
          name: 'resources',
          component: () => import('@/views/ResourcesView.vue'),
          meta: { title: '我的资源', eyebrow: 'RESOURCES' },
        },
        {
          path: 'learning',
          name: 'learning',
          component: () => import('@/views/AnalyticsView.vue'),
          meta: { title: '学情中心', eyebrow: 'LEARNING', roles: ['Student', 'Teacher'] },
        },
        {
          path: 'users',
          name: 'users',
          component: () => import('@/views/SystemView.vue'),
          meta: { title: '用户管理', eyebrow: 'USERS', roles: ['Admin'] },
        },
        {
          path: 'profile',
          name: 'profile',
          component: () => import('@/views/ProfileView.vue'),
          meta: { title: '个人资料', eyebrow: 'PROFILE' },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      component: () => import('@/views/NotFoundView.vue'),
      meta: { public: true },
    },
  ],
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach(async (to) => {
  const session = useSessionStore()
  await session.initialize()
  if (!to.meta.public && !session.loggedIn)
    return { name: 'login', query: { redirect: to.fullPath } }
  if (to.name === 'login' && session.loggedIn) return { name: 'dashboard' }
  if (to.meta.roles?.length && (!session.role || !to.meta.roles.includes(session.role)))
    return { name: 'dashboard' }
})
router.afterEach((to) => {
  document.title = `${String(to.meta.title || 'Intelli Edu')} · Intelli Edu`
})

export default router
