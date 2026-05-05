import type { UserType } from '@/types/enums'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    hidden?: boolean
    affix?: boolean
    noCache?: boolean
    breadcrumb?: boolean
    activeMenu?: string
    roles?: UserType[]
    link?: string
  }
}
