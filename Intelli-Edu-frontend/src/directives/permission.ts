import type { App, Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'
import { UserType } from '@/types/enums'

function hasRole(el: HTMLElement, binding: DirectiveBinding<UserType | UserType[]>) {
  const userStore = useUserStore()
  const requiredRoles = Array.isArray(binding.value) ? binding.value : [binding.value]

  if (requiredRoles.length === 0) {
    throw new Error('v-hasRole 需要指定角色')
  }

  const currentType = userStore.userType
  if (!currentType) return

  const hasAccess = requiredRoles.includes(currentType)
  if (!hasAccess) {
    el.parentNode?.removeChild(el)
  }
}

export function setupPermissionDirectives(app: App) {
  app.directive('hasRole', hasRole as Directive)
}
