import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles.css'

window.addEventListener('auth-expired', () => {
  const currentPath = router.currentRoute.value.fullPath
  if (router.currentRoute.value.name !== 'login')
    router.replace({ name: 'login', query: { redirect: currentPath } })
})

createApp(App).use(createPinia()).use(router).mount('#app')
