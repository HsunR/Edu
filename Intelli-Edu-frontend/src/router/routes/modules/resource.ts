import type { RouteRecordRaw } from 'vue-router'

export const resourceRoutes: RouteRecordRaw[] = [
  {
    path: '/resource',
    component: () => import('@/layout/index.vue'),
    name: 'ResourceLayout',
    meta: { title: '资源管理', icon: 'education' },
    children: [
      {
        path: '',
        component: () => import('@/views/resource/index.vue'),
        name: 'ResourceManagement',
        meta: { title: '我的资源', icon: 'education' },
      },
    ],
  },
  {
    path: '/setting',
    component: () => import('@/layout/index.vue'),
    name: 'SettingLayout',
    children: [
      {
        path: '',
        component: () => import('@/views/setting/index.vue'),
        name: 'Setting',
        meta: { title: '设置', icon: 'system' },
      },
    ],
  },
]
