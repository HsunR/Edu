import { ref, computed } from 'vue'
import type { UploadUserFile } from 'element-plus'
import { ElMessage } from 'element-plus'
import { getToken } from '@/utils/auth'

interface UploadOptions {
  accept?: string
  maxSize?: number
  maxCount?: number
}

export function useUpload(options: UploadOptions = {}) {
  const { accept = '', maxSize = 5 * 1024 * 1024, maxCount = 5 } = options

  const fileList = ref<UploadUserFile[]>([])

  const headers = computed(() => ({
    Authorization: `Bearer ${getToken()}`
  }))

  function beforeUpload(file: File) {
    if (maxSize && file.size > maxSize) {
      ElMessage.error(`文件大小不能超过 ${maxSize / 1024 / 1024}MB`)
      return false
    }
    return true
  }

  function handleExceed() {
    ElMessage.error(`最多上传 ${maxCount} 个文件`)
  }

  function handleSuccess(response: any) {
    if (response.code === 200) {
      ElMessage.success('上传成功')
    } else {
      ElMessage.error(response.msg || '上传失败')
    }
  }

  function handleError() {
    ElMessage.error('上传失败')
  }

  return {
    fileList,
    headers,
    beforeUpload,
    handleExceed,
    handleSuccess,
    handleError,
  }
}
