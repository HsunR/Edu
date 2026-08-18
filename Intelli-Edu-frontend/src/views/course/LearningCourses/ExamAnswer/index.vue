<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { useExamStore } from '@/stores/exam'
import { QuestionType, SheetStatus, ExamType, GradingStatus } from '@/types/enums'
import { getExamList, getPaperDetail } from '@/api/exam/index'
import type { ExamVO, PaperDetailVO, QuestionOptionVO } from '@/api/exam/types'

interface RenderQuestion {
  questionId: string
  questionType: QuestionType
  stem: string
  options: QuestionOptionVO[]
  score: number
  correctAnswer: string
  sectionIndex: number
  orderIndex: number
  recordId: string
  answerContent: string
  studentScore: number
  isCorrect: boolean | null
  gradingStatus: number
  comment: string
}

const route = useRoute()
const router = useRouter()
const examStore = useExamStore()
const examId = route.params.examId as string
const courseId = route.params.id as string

const loading = ref(false)
const currentQuestionIndex = ref(0)
const autoSaveTimer = ref<ReturnType<typeof setInterval> | null>(null)
const examInfo = ref<ExamVO | null>(null)
const paperDetail = ref<PaperDetailVO | null>(null)
const errorMessage = ref('')

const sheet = computed(() => examStore.currentSheet)
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

const questionTypeTagType: Record<number, 'primary' | 'success' | 'warning' | 'info' | 'danger'> = {
  [QuestionType.SingleChoice]: 'primary',
  [QuestionType.MultipleChoice]: 'success',
  [QuestionType.TrueFalse]: 'warning',
  [QuestionType.FillBlank]: 'info',
  [QuestionType.ShortAnswer]: 'danger'
}

const isHomework = computed(() => examInfo.value?.examType === ExamType.Homework)
const isAnswering = computed(() => sheet.value?.status === SheetStatus.Answering)
const isEnded = computed(() => sheet.value?.status === SheetStatus.Submitted || sheet.value?.status === SheetStatus.Graded)
const isGraded = computed(() => sheet.value?.status === SheetStatus.Graded)
const canReEnter = computed(() => isHomework.value && sheet.value?.status === SheetStatus.Submitted)

const sections = computed(() => paperDetail.value?.sections || [])
const questionCount = computed(() => paperDetail.value?.questionCount || 0)

function getEffectiveOptions(pq: any): QuestionOptionVO[] {
  let rawOptions: any[] = []
  if (pq.question?.options?.length) {
    rawOptions = pq.question.options
  } else if (pq.questionSnapshot?.options?.length) {
    rawOptions = pq.questionSnapshot.options.map((o: any) => ({
      optionId: o.optionId || '',
      label: o.label,
      content: o.content,
      isCorrect: o.isCorrect || o.is_correct,
      orderIndex: o.orderIndex ?? o.order_index ?? 0
    }))
  }
  return [...rawOptions].sort((a, b) => (a.orderIndex ?? 0) - (b.orderIndex ?? 0))
}

function getEffectiveStem(pq: any): string {
  if (pq.question?.stem) return pq.question.stem
  if (pq.questionSnapshot?.stem) return pq.questionSnapshot.stem
  return ''
}

function getEffectiveQuestionType(pq: any): QuestionType {
  if (pq.question?.questionType !== undefined) return pq.question.questionType
  if (pq.questionSnapshot?.question_type !== undefined) return pq.questionSnapshot.question_type
  if (pq.questionSnapshot?.questionType !== undefined) return pq.questionSnapshot.questionType
  return QuestionType.SingleChoice
}

function getEffectiveAnswer(pq: any): string {
  if (pq.question?.answer) return pq.question.answer
  if (pq.questionSnapshot?.answer) return pq.questionSnapshot.answer
  return ''
}

const recordsMap = computed(() => {
  const map = new Map<string, any>()
  if (sheet.value?.records) {
    for (const r of sheet.value.records) {
      map.set(r.questionId, r)
    }
  }
  return map
})

