import type { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'

export const dynamicRoutes: RouteRecordRaw[] = [
  {
    path: '/system/user-auth',
    component: Layout,
    meta: { hidden: true, permissions: ['system:user:edit'] },
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole.vue'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user' },
      },
    ],
  },
  {
    path: '/system/role-auth',
    component: Layout,
    meta: { hidden: true, permissions: ['system:role:edit'] },
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser.vue'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role' },
      },
    ],
  },
]
