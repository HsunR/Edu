import type { App, Directive, DirectiveBinding } from 'vue'
import { ElMessage } from 'element-plus'

declare global {
  interface HTMLElement {
    _copyHandler?: () => void
  }
}

async function copyToClipboard(text: string) {
  try {
    await navigator.clipboard.writeText(text)
    ElMessage.success('复制成功')
  } catch {
    ElMessage.error('复制失败')
  }
}

const copyDirective: Directive = {
  mounted(el: HTMLElement, binding: DirectiveBinding) {
    el._copyHandler = () => copyToClipboard(binding.value)
    el.addEventListener('click', el._copyHandler)
  },
  updated(el: HTMLElement, binding: DirectiveBinding) {
    el.removeEventListener('click', el._copyHandler)
    el._copyHandler = () => copyToClipboard(binding.value)
    el.addEventListener('click', el._copyHandler)
  },
  unmounted(el: HTMLElement) {
    el.removeEventListener('click', el._copyHandler)
    delete el._copyHandler
  },
}

export function setupCopyDirective(app: App) {
  app.directive('copyText', copyDirective)
}
