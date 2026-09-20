<script setup lang="ts">
import { computed, onBeforeUnmount, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowRight, Eye, EyeOff, ShieldCheck } from 'lucide-vue-next'
import BrandMark from '@/components/BrandMark.vue'
import { authApi } from '@/api/services'
import { useSessionStore } from '@/stores/session'

const router = useRouter()
const route = useRoute()
const session = useSessionStore()
const mode = ref<'login' | 'register'>('login')
const method = ref<2 | 4>(4)
const modes = [
  { value: 'login', label: '登录' },
  { value: 'register', label: '注册' },
] as const
const loginMethods = [
  { value: 4, label: '密码登录' },
  { value: 2, label: '邮箱验证码' },
] as const
const showPassword = ref(false)
const loading = ref(false)
const error = ref('')
const notice = ref('')
const sendingCode = ref(false)
const cooldown = ref(0)
let cooldownTimer: number | undefined
const form = ref({
  username: '',
  password: '',
  email: '',
  code: '',
  name: '',
  studentNo: '',
  grade: '',
  major: '',
  enrollmentYear: new Date().getFullYear(),
  school: '',
})
const accountLabel = computed(() => (method.value === 2 ? '邮箱' : '用户名'))

function selectMode(nextMode: 'login' | 'register') {
  mode.value = nextMode
  method.value = nextMode === 'login' ? 4 : 2
  error.value = ''
  notice.value = ''
}

function selectMethod(nextMethod: 2 | 4) {
  method.value = nextMethod
}

