import type { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'

export const systemRoutes: RouteRecordRaw[] = [
  {
    path: '/system',
    component: Layout,
    meta: { title: '系统管理', icon: 'system' },
    children: [
      {
        path: 'user',
        component: () => import('@/views/system/user/index.vue'),
        name: 'SystemUser',
        meta: { title: '用户管理', icon: 'user' },
      },
      {
        path: 'role',
        component: () => import('@/views/system/role/index.vue'),
        name: 'SystemRole',
        meta: { title: '角色管理', icon: 'peoples' },
      },
      {
        path: 'menu',
        component: () => import('@/views/system/menu/index.vue'),
        name: 'SystemMenu',
        meta: { title: '菜单管理', icon: 'tree-table' },
      },
      {
        path: 'dept',
        component: () => import('@/views/system/dept/index.vue'),
        name: 'SystemDept',
        meta: { title: '部门管理', icon: 'tree' },
      },
      {
        path: 'notice',
        component: () => import('@/views/system/notice/index.vue'),
        name: 'SystemNotice',
        meta: { title: '通知公告', icon: 'message' },
      },
    ],
  },
]
