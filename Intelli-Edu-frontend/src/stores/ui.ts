import { ref } from 'vue'
import { defineStore } from 'pinia'

export type ToastTone = 'success' | 'error' | 'info'

interface ToastMessage {
  id: number
  message: string
  tone: ToastTone
}

interface ConfirmRequest {
  title: string
  message: string
  confirmLabel: string
  danger: boolean
  resolve: (confirmed: boolean) => void
}

let nextToastId = 0

export const useUiStore = defineStore('ui', () => {
  const toasts = ref<ToastMessage[]>([])
  const confirmation = ref<ConfirmRequest | null>(null)

  function notify(message: string, tone: ToastTone = 'info') {
    const id = ++nextToastId
    toasts.value.push({ id, message, tone })
    window.setTimeout(() => dismiss(id), tone === 'error' ? 6000 : 3500)
  }

  function dismiss(id: number) {
    toasts.value = toasts.value.filter((toast) => toast.id !== id)
  }

  function confirm(options: {
    title: string
    message: string
    confirmLabel?: string
    danger?: boolean
  }) {
    if (confirmation.value) confirmation.value.resolve(false)
    return new Promise<boolean>((resolve) => {
      confirmation.value = {
        title: options.title,
        message: options.message,
        confirmLabel: options.confirmLabel || '确认',
        danger: options.danger ?? false,
        resolve,
      }
    })
  }

  function answerConfirmation(confirmed: boolean) {
    const pending = confirmation.value
    confirmation.value = null
    pending?.resolve(confirmed)
  }

  return { toasts, confirmation, notify, dismiss, confirm, answerConfirmation }
})
