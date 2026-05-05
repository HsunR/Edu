import type { RouteRecordRaw } from 'vue-router'
import { courseRoutes } from './course'
import { systemRoutes } from './system'
import { resourceRoutes } from './resource'

export const dynamicRoutes: RouteRecordRaw[] = [
  ...courseRoutes,
  ...systemRoutes,
  ...resourceRoutes,
]

export { courseRoutes, systemRoutes, resourceRoutes }
