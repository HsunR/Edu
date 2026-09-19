<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ArrowRight, ClipboardList, RefreshCw, Timer } from 'lucide-vue-next'
import { examApi } from '@/api/services'
import { useSessionStore } from '@/stores/session'
import type { Exam } from '@/types'
const session = useSessionStore()
const exams = ref<Exam[]>([])
const loading = ref(true)
const error = ref('')
const status = ref<number | undefined>()
const entering = ref<number | null>(null)
const statuses = [
  { value: undefined, label: '全部' },
  { value: 0, label: '未开始' },
  { value: 1, label: '进行中' },
  { value: 2, label: '已结束' },
  { value: 3, label: '已批阅' },
] as const
const visible = computed(() =>
  status.value === undefined ? exams.value : exams.value.filter((e) => e.status === status.value),
)
async function load() {
  loading.value = true
  error.value = ''
  try {
    exams.value = (await examApi.list({ current: 1, pageSize: 100, status: status.value })).records
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}
async function enter(exam: Exam) {
  entering.value = exam.examId
  try {
    await examApi.enter(exam.examId)
    alert('已成功进入考试，答卷已创建。')
  } catch (e) {
    error.value = e instanceof Error ? e.message : '进入考试失败'
  } finally {
    entering.value = null
  }
}

function selectStatus(nextStatus: number | undefined) {
  status.value = nextStatus
  load()
}

onMounted(load)
</script>
<template>
  <div>
    <section class="rounded-[32px] bg-[#ece6ff] p-8">
      <ClipboardList :size="28" class="text-violet-700" />
      <h2 class="mt-7 text-3xl font-black">考试与作业</h2>
      <p class="mt-2 text-sm text-[#716b7e]">列表来自考试服务，支持学生创建答卷。</p>
    </section>
    <div class="mt-6 flex flex-wrap gap-2">
      <button
        v-for="item in statuses"
        :key="item.label"
        :class="[
          'rounded-full px-4 py-2 text-xs font-bold',
          status === item.value ? 'bg-ink text-white' : 'border bg-white',
        ]"
        @click="selectStatus(item.value)"
      >
        {{ item.label }}</button
      ><button class="icon-btn ml-auto" @click="load"><RefreshCw :size="16" /></button>
    </div>
    <p v-if="error" class="mt-5 rounded-2xl bg-red-50 p-4 text-sm text-red-600">{{ error }}</p>
    <div v-if="loading" class="mt-5 space-y-3">
      <div v-for="i in 4" :key="i" class="h-24 animate-pulse rounded-3xl bg-white" />
    </div>
    <div v-else class="mt-5 space-y-3">
      <article
        v-for="exam in visible"
        :key="exam.examId"
        class="card flex flex-col gap-4 p-5 sm:flex-row sm:items-center"
      >
        <span
          class="grid size-12 shrink-0 place-items-center rounded-2xl bg-violet-50 text-violet-600"
          ><Timer :size="21"
        /></span>
        <div class="min-w-0 flex-1">
          <div class="flex flex-wrap items-center gap-2">
            <h3 class="font-extrabold">{{ exam.examName }}</h3>
            <span class="pill bg-[#f0f2ed] py-1">{{ ['考试', '练习', '作业'][exam.examType] }}</span
            ><span class="pill bg-[#eef4e5] py-1 text-leaf">{{
              ['未开始', '进行中', '已结束', '已批阅'][exam.status]
            }}</span>
          </div>
          <p class="mt-1 text-xs text-[#8b938d]">
            {{ new Date(exam.startTime).toLocaleString() }} —
            {{ new Date(exam.endTime).toLocaleString() }} · {{ exam.durationMinutes }} 分钟
          </p>
        </div>
        <button
          v-if="session.role === 'Student' && exam.status === 1"
          class="btn-primary py-2.5"
          :disabled="entering === exam.examId"
          @click="enter(exam)"
        >
          {{ entering === exam.examId ? '进入中…' : '进入考试' }}<ArrowRight :size="15" />
        </button>
      </article>
      <div v-if="!visible.length" class="card py-16 text-center text-sm text-[#8b938d]">
        暂无考试或作业
      </div>
    </div>
  </div>
</template>
