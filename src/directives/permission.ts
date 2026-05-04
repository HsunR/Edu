import type { App, Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'

function hasRole(el: HTMLElement, binding: DirectiveBinding) {
  const { value } = binding
  const superAdmin = 'admin'
  const roles = useUserStore().roles

  if (value && Array.isArray(value) && value.length > 0) {
    const hasRole = roles.some(role => superAdmin === role || value.includes(role))
    if (!hasRole) {
      el.parentNode?.removeChild(el)
    }
  } else {
    throw new Error('请设置角色权限标签值')
  }
}

function hasPermi(el: HTMLElement, binding: DirectiveBinding) {
  const { value } = binding
  const allPermission = '*:*:*'
  const permissions = useUserStore().permissions

  if (value && Array.isArray(value) && value.length > 0) {
    const hasPermissions = permissions.some(permission => allPermission === permission || value.includes(permission))
    if (!hasPermissions) {
      el.parentNode?.removeChild(el)
    }
  } else {
    throw new Error('请设置操作权限标签值')
  }
}

export function setupPermissionDirectives(app: App) {
  app.directive('hasRole', hasRole as Directive)
  app.directive('hasPermi', hasPermi as Directive)
}
