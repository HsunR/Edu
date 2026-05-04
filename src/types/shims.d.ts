declare module 'json-bigint' {
  interface Options {
    strict?: boolean
    storeAsString?: boolean
    alwaysParseAsBig?: boolean
  }
  function JSONbig(options?: Options): {
    parse(text: string): any
    stringify(value: any, replacer?: any, space?: any): string
  }
  export default JSONbig
}

declare module '@/utils/ruoyi' {
  export function tansParams(params: any): string
  export function blobValidate(data: any): boolean
}

declare module '@/utils/errorCode' {
  const errorCode: Record<string, string>
  export default errorCode
}

declare module '@/store/modules/permission' {
  import { defineStore } from 'pinia'
  const usePermissionStore: ReturnType<typeof defineStore>
  export default usePermissionStore
}

declare module './router' {
  import type { Router } from 'vue-router'
  const router: Router
  export default router
}
