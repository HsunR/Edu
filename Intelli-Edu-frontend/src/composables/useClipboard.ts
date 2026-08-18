import { ref } from 'vue'
import { ElMessage } from 'element-plus'

export function useClipboard() {
  const copied = ref(false)

  async function copy(text: string) {
    try {
      await navigator.clipboard.writeText(text)
      copied.value = true
      ElMessage.success('复制成功')
      setTimeout(() => { copied.value = false }, 2000)
    } catch {
      ElMessage.error('复制失败')
    }
  }

  return { copied, copy }
}
