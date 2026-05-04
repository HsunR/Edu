import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import ElementPlus from 'element-plus'
import locale from 'element-plus/es/locale/lang/zh-cn'

import App from './App.vue'
import router from './router'
import './router/guards'
import { setupDirectives } from './directives'
import { setupGlobalComponents } from './components/setup'

import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import '@/assets/styles/index.scss'
import 'virtual:svg-icons-register'

const app = createApp(App)

const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

app.use(router)

app.use(ElementPlus, {
  locale,
  size: localStorage.getItem('size') || 'default',
})

setupDirectives(app)
setupGlobalComponents(app)

app.mount('#app')
