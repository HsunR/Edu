import type { PageRequest } from '@/types/api'

export interface DeptVO {
  deptId: number
  parentId: number
  ancestors: string
  deptName: string
  orderNum: number
  leader: string
  phone: string
  email: string
  status: string
  children?: DeptVO[]
}

export interface DeptQueryRequest extends PageRequest {
  deptName?: string
  status?: string
}

export interface DeptRequest {
  deptId?: number
  parentId: number
  deptName: string
  orderNum: number
  leader?: string
  phone?: string
  email?: string
  status?: string
}

export interface MenuVO {
  menuId: number
  menuName: string
  parentId: number
  orderNum: number
  path: string
  component: string
  query: string
  routeName: string
  isFrame: number
  isCache: number
  menuType: string
  visible: string
  status: string
  perms: string
  icon: string
  children?: MenuVO[]
}

export interface MenuQueryRequest extends PageRequest {
  menuName?: string
  status?: string
}

export interface MenuRequest {
  menuId?: number
  menuName: string
  parentId: number
  orderNum: number
  path?: string
  component?: string
  query?: string
  routeName?: string
  isFrame?: number
  isCache?: number
  menuType: string
  visible?: string
  status?: string
  perms?: string
  icon?: string
}

export interface TreeSelectVO {
  id: number
  label: string
  children?: TreeSelectVO[]
}

export interface RoleVO {
  roleId: number
  roleName: string
  roleKey: string
  roleSort: number
  dataScope: string
  menuCheckStrictly: boolean
  deptCheckStrictly: boolean
  status: string
  remark: string
  createTime: string
}

export interface RoleQueryRequest extends PageRequest {
  roleName?: string
  roleKey?: string
  status?: string
}

export interface RoleRequest {
  roleId?: number
  roleName: string
  roleKey: string
  roleSort: number
  dataScope?: string
  menuCheckStrictly?: boolean
  deptCheckStrictly?: boolean
  status?: string
  remark?: string
  menuIds?: number[]
  deptIds?: number[]
}

export interface NoticeVO {
  noticeId: number
  noticeTitle: string
  noticeType: string
  noticeContent: string
  status: string
  creator: string
  createTime: string
  remark: string
}

export interface NoticeQueryRequest extends PageRequest {
  noticeTitle?: string
  noticeType?: string
  status?: string
  creator?: string
}

export interface NoticeRequest {
  noticeId?: number
  noticeTitle: string
  noticeType: string
  noticeContent?: string
  status?: string
  remark?: string
}

export interface SysUserVO {
  userId: number
  deptId: number
  userName: string
  nickName: string
  email: string
  phonenumber: string
  sex: string
  avatar: string
  status: string
  remark: string
  createTime: string
  dept?: DeptVO
  roles?: RoleVO[]
}

export interface SysUserQueryRequest extends PageRequest {
  userName?: string
  phonenumber?: string
  status?: string
  deptId?: number
}

export interface SysUserRequest {
  userId?: number
  deptId?: number
  userName: string
  nickName: string
  password?: string
  email?: string
  phonenumber?: string
  sex?: string
  status?: string
  remark?: string
  roleIds?: number[]
  postIds?: number[]
}

export interface AuthRoleRequest {
  userId: number
  roleIds: number[]
}
