import 'vue-router'

declare module 'vue-router' {
  interface RouteMeta {
    title?: string
    icon?: string
    hidden?: boolean
    affix?: boolean
    noCache?: boolean
    breadcrumb?: boolean
    activeMenu?: string
    permissions?: string[]
    roles?: string[]
    link?: string
  }
}