const renderQuestions = computed<RenderQuestion[]>(() => {
  if (!paperDetail.value?.questions?.length) return []
  const sorted = [...paperDetail.value.questions].sort((a, b) => {
    if (a.sectionIndex !== b.sectionIndex) return a.sectionIndex - b.sectionIndex
    return a.orderIndex - b.orderIndex
  })
  return sorted.map((pq) => {
    const record = recordsMap.value.get(pq.questionId)
    return {
      questionId: pq.questionId,
      questionType: getEffectiveQuestionType(pq),
      stem: getEffectiveStem(pq),
      options: getEffectiveOptions(pq),
      score: pq.score,
      correctAnswer: getEffectiveAnswer(pq),
      sectionIndex: pq.sectionIndex,
      orderIndex: pq.orderIndex,
      recordId: record?.recordId || '',
      answerContent: record?.answerContent || '',
      studentScore: record?.score ?? null,
      isCorrect: record?.isCorrect ?? null,
      gradingStatus: record?.gradingStatus ?? 0,
      comment: record?.comment || ''
    }
  })
})

const currentQuestion = computed(() => renderQuestions.value[currentQuestionIndex.value])

const sectionQuestionsMap = computed(() => {
  const map = new Map<number, RenderQuestion[]>()
  for (const q of renderQuestions.value) {
    if (!map.has(q.sectionIndex)) map.set(q.sectionIndex, [])
    map.get(q.sectionIndex)!.push(q)
  }
  return map
})

const sectionScoreMap = computed(() => {
  const map = new Map<number, number>()
  for (const [sec, qs] of sectionQuestionsMap.value) {
    map.set(sec, qs.reduce((sum, q) => sum + q.score, 0))
  }
  return map
})

async function initExam() {
  loading.value = true
  errorMessage.value = ''
  try {
    const result = await getExamList({ current: 1, pageSize: 100, courseId })
    const found = result.records.find(e => e.examId === examId)
    if (!found) {
      errorMessage.value = '未找到考试信息'
      return
    }
    examInfo.value = found
    try {
      await examStore.fetchMySheet(examId)
    } catch {
      await examStore.enterExamAction(examId)
    }
    if (found.paperId) {
      paperDetail.value = await getPaperDetail(found.paperId)
      if (!paperDetail.value?.questions?.length) {
        errorMessage.value = '试卷暂无题目'
      }
    } else {
      errorMessage.value = '考试未关联试卷'
    }
  } catch (error) {
    const msg = error instanceof Error ? error.message : '进入考试失败'
    ElMessage.error(msg)
    errorMessage.value = msg
  } finally {
    loading.value = false
  }
  startAutoSave()
}

