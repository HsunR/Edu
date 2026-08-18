import { ref, computed, onUnmounted } from 'vue'

export function useCountdown(defaultSeconds = 60) {
  const remaining = ref(0)
  const isCounting = ref(false)
  let timer: ReturnType<typeof setInterval> | null = null

  function start(seconds = defaultSeconds) {
    remaining.value = seconds
    isCounting.value = true

    timer = setInterval(() => {
      remaining.value--
      if (remaining.value <= 0) {
        stop()
      }
    }, 1000)
  }

  function stop() {
    if (timer) {
      clearInterval(timer)
      timer = null
    }
    remaining.value = 0
    isCounting.value = false
  }

  const buttonText = computed(() =>
    isCounting.value ? `${remaining.value}秒后重新获取` : '获取验证码'
  )

  onUnmounted(stop)

  return { remaining, isCounting, buttonText, start, stop }
}
