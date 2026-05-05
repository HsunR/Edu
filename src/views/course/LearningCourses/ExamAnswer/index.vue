<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useExamStore } from '@/stores/exam'
import { QuestionType, SheetStatus } from '@/types/enums'

const route = useRoute()
const router = useRouter()
const examStore = useExamStore()
const examId = route.params.examId as string

const loading = ref(false)
const currentQuestionIndex = ref(0)
const autoSaveTimer = ref<ReturnType<typeof setInterval> | null>(null)

const sheet = computed(() => examStore.currentSheet)
const records = computed(() => sheet.value?.records || [])
const currentRecord = computed(() => records.value[currentQuestionIndex.value])
const remainingTime = computed(() => {
  const s = examStore.remainingSeconds
  const h = Math.floor(s / 3600)
  const m = Math.floor((s % 3600) / 60)
  const sec = s % 60
  return `${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}:${sec.toString().padStart(2, '0')}`
})

const questionTypeMap: Record<number, string> = {
  [QuestionType.SingleChoice]: '单选题',
  [QuestionType.MultipleChoice]: '多选题',
  [QuestionType.TrueFalse]: '判断题',
  [QuestionType.FillBlank]: '填空题',
  [QuestionType.ShortAnswer]: '简答题'
}

const isAnswering = computed(() => sheet.value?.status === 'IN_PROGRESS')
const isEnded = computed(() => sheet.value?.status === 'ENDED' || sheet.value?.status === 'GRADED')

async function initExam() {
  loading.value = true
  try {
    await examStore.fetchMySheet(examId)
    if (!sheet.value || sheet.value?.status === 'NOT_STARTED') {
      await examStore.enterExamAction(examId)
    }
  } catch (error) {
    const msg = error instanceof Error ? error.message : '进入考试失败'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
  startAutoSave()
}

function startAutoSave() {
  stopAutoSave()
  autoSaveTimer.value = setInterval(() => {
    if (!sheet.value || !isAnswering.value) return
    for (const record of records.value) {
      const savedAnswer = examStore.answers.get(record.questionId)
      if (savedAnswer) {
        examStore.saveAnswerAction(sheet.value.sheetId, record.questionId, savedAnswer).catch(() => {})
      }
    }
  }, 30000)
}

function stopAutoSave() {
  if (autoSaveTimer.value) {
    clearInterval(autoSaveTimer.value)
    autoSaveTimer.value = null
  }
}

function getAnswer(questionId: string): string {
  return examStore.answers.get(questionId) || ''
}

function setAnswer(questionId: string, value: string) {
  examStore.answers.set(questionId, value)
  if (sheet.value && isAnswering.value) {
    examStore.saveAnswerAction(sheet.value.sheetId, questionId, value).catch(() => {})
  }
}

function getMultiAnswer(questionId: string): string[] {
  const ans = examStore.answers.get(questionId) || ''
  return ans ? ans.split(',') : []
}

function setMultiAnswer(questionId: string, values: string[]) {
  setAnswer(questionId, values.sort().join(','))
}

function isOptionSelected(questionId: string, label: string): boolean {
  return getMultiAnswer(questionId).includes(label)
}

function toggleMultiOption(questionId: string, label: string) {
  const current = getMultiAnswer(questionId)
  const idx = current.indexOf(label)
  if (idx >= 0) {
    current.splice(idx, 1)
  } else {
    current.push(label)
  }
  setMultiAnswer(questionId, current)
}

function goToQuestion(index: number) {
  currentQuestionIndex.value = index
}

function prevQuestion() {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

function nextQuestion() {
  if (currentQuestionIndex.value < records.value.length - 1) {
    currentQuestionIndex.value++
  }
}

async function handleSubmit() {
  if (!sheet.value) return
  try {
    await ElMessageBox.confirm('确认交卷吗？交卷后不可修改答案。', '交卷确认', {
      confirmButtonText: '确认交卷',
      cancelButtonText: '继续答题',
      type: 'warning'
    })
    await examStore.submitExamAction(sheet.value.sheetId)
    ElMessage.success('交卷成功')
  } catch {
    // cancelled
  }
}

onMounted(initExam)
onUnmounted(() => {
  stopAutoSave()
  examStore.clearExamState()
})
</script>

<template>
  <div v-loading="loading" class="exam-answer">
    <template v-if="sheet">
      <div class="answer-header">
        <h2 class="exam-title">{{ sheet.examName }}</h2>
        <div v-if="isAnswering" class="countdown">
          <el-tag type="danger" size="large" effect="dark">
            剩余时间：{{ remainingTime }}
          </el-tag>
        </div>
        <div v-if="isEnded" class="result-info">
          <el-tag type="success" size="large">已交卷</el-tag>
          <span v-if="sheet.totalScore > 0" style="margin-left: 12px; font-size: 16px; font-weight: 600">
            总分：{{ sheet.totalScore }}
          </span>
        </div>
      </div>

      <div class="answer-body">
        <div class="question-nav">
          <div class="nav-title">题目导航</div>
          <div class="nav-grid">
            <div
              v-for="(record, idx) in records"
              :key="record.recordId"
              class="nav-item"
              :class="{
                active: idx === currentQuestionIndex,
                answered: examStore.answers.has(record.questionId)
              }"
              @click="goToQuestion(idx)"
            >
              {{ idx + 1 }}
            </div>
          </div>
          <div class="nav-legend">
            <span class="legend-item"><span class="dot answered"></span>已答</span>
            <span class="legend-item"><span class="dot"></span>未答</span>
          </div>
          <div class="nav-progress">
            已答 {{ examStore.answeredCount }} / {{ records.length }}
          </div>
          <el-button
            v-if="isAnswering"
            type="danger"
            style="width: 100%; margin-top: 12px"
            @click="handleSubmit"
          >
            交卷
          </el-button>
        </div>

        <div class="question-area">
          <template v-if="currentRecord">
            <div class="question-header">
              <span class="question-index">第 {{ currentQuestionIndex + 1 }} 题</span>
              <el-tag size="small">{{ questionTypeMap[currentRecord.questionType] }}</el-tag>
              <span class="question-score">分值：{{ currentRecord.questionScore }}</span>
            </div>

            <div class="question-stem">{{ currentRecord.stem }}</div>

            <div class="question-answer">
              <template v-if="currentRecord.questionType === QuestionType.SingleChoice">
                <el-radio-group
                  :model-value="getAnswer(currentRecord.questionId)"
                  @change="(val: string) => setAnswer(currentRecord.questionId, val)"
                  :disabled="isEnded"
                >
                  <el-radio
                    v-for="label in ['A', 'B', 'C', 'D', 'E', 'F']"
                    :key="label"
                    :value="label"
                    style="display: flex; margin-bottom: 8px"
                  >
                    {{ label }}
                  </el-radio>
                </el-radio-group>
              </template>

              <template v-if="currentRecord.questionType === QuestionType.MultipleChoice">
                <div v-for="label in ['A', 'B', 'C', 'D', 'E', 'F']" :key="label" style="margin-bottom: 8px">
                  <el-checkbox
                    :model-value="isOptionSelected(currentRecord.questionId, label)"
                    :disabled="isEnded"
                    @change="toggleMultiOption(currentRecord.questionId, label)"
                  >
                    {{ label }}
                  </el-checkbox>
                </div>
              </template>

              <template v-if="currentRecord.questionType === QuestionType.TrueFalse">
                <el-radio-group
                  :model-value="getAnswer(currentRecord.questionId)"
                  @change="(val: string) => setAnswer(currentRecord.questionId, val)"
                  :disabled="isEnded"
                >
                  <el-radio value="正确">正确</el-radio>
                  <el-radio value="错误">错误</el-radio>
                </el-radio-group>
              </template>

              <template v-if="currentRecord.questionType === QuestionType.FillBlank">
                <el-input
                  :model-value="getAnswer(currentRecord.questionId)"
                  @input="(val: string) => setAnswer(currentRecord.questionId, val)"
                  :disabled="isEnded"
                  placeholder="请输入答案"
                />
              </template>

              <template v-if="currentRecord.questionType === QuestionType.ShortAnswer">
                <el-input
                  :model-value="getAnswer(currentRecord.questionId)"
                  @input="(val: string) => setAnswer(currentRecord.questionId, val)"
                  :disabled="isEnded"
                  type="textarea"
                  :rows="6"
                  placeholder="请输入答案"
                />
              </template>
            </div>

            <template v-if="isEnded">
              <el-divider />
              <div class="question-result">
                <div>正确答案：{{ currentRecord.correctAnswer }}</div>
                <div>我的答案：{{ currentRecord.answerContent || '未作答' }}</div>
                <div>得分：{{ currentRecord.score }}</div>
                <el-tag :type="currentRecord.isCorrect ? 'success' : 'danger'" size="small">
                  {{ currentRecord.isCorrect ? '正确' : '错误' }}
                </el-tag>
              </div>
            </template>

            <div class="question-nav-buttons">
              <el-button :disabled="currentQuestionIndex === 0" @click="prevQuestion">上一题</el-button>
              <el-button :disabled="currentQuestionIndex === records.length - 1" @click="nextQuestion">下一题</el-button>
            </div>
          </template>

          <el-empty v-else description="暂无题目" />
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped lang="scss">
.exam-answer {
  padding: 20px;
}

.answer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .exam-title {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }
}

