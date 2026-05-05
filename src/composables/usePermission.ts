import { useUserStore } from '@/stores'
import { UserType } from '@/types/enums'

export function usePermission() {
  const userStore = useUserStore()

  function hasRole(role: UserType | UserType[]): boolean {
    const roles = Array.isArray(role) ? role : [role]
    const currentType = userStore.userType
    if (!currentType) return false
    return roles.includes(currentType)
  }

  function hasAnyRole(...roles: UserType[]): boolean {
    return roles.some((role) => hasRole(role))
  }

  function isAdmin(): boolean {
    return userStore.isAdmin
  }

  function isTeacher(): boolean {
    return userStore.isTeacher
  }

  function isStudent(): boolean {
    return userStore.isStudent
  }

  return { hasRole, hasAnyRole, isAdmin, isTeacher, isStudent }
}
