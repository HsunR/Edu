import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import router, { constantRoutes, dynamicRoutes } from '@/router'

export const usePermissionStore = defineStore('permission', () => {
  const routes = ref<RouteRecordRaw[]>([])
  const addRoutes = ref<RouteRecordRaw[]>([])
  const defaultRoutes = ref<RouteRecordRaw[]>([])
  const topbarRouters = ref<RouteRecordRaw[]>([])
  const sidebarRouters = ref<RouteRecordRaw[]>([])
  const isRoutesGenerated = ref(false)

  function setRoutes(routeList: RouteRecordRaw[]) {
    addRoutes.value = routeList
    routes.value = [...constantRoutes, ...routeList]
  }

  function setDefaultRoutes(routeList: RouteRecordRaw[]) {
    defaultRoutes.value = [...constantRoutes, ...routeList]
  }

  function setTopbarRoutes(routeList: RouteRecordRaw[]) {
    topbarRouters.value = routeList
  }

  function setSidebarRouters(routeList: RouteRecordRaw[]) {
    sidebarRouters.value = routeList
  }

  function generateRoutes() {
    addRoutes.value = dynamicRoutes
    routes.value = [...constantRoutes, ...dynamicRoutes]
    setSidebarRouters(routes.value)
    setDefaultRoutes(routes.value)
    setTopbarRoutes(routes.value)
    isRoutesGenerated.value = true

    dynamicRoutes.forEach((route) => router.addRoute(route))
    return dynamicRoutes
  }

  return {
    routes,
    addRoutes,
    defaultRoutes,
    topbarRouters,
    sidebarRouters,
    isRoutesGenerated,
    setRoutes,
    setDefaultRoutes,
    setTopbarRoutes,
    setSidebarRouters,
    generateRoutes
  }
})
