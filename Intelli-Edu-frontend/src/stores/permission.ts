import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import router, { constantRoutes } from '@/router'
import { dynamicRoutes } from '@/router/routes/modules'
import { useUserStore } from '@/stores/user'
import type { UserType } from '@/types/enums'

function filterRoutes(routes: RouteRecordRaw[], userType: UserType): RouteRecordRaw[] {
  return routes
    .filter((route) => {
      const roles = route.meta?.roles
      if (!roles) return true
      return roles.includes(userType)
    })
    .map((route) => {
      if (route.children) {
        const filteredChildren = filterRoutes(route.children, userType)
        return { ...route, children: filteredChildren }
      }
      return route
    })
    .filter((route) => {
      if (route.children && route.children.length === 0) {
        return false
      }
      return true
    })
}

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref<RouteRecordRaw[]>([])
  const addRoutes = ref<RouteRecordRaw[]>([])
  const sidebarRouters = ref<RouteRecordRaw[]>([])
  const isRoutesGenerated = ref(false)

  function generateRoutes() {
    const userStore = useUserStore()
    const userType = userStore.userType

    if (!userType) {
      console.warn('[PermissionStore] generateRoutes called without userType')
      isRoutesGenerated.value = true
      return []
    }

    const filteredRoutes = filterRoutes(dynamicRoutes, userType)

    addRoutes.value = filteredRoutes
    routes.value = [...constantRoutes, ...filteredRoutes]
    sidebarRouters.value = filteredRoutes
    isRoutesGenerated.value = true

    filteredRoutes.forEach((route) => router.addRoute(route))

    return filteredRoutes
  }

  // 用于顶部导航栏与侧边栏联动时，设置当前选中顶级菜单的子路由
  // 传入的数据来源于已过滤的 sidebarRouters，不会绕过权限控制
  function setSidebarRouters(routeList: RouteRecordRaw[]) {
    sidebarRouters.value = routeList
  }

  function resetRoutes() {
    addRoutes.value.forEach((route) => {
      if (route.name) {
        router.removeRoute(route.name)
      } else if (route.path) {
        const matched = router.getRoutes().find((r) => r.path === route.path)
        if (matched && matched.name) {
          router.removeRoute(matched.name)
        }
      }
    })
    addRoutes.value = []
    routes.value = [...constantRoutes]
    sidebarRouters.value = []
    isRoutesGenerated.value = false
  }

  return {
    routes,
    addRoutes,
    sidebarRouters,
    isRoutesGenerated,
    generateRoutes,
    setSidebarRouters,
    resetRoutes,
  }
})
