import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'

export function useMessage() {
  function success(message: string) {
    ElMessage.success(message)
  }

  function error(message: string) {
    ElMessage.error(message)
  }

  function warning(message: string) {
    ElMessage.warning(message)
  }

  function info(message: string) {
    ElMessage.info(message)
  }

  function confirm(message: string, title = '提示') {
    return ElMessageBox.confirm(message, title, {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
  }

  function notify(message: string, type: 'success' | 'warning' | 'info' | 'error' = 'info') {
    ElNotification({ message, type })
  }

  return { success, error, warning, info, confirm, notify }
}
