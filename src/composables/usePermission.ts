import { useUserStore } from '@/stores'

export function usePermission() {
  const userStore = useUserStore()

  function hasRole(role: string | string[]): boolean {
    const roles = Array.isArray(role) ? role : [role]
    return roles.includes(userStore.userInfo?.type || '')
  }

  function hasPermission(_permission: string | string[]): boolean {
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
