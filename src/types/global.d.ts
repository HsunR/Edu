declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<object, object, unknown>
  export default component
}

declare module 'nprogress'

declare module 'json-bigint' {
  interface Options {
    strict?: boolean
    storeAsString?: boolean
    alwaysParseAsBig?: boolean
  }
  function JSONbig(options?: Options): {
    parse(text: string): unknown
    stringify(value: unknown, replacer?: unknown, space?: unknown): string
  }
  export default JSONbig
}