.answer-body {
  display: flex;
  gap: 16px;
}

.question-nav {
  width: 200px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 8px;
  padding: 16px;

  .nav-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
  }

  .nav-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 6px;
    margin-bottom: 12px;
  }

  .nav-item {
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    font-size: 13px;
    color: #606266;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      border-color: #409eff;
      color: #409eff;
    }

    &.active {
      background: #409eff;
      color: #fff;
      border-color: #409eff;
    }

    &.answered {
      background: #e6f7ff;
      border-color: #91d5ff;
    }

    &.answered.active {
      background: #409eff;
      color: #fff;
    }
  }

  .nav-legend {
    display: flex;
    gap: 12px;
    margin-bottom: 8px;

    .legend-item {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #909399;
    }

    .dot {
      width: 10px;
      height: 10px;
      border: 1px solid #dcdfe6;
      border-radius: 2px;

      &.answered {
        background: #e6f7ff;
        border-color: #91d5ff;
      }
    }
  }

  .nav-progress {
    font-size: 13px;
    color: #606266;
  }
}

.question-area {
  flex: 1;
  min-width: 0;
  background: #fff;
  border-radius: 8px;
  padding: 20px;
}

.question-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;

  .question-index {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  .question-score {
    margin-left: auto;
    font-size: 13px;
    color: #909399;
  }
}

.question-stem {
  font-size: 15px;
  color: #303133;
  line-height: 1.8;
  margin-bottom: 20px;
  white-space: pre-wrap;
}

.question-answer {
  padding: 0 16px;
}

.question-result {
  font-size: 14px;
  color: #606266;
  line-height: 2;
}

.question-nav-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
