import { ref, type Ref } from 'vue'

export function useLoading(initial = false) {
  const loading: Ref<boolean> = ref(initial)

  const startLoading = () => { loading.value = true }
  const stopLoading = () => { loading.value = false }

  async function withLoading<T>(fn: () => Promise<T>): Promise<T> {
    try {
      startLoading()
      return await fn()
    } finally {
      stopLoading()
    }
  }

  return { loading, startLoading, stopLoading, withLoading }
}
