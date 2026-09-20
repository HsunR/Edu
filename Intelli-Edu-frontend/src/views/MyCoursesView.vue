<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { LogOut, Plus, Users } from 'lucide-vue-next'
import { courseApi } from '@/api/services'
import { useUiStore } from '@/stores/ui'
import type { CourseClass } from '@/types'
const ui = useUiStore()
const classes = ref<CourseClass[]>([])
const loading = ref(true)
const error = ref('')
const showJoin = ref(false)
const inviteCode = ref('')
const saving = ref(false)
async function load() {
  loading.value = true
  error.value = ''
  try {
    classes.value = await courseApi.mine()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}
async function join() {
  saving.value = true
  error.value = ''
  try {
    await courseApi.join(inviteCode.value)
    showJoin.value = false
    inviteCode.value = ''
    await load()
    ui.notify('已加入班级', 'success')
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加入失败'
  } finally {
    saving.value = false
  }
}
async function quit(item: CourseClass) {
  const confirmed = await ui.confirm({
    title: '退出班级',
    message: `确定退出“${item.className}”吗？退出后可能无法继续访问班级课程与考试。`,
    confirmLabel: '确认退出',
    danger: true,
  })
  if (!confirmed) return
  try {
    await courseApi.quit(item.classId)
    await load()
    ui.notify('已退出班级', 'success')
  } catch (e) {
    error.value = e instanceof Error ? e.message : '退出失败'
  }
}
onMounted(load)
</script>
<template>
  <div>
    <div class="flex items-end justify-between">
      <div>
        <h2 class="text-3xl font-black">我的班级</h2>
        <p class="mt-2 text-sm text-[#7d857f]">查看已加入的班级，进入课程或管理入班状态。</p>
      </div>
      <button class="btn-primary" @click="showJoin = true"><Plus :size="17" />加入班级</button>
    </div>
    <p v-if="error" class="mt-5 rounded-2xl bg-red-50 p-4 text-sm text-red-600">{{ error }}</p>
    <div v-if="loading" class="mt-7 grid gap-5 md:grid-cols-2">
      <div v-for="i in 4" :key="i" class="h-48 animate-pulse rounded-[26px] bg-white" />
    </div>
    <div v-else-if="classes.length" class="mt-7 grid gap-5 md:grid-cols-2 xl:grid-cols-3">
      <article v-for="item in classes" :key="item.classId" class="card p-6">
        <div class="flex items-start justify-between">
          <span class="pill bg-[#eaf2dd] text-leaf">{{
            ['招生中', '进行中', '已结束'][item.status] || '未知'
          }}</span
          ><button class="icon-btn size-8 text-red-500" @click="quit(item)">
            <LogOut :size="14" />
          </button>
        </div>
        <h3 class="mt-5 text-xl font-black">{{ item.courseName }}</h3>
        <p class="mt-1 text-sm text-[#747d77]">{{ item.className }} · {{ item.teacherName }}</p>
        <div class="mt-6 flex items-center gap-2 text-xs text-[#7e8781]">
          <Users :size="15" />{{ item.currentStudents }} / {{ item.maxStudents }} 人
        </div>
        <RouterLink :to="`/courses/${item.courseId}`" class="btn-secondary mt-5 w-full py-2.5"
          >查看课程</RouterLink
        >
      </article>
    </div>
    <div v-else-if="!loading" class="card mt-7 py-20 text-center">
      <p class="font-bold">尚未加入班级</p>
      <p class="mt-1 text-sm text-[#89918c]">使用教师提供的邀请码加入</p>
    </div>
    <Transition name="fade"
      ><div
        v-if="showJoin"
        class="fixed inset-0 z-50 grid place-items-center bg-ink/35 p-5 backdrop-blur-sm"
        @click.self="showJoin = false"
      >
        <form class="card w-full max-w-md p-7" @submit.prevent="join">
          <h3 class="text-xl font-black">加入班级</h3>
          <p class="mt-2 text-sm text-[#7b847e]">请输入班级邀请码</p>
          <input
            v-model.trim="inviteCode"
            class="field mt-5 uppercase"
            required
            maxlength="20"
            autocomplete="off"
            placeholder="例如：A8K2Q6"
          />
          <div class="mt-5 flex justify-end gap-2">
            <button type="button" class="btn-secondary" @click="showJoin = false">取消</button
            ><button class="btn-primary" :disabled="saving">
              {{ saving ? '加入中…' : '确认加入' }}
            </button>
          </div>
        </form>
      </div></Transition
    >
  </div>
</template>