async function sendCode() {
  error.value = ''
  notice.value = ''
  if (!/^\S+@\S+\.\S+$/.test(form.value.email)) {
    error.value = '请输入有效的邮箱地址'
    return
  }
  sendingCode.value = true
  try {
    if (mode.value === 'login')
      await authApi.sendLoginCode({
        loginType: 2,
        email: form.value.email,
      })
    else
      await authApi.sendRegisterCode({
        registerType: 2,
        email: form.value.email,
      })
    notice.value = '验证码已发送'
    cooldown.value = 60
    window.clearInterval(cooldownTimer)
    cooldownTimer = window.setInterval(() => {
      cooldown.value--
      if (cooldown.value <= 0) window.clearInterval(cooldownTimer)
    }, 1000)
  } catch (e) {
    error.value = e instanceof Error ? e.message : '发送失败'
  } finally {
    sendingCode.value = false
  }
}
async function submit() {
  loading.value = true
  error.value = ''
  try {
    if (mode.value === 'login') {
      await session.login({
        loginType: method.value,
        username: form.value.username,
        password: form.value.password,
        email: form.value.email,
        code: form.value.code,
      })
      const redirect = String(route.query.redirect || '/')
      await router.push(redirect.startsWith('/') && !redirect.startsWith('//') ? redirect : '/')
    } else {
      await authApi.register({
        registerType: 2,
        name: form.value.name,
        password: form.value.password,
        email: form.value.email,
        code: form.value.code,
        studentNo: form.value.studentNo,
        grade: form.value.grade,
        major: form.value.major,
        enrollmentYear: form.value.enrollmentYear,
        school: form.value.school,
      })
      mode.value = 'login'
      notice.value = '注册成功，请登录'
    }
  } catch (e) {
    error.value = e instanceof Error ? e.message : '操作失败'
  } finally {
    loading.value = false
  }
}
onBeforeUnmount(() => window.clearInterval(cooldownTimer))
</script>
<template>
  <div class="grid min-h-screen bg-[#f4f5ef] lg:grid-cols-[1.05fr_.95fr]">
    <section class="relative hidden overflow-hidden bg-ink p-12 text-white lg:flex lg:flex-col">
      <div
        class="absolute -right-32 -top-28 size-[420px] rounded-full bg-[#637a39] opacity-50 blur-3xl"
      />
      <BrandMark class="relative z-10 brightness-0 invert" />
      <div class="relative z-10 my-auto max-w-xl">
        <p class="eyebrow text-lime">INTELLI EDU</p>
        <h1 class="mt-6 text-6xl font-black leading-[1.08] tracking-[-0.055em]">
          让教学更清晰，<br /><span class="text-lime">让学习更专注。</span>
        </h1>
        <p class="mt-7 max-w-md leading-7 text-white/60">
          从课程学习到在线考试，所有学习进度尽在一个空间。
        </p>
      </div>
    </section>
    <section class="flex items-center justify-center p-6 sm:p-10">
      <div class="w-full max-w-md">
        <BrandMark class="mb-10 lg:hidden" />
        <h2 class="text-4xl font-black">
          {{ mode === 'login' ? '登录平台' : '注册学生账号' }}
        </h2>
        <div class="mt-7 flex rounded-2xl bg-[#e9ebe5] p-1">
          <button
            v-for="item in modes"
            :key="item.value"
            :class="[
              'flex-1 rounded-xl py-2.5 text-sm font-bold',
              mode === item.value ? 'bg-white shadow-sm' : 'text-[#7c857f]',
            ]"
            @click="selectMode(item.value)"
          >
            {{ item.label }}
          </button>
        </div>
        <div v-if="mode === 'login'" class="mt-5 flex gap-2">
          <button
            v-for="item in loginMethods"
            :key="item.value"
            :class="[
              'rounded-full px-3 py-2 text-xs font-bold',
              method === item.value ? 'bg-ink text-white' : 'border bg-white',
            ]"
            @click="selectMethod(item.value)"
          >
            {{ item.label }}
          </button>
        </div>
        <form class="mt-6 space-y-4" @submit.prevent="submit">
          <template v-if="mode === 'register'"
            ><input v-model="form.name" class="field" placeholder="姓名" required />
            <div class="grid grid-cols-2 gap-3">
              <input v-model="form.studentNo" class="field" placeholder="学号" required /><input
                v-model="form.grade"
                class="field"
                placeholder="年级"
                required
              />
            </div>
            <div class="grid grid-cols-2 gap-3">
              <input v-model="form.major" class="field" placeholder="专业" required /><input
                v-model.number="form.enrollmentYear"
                type="number"
                class="field"
                placeholder="入学年份"
                required
              />
            </div>
            <input v-model="form.school" class="field" placeholder="学校" required /></template
          ><input
            v-if="method === 4"
            v-model="form.username"
            class="field"
            :placeholder="accountLabel"
            required
          /><input
            v-else
            v-model="form.email"
            type="email"
            class="field"
            :placeholder="accountLabel"
            required
          />
          <div v-if="method !== 4" class="flex gap-2">
            <input v-model="form.code" class="field" placeholder="验证码" required /><button
              type="button"
              class="btn-secondary shrink-0"
              :disabled="sendingCode || cooldown > 0"
              @click="sendCode"
            >
              {{ sendingCode ? '发送中…' : cooldown > 0 ? `${cooldown}s 后重试` : '获取验证码' }}
            </button>
          </div>
          <span v-if="method === 4 || mode === 'register'" class="relative block"
            ><input
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              class="field pr-12"
              placeholder="密码"
              minlength="6"
              maxlength="20"
              pattern="(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&amp;*()_+...]).{6,20}"
              title="需包含字母、数字和特殊字符，长度 6–20 位"
              :autocomplete="mode === 'login' ? 'current-password' : 'new-password'"
              required /><button
              type="button"
              class="absolute right-4 top-1/2 -translate-y-1/2"
              @click="showPassword = !showPassword"
            >
              <EyeOff v-if="showPassword" :size="18" /><Eye v-else :size="18" /></button
          ></span>
          <p v-if="error" class="rounded-xl bg-red-50 px-3 py-2 text-xs text-red-600">
            {{ error }}
          </p>
          <p v-if="notice" class="rounded-xl bg-green-50 px-3 py-2 text-xs text-green-700">
            {{ notice }}
          </p>
          <button class="btn-primary w-full" :disabled="loading">
            {{ loading ? '请稍候…' : mode === 'login' ? '登录' : '注册' }}<ArrowRight :size="17" />
          </button>
        </form>
        <p class="mt-5 flex items-center justify-center gap-2 text-xs text-[#8a928c]">
          <ShieldCheck :size="15" />课程学习 · 在线考试 · 学情分析
        </p>
      </div>
    </section>
  </div>
</template>
