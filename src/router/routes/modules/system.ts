import type { RouteRecordRaw } from 'vue-router'
import { UserType } from '@/types/enums'

export const systemRoutes: RouteRecordRaw[] = [
  {
    path: '/user',
    component: () => import('@/layout/index.vue'),
    name: 'UserLayout',
    meta: { hidden: true },
    children: [
      {
        path: 'profile',
        component: () => import('@/views/system/user/profile/index.vue'),
        name: 'Profile',
        meta: { title: '个人中心', icon: 'user' },
      },
    ],
  },
  {
    path: '/system',
    component: () => import('@/layout/index.vue'),
    name: 'SystemLayout',
    meta: { title: '系统管理', icon: 'system', roles: [UserType.Admin] },
    children: [
      {
        path: 'user',
        component: () => import('@/views/system/user/index.vue'),
        name: 'SystemUser',
        meta: { title: '用户管理', icon: 'user', roles: [UserType.Admin] },
      },
      {
        path: 'role',
        component: () => import('@/views/system/role/index.vue'),
        name: 'SystemRole',
        meta: { title: '角色管理', icon: 'peoples', roles: [UserType.Admin] },
      },
      {
        path: 'menu',
        component: () => import('@/views/system/menu/index.vue'),
        name: 'SystemMenu',
        meta: { title: '菜单管理', icon: 'tree-table', roles: [UserType.Admin] },
      },
      {
        path: 'dept',
        component: () => import('@/views/system/dept/index.vue'),
        name: 'SystemDept',
        meta: { title: '部门管理', icon: 'tree', roles: [UserType.Admin] },
      },
      {
        path: 'notice',
        component: () => import('@/views/system/notice/index.vue'),
        name: 'SystemNotice',
        meta: { title: '通知公告', icon: 'message', roles: [UserType.Admin] },
      },
    ],
  },
  {
    path: '/system/user-auth',
    component: () => import('@/layout/index.vue'),
    name: 'SystemUserAuth',
    meta: { hidden: true, roles: [UserType.Admin] },
    children: [
      {
        path: 'role/:userId(\\d+)',
        component: () => import('@/views/system/user/authRole.vue'),
        name: 'AuthRole',
        meta: { title: '分配角色', activeMenu: '/system/user', roles: [UserType.Admin] },
      },
    ],
  },
  {
    path: '/system/role-auth',
    component: () => import('@/layout/index.vue'),
    name: 'SystemRoleAuth',
    meta: { hidden: true, roles: [UserType.Admin] },
    children: [
      {
        path: 'user/:roleId(\\d+)',
        component: () => import('@/views/system/role/authUser.vue'),
        name: 'AuthUser',
        meta: { title: '分配用户', activeMenu: '/system/role', roles: [UserType.Admin] },
      },
    ],
  },
]
