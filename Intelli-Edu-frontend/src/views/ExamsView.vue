<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, ClipboardList, RefreshCw, Timer } from 'lucide-vue-next'
import { examApi } from '@/api/services'
import { useSessionStore } from '@/stores/session'
import type { Exam } from '@/types'
import AppPagination from '@/components/AppPagination.vue'

const router = useRouter()
const session = useSessionStore()
const exams = ref<Exam[]>([])
const loading = ref(true)
const error = ref('')
const status = ref<number | undefined>()
const page = ref(1)
const pageSize = 10
const total = ref(0)
const statuses = [
  { value: undefined, label: '全部' },
  { value: 0, label: '未开始' },
  { value: 1, label: '进行中' },
  { value: 2, label: '已结束' },
  { value: 3, label: '已批阅' },
] as const

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await examApi.list({ current: page.value, pageSize, status: status.value })
    exams.value = result.records
    total.value = result.total
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}

function selectStatus(nextStatus: number | undefined) {
  status.value = nextStatus
  page.value = 1
  load()
}

function changePage(nextPage: number) {
  page.value = nextPage
  load()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function enter(exam: Exam) {
  router.push({ name: 'exam-workspace', params: { id: exam.examId } })
}

onMounted(load)
</script>

<template>
  <div>
    <section class="rounded-[32px] bg-[#ece6ff] p-8">
      <ClipboardList :size="28" class="text-violet-700" />
      <h2 class="mt-7 text-3xl font-black">考试与作业</h2>
      <p class="mt-2 text-sm text-[#716b7e]">
        {{
          session.role === 'Student' ? '查看考试安排并在线完成答卷。' : '查看已发布的考试与作业。'
        }}
      </p>
    </section>
    <div class="mt-6 flex flex-wrap gap-2">
      <button
        v-for="item in statuses"
        :key="item.label"
        :class="[
          'rounded-full px-4 py-2 text-xs font-bold transition',
          status === item.value ? 'bg-ink text-white' : 'border bg-white hover:border-[#b8c1b5]',
        ]"
        :aria-pressed="status === item.value"
        @click="selectStatus(item.value)"
      >
        {{ item.label }}
      </button>
      <button class="icon-btn ml-auto" :disabled="loading" aria-label="刷新" @click="load">
        <RefreshCw :class="loading ? 'animate-spin' : ''" :size="16" />
      </button>
    </div>
    <p v-if="error" class="mt-5 rounded-2xl bg-red-50 p-4 text-sm text-red-600">{{ error }}</p>
    <div v-if="loading" class="mt-5 space-y-3" aria-label="正在加载">
      <div v-for="i in 4" :key="i" class="h-24 animate-pulse rounded-3xl bg-white" />
    </div>
    <div v-else class="mt-5 space-y-3">
      <article
        v-for="exam in exams"
        :key="exam.examId"
        class="card flex flex-col gap-4 p-5 sm:flex-row sm:items-center"
      >
        <span
          class="grid size-12 shrink-0 place-items-center rounded-2xl bg-violet-50 text-violet-600"
        >
          <Timer :size="21" />
        </span>
        <div class="min-w-0 flex-1">
          <div class="flex flex-wrap items-center gap-2">
            <h3 class="font-extrabold">{{ exam.examName }}</h3>
            <span class="pill bg-[#f0f2ed] py-1">{{
              ['考试', '练习', '作业'][exam.examType]
            }}</span>
            <span
              :class="['pill py-1', exam.status === 1 ? 'bg-[#eef4e5] text-leaf' : 'bg-[#f0f2ed]']"
            >
              {{ ['未开始', '进行中', '已结束', '已批阅'][exam.status] || '未知状态' }}
            </span>
          </div>
          <p class="mt-1 text-xs text-[#8b938d]">
            {{ new Date(exam.startTime).toLocaleString() }} —
            {{ new Date(exam.endTime).toLocaleString() }} · {{ exam.durationMinutes }} 分钟
          </p>
          <p v-if="exam.paperName" class="mt-1 text-xs text-[#8b938d]">
            试卷：{{ exam.paperName }}
          </p>
        </div>
        <button
          v-if="session.role === 'Student' && exam.status === 1"
          class="btn-primary py-2.5"
          @click="enter(exam)"
        >
          进入答题 <ArrowRight :size="15" />
        </button>
      </article>
      <div v-if="!exams.length" class="card py-16 text-center text-sm text-[#8b938d]">
        当前筛选条件下暂无考试或作业
      </div>
    </div>
    <AppPagination
      :page="page"
      :page-size="pageSize"
      :total="total"
      :disabled="loading"
      @change="changePage"
    />
  </div>
</template>