async function reEnterHomework() {
  loading.value = true
  try {
    await examStore.enterExamAction(examId)
    ElMessage.success('已重新进入作业，可继续作答')
    if (examInfo.value?.paperId) {
      paperDetail.value = await getPaperDetail(examInfo.value.paperId)
    }
  } catch (error) {
    const msg = error instanceof Error ? error.message : '重新进入失败'
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
    for (const q of renderQuestions.value) {
      const savedAnswer = examStore.answers.get(q.questionId)
      if (savedAnswer !== undefined && savedAnswer !== null && savedAnswer !== '') {
        examStore.saveAnswerAction(sheet.value.sheetId, q.questionId, savedAnswer).catch(() => {})
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
  setAnswer(questionId, current.sort().join(','))
}

function goToQuestion(index: number) {
  if (index >= 0 && index < renderQuestions.value.length) {
    currentQuestionIndex.value = index
  }
}

function prevQuestion() {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

function nextQuestion() {
  if (currentQuestionIndex.value < renderQuestions.value.length - 1) {
    currentQuestionIndex.value++
  }
}

function goToSectionFirst(sectionIndex: number) {
  const idx = renderQuestions.value.findIndex(q => q.sectionIndex === sectionIndex)
  if (idx >= 0) goToQuestion(idx)
}

async function handleSubmit() {
  if (!sheet.value) return
  try {
    const confirmMsg = isHomework.value
      ? '确认提交作业吗？提交后可以重新进入修改。'
      : '确认交卷吗？交卷后不可修改答案。'
    await ElMessageBox.confirm(confirmMsg, '提交确认', {
      confirmButtonText: '确认提交',
      cancelButtonText: '继续答题',
      type: 'warning'
    })
    await examStore.submitExamAction(sheet.value.sheetId)
    ElMessage.success('提交成功')
  } catch {
    // cancelled
  }
}

function goBack() {
  router.back()
}

onMounted(initExam)
onUnmounted(() => {
  stopAutoSave()
  examStore.clearExamState()
})
</script>

<template>
  <div v-loading="loading" class="exam-answer">
    <template v-if="sheet && !errorMessage">
      <div class="answer-header">
        <div class="header-left">
          <el-button text @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            返回
          </el-button>
          <h2 class="exam-title">{{ sheet.examName }}</h2>
          <el-tag v-if="isHomework" type="success" size="small">作业</el-tag>
        </div>
        <div class="header-right">
          <div v-if="isAnswering" class="countdown">
            <el-tag type="danger" size="large" effect="dark">
              剩余时间：{{ remainingTime }}
            </el-tag>
          </div>
          <div v-if="isEnded" class="result-info">
            <el-tag :type="isGraded ? 'success' : 'warning'" size="large">
              {{ isGraded ? '已批阅' : '已提交' }}
            </el-tag>
            <span v-if="sheet.totalScore > 0" class="total-score">
              总分：{{ sheet.totalScore }}
            </span>
          </div>
        </div>
      </div>

      <div class="answer-body">
        <div class="question-nav">
          <div class="nav-title">题目导航</div>
          <div class="nav-sections">
            <div v-for="sec in sections" :key="sec.index" class="nav-section">
              <div class="nav-section-title" @click="goToSectionFirst(sec.index)">
                第{{ sec.index }}节：{{ sec.title || '未命名' }}
                <span class="nav-section-count">({{ sectionQuestionsMap.get(sec.index)?.length || 0 }}题)</span>
              </div>
              <div class="nav-grid">
                <div
                  v-for="(q, idx) in sectionQuestionsMap.get(sec.index)"
                  :key="q.questionId"
                  class="nav-item"
                  :class="{
                    active: renderQuestions.indexOf(q) === currentQuestionIndex,
                    answered: examStore.answers.has(q.questionId) || !!q.answerContent,
                    wrong: isEnded && q.isCorrect === false,
                    correct: isEnded && q.isCorrect === true
                  }"
                  @click="goToQuestion(renderQuestions.indexOf(q))"
                >
                  {{ renderQuestions.indexOf(q) + 1 }}
                </div>
              </div>
            </div>
          </div>
          <div class="nav-legend">
            <span class="legend-item"><span class="dot answered"></span>已答</span>
            <span class="legend-item"><span class="dot"></span>未答</span>
            <span v-if="isEnded" class="legend-item"><span class="dot correct"></span>正确</span>
            <span v-if="isEnded" class="legend-item"><span class="dot wrong"></span>错误</span>
          </div>
          <div class="nav-progress">
            已答 {{ examStore.answeredCount }} / {{ renderQuestions.length }}
          </div>
          <el-button
            v-if="isAnswering"
            type="danger"
            style="width: 100%; margin-top: 12px"
            @click="handleSubmit"
          >
            {{ isHomework ? '提交作业' : '交卷' }}
          </el-button>
          <el-button
            v-if="canReEnter"
            type="primary"
            style="width: 100%; margin-top: 12px"
            @click="reEnterHomework"
          >
            重新作答
          </el-button>
        </div>

        <div class="question-area">
          <template v-if="currentQuestion">
            <div class="question-header">
              <el-tag v-if="sections.length > 1" size="small" type="info" style="margin-right: 4px">
                第{{ currentQuestion.sectionIndex }}节
              </el-tag>
              <span class="question-index">第{{ currentQuestionIndex + 1 }}题</span>
              <el-tag :type="questionTypeTagType[currentQuestion.questionType]" size="small">
                {{ questionTypeMap[currentQuestion.questionType] }}
              </el-tag>
              <span class="question-score">{{ currentQuestion.score }}分</span>
            </div>

            <div class="question-stem">{{ currentQuestion.stem }}</div>

            <div class="question-answer">
              <template v-if="currentQuestion.questionType === QuestionType.SingleChoice">
                <el-radio-group
                  :model-value="getAnswer(currentQuestion.questionId)"
                  @change="(val: string | number | boolean | undefined) => setAnswer(currentQuestion.questionId, val as string)"
                  :disabled="isEnded"
                >
                  <el-radio
                    v-for="opt in currentQuestion.options"
                    :key="opt.label"
                    :value="opt.label"
                    class="option-radio"
                    :class="{ 'correct-option': isEnded && opt.isCorrect }"
                  >
                    <span class="option-label">{{ opt.label }}.</span>
                    <span class="option-content">{{ opt.content }}</span>
                    <el-tag v-if="isEnded && opt.isCorrect" type="success" size="small" class="correct-badge">正确</el-tag>
                  </el-radio>
                </el-radio-group>
              </template>

              <template v-if="currentQuestion.questionType === QuestionType.MultipleChoice">
                <div
                  v-for="opt in currentQuestion.options"
                  :key="opt.label"
                  class="option-checkbox"
                  :class="{ 'correct-option': isEnded && opt.isCorrect }"
                >
                  <el-checkbox
                    :model-value="isOptionSelected(currentQuestion.questionId, opt.label)"
                    :disabled="isEnded"
                    @change="toggleMultiOption(currentQuestion.questionId, opt.label)"
                  >
                    <span class="option-label">{{ opt.label }}.</span>
                    <span class="option-content">{{ opt.content }}</span>
                  </el-checkbox>
                  <el-tag v-if="isEnded && opt.isCorrect" type="success" size="small" class="correct-badge">正确</el-tag>
                </div>
              </template>

              <template v-if="currentQuestion.questionType === QuestionType.TrueFalse">
                <el-radio-group
                  :model-value="getAnswer(currentQuestion.questionId)"
                  @change="(val: string | number | boolean | undefined) => setAnswer(currentQuestion.questionId, val as string)"
                  :disabled="isEnded"
                >
                  <el-radio value="正确" class="option-radio">正确</el-radio>
                  <el-radio value="错误" class="option-radio">错误</el-radio>
                </el-radio-group>
              </template>

              <template v-if="currentQuestion.questionType === QuestionType.FillBlank">
                <el-input
                  :model-value="getAnswer(currentQuestion.questionId)"
                  @input="(val: string) => setAnswer(currentQuestion.questionId, val)"
                  :disabled="isEnded"
                  placeholder="请输入答案"
                  class="fill-blank-input"
                />
              </template>

              <template v-if="currentQuestion.questionType === QuestionType.ShortAnswer">
                <el-input
                  :model-value="getAnswer(currentQuestion.questionId)"
                  @input="(val: string) => setAnswer(currentQuestion.questionId, val)"
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
                <div class="result-row">
                  <span class="result-label">正确答案：</span>
                  <span class="result-value correct">{{ currentQuestion.correctAnswer || '无' }}</span>
                </div>
                <div class="result-row">
                  <span class="result-label">我的答案：</span>
                  <span
                    class="result-value"
                    :class="{
                      correct: currentQuestion.isCorrect === true,
                      wrong: currentQuestion.isCorrect === false
                    }"
                  >
                    {{ currentQuestion.answerContent || '未作答' }}
                  </span>
                </div>
                <div class="result-row">
                  <span class="result-label">得分：</span>
                  <span
                    class="result-value score-value"
                    :class="{
                      graded: currentQuestion.gradingStatus === GradingStatus.Graded
                    }"
                  >
                    {{
                      currentQuestion.gradingStatus === GradingStatus.NotGraded
                        ? '未批改'
                        : currentQuestion.gradingStatus === GradingStatus.AIGrading
                          ? 'AI批阅中'
                          : currentQuestion.studentScore ?? '0'
                    }}
                  </span>
                </div>
                <div class="result-row" v-if="currentQuestion.isCorrect !== null">
                  <el-tag :type="currentQuestion.isCorrect ? 'success' : 'danger'" size="small">
                    {{ currentQuestion.isCorrect ? '正确' : '错误' }}
                  </el-tag>
                </div>
                <div class="result-row" v-if="currentQuestion.comment">
                  <span class="result-label">评语：</span>
                  <span class="result-value">{{ currentQuestion.comment }}</span>
                </div>
              </div>
            </template>

            <div class="question-nav-buttons">
              <el-button :disabled="currentQuestionIndex === 0" @click="prevQuestion">上一题</el-button>
              <el-button :disabled="currentQuestionIndex === renderQuestions.length - 1" @click="nextQuestion">下一题</el-button>
            </div>
          </template>

          <el-empty v-else-if="!loading && !errorMessage" description="暂无题目" />
        </div>
      </div>
    </template>

    <el-result v-else-if="errorMessage" icon="error" :title="errorMessage">
      <template #extra>
        <el-button type="primary" @click="goBack">返回</el-button>
      </template>
    </el-result>
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

  .header-left {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .exam-title {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .total-score {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
}

.answer-body {
  display: flex;
  gap: 16px;
}

.question-nav {
  width: 220px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  max-height: calc(100vh - 160px);
  overflow-y: auto;

  .nav-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 12px;
  }

  .nav-sections {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .nav-section-title {
    font-size: 12px;
    font-weight: 500;
    color: #606266;
    margin-bottom: 6px;
    cursor: pointer;
    padding: 2px 4px;
    border-radius: 4px;
    transition: background 0.2s;

    &:hover {
      background: #f5f7fa;
    }

    .nav-section-count {
      color: #909399;
      font-weight: 400;
    }
  }

  .nav-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 6px;
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

    &.correct {
      background: #f0f9eb;
      border-color: #b3e19d;
      color: #67c23a;
    }

    &.wrong {
      background: #fef0f0;
      border-color: #fbc4c4;
      color: #f56c6c;
    }
  }

  .nav-legend {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 12px;
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

      &.correct {
        background: #f0f9eb;
        border-color: #b3e19d;
      }

      &.wrong {
        background: #fef0f0;
        border-color: #fbc4c4;
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
    color: #e6a23c;
    font-weight: 600;
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

  .option-label {
    font-weight: 600;
    margin-right: 4px;
  }

  .option-content {
    white-space: pre-wrap;
  }

  .option-radio,
  .option-checkbox {
    display: flex;
    align-items: flex-start;
    margin-bottom: 12px;
    padding: 8px 12px;
    border: 1px solid #ebeef5;
    border-radius: 6px;
    transition: all 0.2s;

    &:hover {
      border-color: #c0c4cc;
      background: #fafafa;
    }

    &.correct-option {
      border-color: #b3e19d;
      background: #f0f9eb;
    }
  }

  .option-checkbox {
    align-items: center;
  }

  .correct-badge {
    margin-left: 8px;
    transform: scale(0.85);
  }

  .fill-blank-input {
    max-width: 400px;
  }
}

.question-result {
  font-size: 14px;
  color: #606266;
  line-height: 2;

  .result-row {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .result-label {
    color: #909399;
    min-width: 80px;
  }

  .result-value {
    &.correct {
      color: #67c23a;
      font-weight: 600;
    }

    &.wrong {
      color: #f56c6c;
    }

    &.score-value.graded {
      color: #409eff;
      font-weight: 600;
    }
  }
}

.question-nav-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
}
</style>
