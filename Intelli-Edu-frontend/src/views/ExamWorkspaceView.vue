<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft, CheckCircle2, Clock3, Save } from 'lucide-vue-next'
import { examApi } from '@/api/services'
import { useUiStore } from '@/stores/ui'
import type { AnswerRecord, AnswerSheet } from '@/types'

const route = useRoute()
const router = useRouter()
const ui = useUiStore()
const sheet = ref<AnswerSheet | null>(null)
const answers = ref<Record<string, string>>({})
const loading = ref(true)
const error = ref('')
const saving = ref<string | number | null>(null)
const submitting = ref(false)
const now = ref(Date.now())
let clock: number | undefined
const dirtyQuestions = new Set<string>()
const saveTimers = new Map<string, number>()

const readonly = computed(() => sheet.value?.status !== 0)
const answered = computed(
  () =>
    sheet.value?.records?.filter((record) => answers.value[record.questionId]?.trim()).length || 0,
)
const remainingSeconds = computed(() => {
  if (!sheet.value?.deadline) return null
  return Math.max(0, Math.floor((new Date(sheet.value.deadline).getTime() - now.value) / 1000))
})
const remainingText = computed(() => {
  const seconds = remainingSeconds.value
  if (seconds === null) return '不限时'
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const rest = seconds % 60
  return `${String(hours).padStart(2, '0')}:${String(minutes).padStart(2, '0')}:${String(rest).padStart(2, '0')}`
})

function questionTypeLabel(type?: number) {
  return ['单选题', '多选题', '判断题', '填空题', '简答题'][type ?? -1] || '题目'
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const examId = String(route.params.id || '')
    if (!/^\d+$/.test(examId)) throw new Error('无效的考试编号')
    await examApi.enter(examId)
    sheet.value = await examApi.mySheet(examId)
    answers.value = Object.fromEntries(
      (sheet.value.records || []).map((record) => [record.questionId, record.answerContent || '']),
    )
  } catch (e) {
    error.value = e instanceof Error ? e.message : '答卷加载失败'
  } finally {
    loading.value = false
  }
}

async function save(record: AnswerRecord) {
  if (!sheet.value || readonly.value) return
  const key = String(record.questionId)
  if (saving.value === record.questionId) {
    window.clearTimeout(saveTimers.get(key))
    saveTimers.set(
      key,
      window.setTimeout(() => save(record), 500),
    )
    return
  }
  window.clearTimeout(saveTimers.get(key))
  saveTimers.delete(key)
  saving.value = record.questionId
  try {
    await examApi.saveAnswer(
      sheet.value.sheetId,
      record.questionId,
      answers.value[record.questionId] || '',
    )
    dirtyQuestions.delete(key)
    ui.notify(`第 ${(sheet.value.records || []).indexOf(record) + 1} 题已保存`, 'success')
  } catch (e) {
    ui.notify(e instanceof Error ? e.message : '答案保存失败', 'error')
  } finally {
    saving.value = null
  }
}

function queueSave(record: AnswerRecord) {
  if (readonly.value) return
  const key = String(record.questionId)
  dirtyQuestions.add(key)
  window.clearTimeout(saveTimers.get(key))
  saveTimers.set(
    key,
    window.setTimeout(() => save(record), 1200),
  )
}

function warnBeforeUnload(event: BeforeUnloadEvent) {
  if (!dirtyQuestions.size) return
  event.preventDefault()
}

async function submit() {
  if (!sheet.value) return
  const total = sheet.value.records?.length || 0
  const unanswered = total - answered.value
  const confirmed = await ui.confirm({
    title: '确认交卷',
    message: unanswered
      ? `还有 ${unanswered} 道题未作答。交卷后普通考试不能继续修改，确定提交吗？`
      : '交卷后普通考试不能继续修改，确定提交吗？',
    confirmLabel: '确认交卷',
    danger: unanswered > 0,
  })
  if (!confirmed) return
  submitting.value = true
  try {
    for (const record of sheet.value.records || []) {
      await examApi.saveAnswer(
        sheet.value.sheetId,
        record.questionId,
        answers.value[record.questionId] || '',
      )
      dirtyQuestions.delete(String(record.questionId))
    }
    await examApi.submit(sheet.value.sheetId)
    ui.notify('答卷已提交', 'success')
    await router.replace('/exams')
  } catch (e) {
    ui.notify(e instanceof Error ? e.message : '交卷失败', 'error')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  load()
  clock = window.setInterval(() => (now.value = Date.now()), 1000)
  window.addEventListener('beforeunload', warnBeforeUnload)
})
onBeforeUnmount(() => {
  window.clearInterval(clock)
  window.removeEventListener('beforeunload', warnBeforeUnload)
  saveTimers.forEach((timer) => window.clearTimeout(timer))
})
</script>

<template>
  <div class="mx-auto max-w-5xl">
    <button class="btn-secondary mb-5 py-2.5" @click="router.push('/exams')">
      <ArrowLeft :size="16" />返回考试列表
    </button>
    <div v-if="loading" class="card h-96 animate-pulse" />
    <div v-else-if="error" class="card py-20 text-center">
      <p class="font-bold text-red-600">{{ error }}</p>
      <button class="btn-secondary mt-4" @click="load">重新加载</button>
    </div>
    <template v-else-if="sheet">
      <header class="card sticky top-[92px] z-20 flex flex-wrap items-center gap-4 p-5">
        <div class="min-w-0 flex-1">
          <p class="text-xs font-bold text-[#7d857f]">在线答题</p>
          <h2 class="mt-1 truncate text-xl font-black">
            {{ sheet.examName || `考试 #${sheet.examId}` }}
          </h2>
        </div>
        <div class="flex items-center gap-2 text-sm font-bold">
          <Clock3 :size="17" />{{ remainingText }}
        </div>
        <span class="pill bg-[#eef4e5] text-leaf"
          >{{ answered }} / {{ sheet.records?.length || 0 }} 已作答</span
        >
        <button v-if="!readonly" class="btn-primary" :disabled="submitting" @click="submit">
          <CheckCircle2 :size="16" />{{ submitting ? '提交中…' : '交卷' }}
        </button>
      </header>

      <div class="mt-5 space-y-4">
        <article
          v-for="(record, index) in sheet.records"
          :key="record.questionId"
          class="card p-6 sm:p-8"
        >
          <div class="flex flex-wrap items-center gap-2">
            <span
              class="grid size-8 place-items-center rounded-xl bg-ink text-xs font-black text-white"
              >{{ index + 1 }}</span
            >
            <span class="pill bg-[#f0f2ed]">{{ questionTypeLabel(record.questionType) }}</span>
            <span class="ml-auto text-xs font-bold text-[#7d857f]"
              >{{ record.questionScore ?? 0 }} 分</span
            >
          </div>
          <p class="mt-5 whitespace-pre-wrap text-base font-semibold leading-7">
            {{ record.stem || `题目 #${record.questionId}` }}
          </p>
          <textarea
            v-model="answers[record.questionId]"
            class="field mt-5 min-h-28 resize-y"
            :disabled="readonly"
            :placeholder="readonly ? '未作答' : '请输入答案，离开输入框时自动保存'"
            @input="queueSave(record)"
            @blur="save(record)"
          />
          <div class="mt-3 flex items-center justify-between text-xs text-[#8b938d]">
            <span v-if="saving === record.questionId" class="inline-flex items-center gap-1"
              ><Save :size="13" />正在保存…</span
            >
            <span v-else>{{ readonly ? '答卷已提交' : '答案将在离开输入框时保存' }}</span>
            <span v-if="readonly && record.score !== undefined">得分：{{ record.score }}</span>
          </div>
          <p v-if="readonly && record.comment" class="mt-3 rounded-xl bg-[#f5f6f1] p-3 text-sm">
            评语：{{ record.comment }}
          </p>
        </article>
        <div v-if="!sheet.records?.length" class="card py-20 text-center text-sm text-[#8b938d]">
          该试卷暂未配置题目
        </div>
      </div>
    </template>
  </div>
</template>
