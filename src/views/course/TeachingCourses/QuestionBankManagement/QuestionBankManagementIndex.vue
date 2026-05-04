<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Search, ArrowLeft } from '@element-plus/icons-vue'
import {
  getQuestionBankList,
  createQuestionBank,
  updateQuestionBank,
  deleteQuestionBank,
  getQuestionList,
  createQuestion,
  updateQuestion,
  deleteQuestion
} from '@/api/exam/index'
import type {
  QuestionBankVO,
  QuestionBankCreateRequest,
  QuestionBankUpdateRequest,
  QuestionVO,
  QuestionCreateRequest,
  QuestionUpdateRequest,
  QuestionOptionDTO
} from '@/api/exam/types'
import { QuestionType, Difficulty } from '@/types/enums'
import type { FormInstance, FormRules } from 'element-plus'

const route = useRoute()
const courseId = Number(route.params.id)

const loading = ref(false)
const questionLoading = ref(false)
const banks = ref<QuestionBankVO[]>([])
const bankTotal = ref(0)
const bankPage = ref(1)
const bankPageSize = ref(10)
const selectedBank = ref<QuestionBankVO | null>(null)

const questions = ref<QuestionVO[]>([])
const questionTotal = ref(0)
const questionPage = ref(1)
const questionPageSize = ref(10)
const questionFilterType = ref<QuestionType | undefined>(undefined)
const questionKeyword = ref('')

const bankDialogVisible = ref(false)
const isEditBank = ref(false)
const editingBankId = ref<number | null>(null)
const bankFormRef = ref<FormInstance>()
const bankForm = reactive<QuestionBankCreateRequest & { bankName: string; courseId: number; description: string }>({
  bankName: '',
  courseId,
  description: ''
})
const bankRules = reactive<FormRules>({
  bankName: [{ required: true, message: '请输入题库名称', trigger: 'blur' }]
})

const questionDialogVisible = ref(false)
const isEditQuestion = ref(false)
const editingQuestionId = ref<number | null>(null)
const questionFormRef = ref<FormInstance>()
const questionForm = reactive<QuestionCreateRequest & { questionType: QuestionType; stem: string; score: number }>({
  questionType: QuestionType.SingleChoice,
  stem: '',
  analysis: '',
  answer: '',
  score: 5,
  difficulty: Difficulty.Medium,
  options: []
})

const questionTypeMap: Record<number, string> = {
  [QuestionType.SingleChoice]: '单选题',
  [QuestionType.MultipleChoice]: '多选题',
  [QuestionType.TrueFalse]: '判断题',
  [QuestionType.FillBlank]: '填空题',
  [QuestionType.ShortAnswer]: '简答题'
}

const questionTypeTagType: Record<number, string> = {
  [QuestionType.SingleChoice]: 'primary',
  [QuestionType.MultipleChoice]: 'success',
  [QuestionType.TrueFalse]: 'warning',
  [QuestionType.FillBlank]: 'info',
  [QuestionType.ShortAnswer]: 'danger'
}

const hasOptions = computed(() =>
  questionForm.questionType === QuestionType.SingleChoice ||
  questionForm.questionType === QuestionType.MultipleChoice
)

async function loadBanks() {
  loading.value = true
  try {
    const result = await getQuestionBankList({
      current: bankPage.value,
      pageSize: bankPageSize.value,
      courseId
    })
    banks.value = result.records
    bankTotal.value = result.total
    if (banks.value.length > 0 && !selectedBank.value) {
      selectBank(banks.value[0])
    }
  } finally {
    loading.value = false
  }
}

function selectBank(bank: QuestionBankVO) {
  selectedBank.value = bank
  questionPage.value = 1
  questionFilterType.value = undefined
  questionKeyword.value = ''
  loadQuestions()
}

async function loadQuestions() {
  if (!selectedBank.value) return
  questionLoading.value = true
  try {
    const result = await getQuestionList({
      current: questionPage.value,
      pageSize: questionPageSize.value,
      bankId: selectedBank.value.bankId,
      questionType: questionFilterType.value,
      keyword: questionKeyword.value || undefined
    })
    questions.value = result.records
    questionTotal.value = result.total
  } finally {
    questionLoading.value = false
  }
}

function openCreateBank() {
  isEditBank.value = false
  editingBankId.value = null
  bankForm.bankName = ''
  bankForm.description = ''
  bankDialogVisible.value = true
}

function openEditBank(bank: QuestionBankVO) {
  isEditBank.value = true
  editingBankId.value = bank.bankId
  bankForm.bankName = bank.bankName
  bankForm.description = bank.description
  bankDialogVisible.value = true
}

async function handleBankSubmit() {
  if (!bankFormRef.value) return
  await bankFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEditBank.value && editingBankId.value) {
        await updateQuestionBank(editingBankId.value, {
          bankName: bankForm.bankName,
          description: bankForm.description
        })
        ElMessage.success('修改成功')
      } else {
        await createQuestionBank({
          bankName: bankForm.bankName,
          courseId,
          description: bankForm.description
        })
        ElMessage.success('创建成功')
      }
      bankDialogVisible.value = false
      await loadBanks()
    } catch (error) {
      const msg = error instanceof Error ? error.message : '操作失败'
      ElMessage.error(msg)
    }
  })
}

async function handleDeleteBank(bank: QuestionBankVO) {
  if (bank.questionCount > 0) {
    ElMessage.warning('该题库下有题目，无法删除')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除该题库吗？', '删除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteQuestionBank(bank.bankId)
    ElMessage.success('删除成功')
    if (selectedBank.value?.bankId === bank.bankId) {
      selectedBank.value = null
    }
    await loadBanks()
  } catch {
    // cancelled
  }
}

function openCreateQuestion() {
  if (!selectedBank.value) {
    ElMessage.warning('请先选择题库')
    return
  }
  isEditQuestion.value = false
  editingQuestionId.value = null
  questionForm.questionType = QuestionType.SingleChoice
  questionForm.stem = ''
  questionForm.analysis = ''
  questionForm.answer = ''
  questionForm.score = 5
  questionForm.difficulty = Difficulty.Medium
  questionForm.options = [
    { label: 'A', content: '', isCorrect: false, orderIndex: 0 },
    { label: 'B', content: '', isCorrect: false, orderIndex: 1 },
    { label: 'C', content: '', isCorrect: false, orderIndex: 2 },
    { label: 'D', content: '', isCorrect: false, orderIndex: 3 }
  ]
  questionDialogVisible.value = true
}

function openEditQuestion(q: QuestionVO) {
  isEditQuestion.value = true
  editingQuestionId.value = q.questionId
  questionForm.questionType = q.questionType
  questionForm.stem = q.stem
  questionForm.analysis = q.analysis
  questionForm.answer = q.answer
  questionForm.score = q.score
  questionForm.difficulty = q.difficulty
  questionForm.options = q.options?.map(o => ({
    label: o.label,
    content: o.content,
    isCorrect: o.isCorrect,
    orderIndex: o.orderIndex
  })) || []
  questionDialogVisible.value = true
}

function addOption() {
  const labels = 'ABCDEFGHIJ'
  const nextLabel = labels[questionForm.options?.length || 0] || '?'
  questionForm.options = [...(questionForm.options || []), {
    label: nextLabel,
    content: '',
    isCorrect: false,
    orderIndex: questionForm.options?.length || 0
  }]
}

function removeOption(index: number) {
  const opts = [...(questionForm.options || [])]
  opts.splice(index, 1)
  opts.forEach((o, i) => {
    o.label = 'ABCDEFGHIJ'[i]
    o.orderIndex = i
  })
  questionForm.options = opts
}

function handleCorrectChange(index: number) {
  if (questionForm.questionType === QuestionType.SingleChoice) {
    questionForm.options?.forEach((o, i) => {
      o.isCorrect = i === index
    })
  }
}

async function handleQuestionSubmit() {
  if (!questionForm.stem.trim()) {
    ElMessage.warning('请输入题干')
    return
  }
  if (!selectedBank.value) return
  try {
    const data: QuestionCreateRequest | QuestionUpdateRequest = {
      questionType: questionForm.questionType,
      stem: questionForm.stem,
      analysis: questionForm.analysis || undefined,
      answer: questionForm.answer || undefined,
      score: questionForm.score,
      difficulty: questionForm.difficulty,
      options: hasOptions.value ? questionForm.options : undefined
    }
    if (isEditQuestion.value && editingQuestionId.value) {
      await updateQuestion(editingQuestionId.value, data as QuestionUpdateRequest)
      ElMessage.success('修改成功')
    } else {
      await createQuestion(selectedBank.value.bankId, data as QuestionCreateRequest)
      ElMessage.success('创建成功')
    }
    questionDialogVisible.value = false
    await loadQuestions()
    await loadBanks()
  } catch (error) {
    const msg = error instanceof Error ? error.message : '操作失败'
    ElMessage.error(msg)
  }
}

async function handleDeleteQuestion(q: QuestionVO) {
  try {
    await ElMessageBox.confirm('确认删除该题目吗？', '删除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteQuestion(q.questionId)
    ElMessage.success('删除成功')
    await loadQuestions()
    await loadBanks()
  } catch {
    // cancelled
  }
}

function handleQuestionPageChange(page: number) {
  questionPage.value = page
  loadQuestions()
}

function handleBankPageChange(page: number) {
  bankPage.value = page
  loadBanks()
}

function handleFilterSearch() {
  questionPage.value = 1
  loadQuestions()
}

onMounted(loadBanks)
</script>

<template>
  <div class="question-bank-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/TeachingCourses' }">我教的课</el-breadcrumb-item>
            <el-breadcrumb-item>题库管理</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <div class="content">
        <div class="left-panel">
          <div class="panel-header">
            <span class="panel-title">题库列表</span>
            <el-button type="primary" :icon="Plus" size="small" @click="openCreateBank">新建</el-button>
          </div>
          <el-scrollbar height="65vh">
            <div
              v-for="bank in banks"
              :key="bank.bankId"
              class="bank-item"
              :class="{ active: selectedBank?.bankId === bank.bankId }"
              @click="selectBank(bank)"
            >
              <div class="bank-info">
                <span class="bank-name">{{ bank.bankName }}</span>
                <span class="bank-count">{{ bank.questionCount }} 题</span>
              </div>
              <div class="bank-actions" @click.stop>
                <el-button size="small" link :icon="Edit" @click="openEditBank(bank)" />
                <el-button size="small" link type="danger" :icon="Delete" @click="handleDeleteBank(bank)" />
              </div>
            </div>
            <el-empty v-if="banks.length === 0" description="暂无题库" :image-size="60" />
          </el-scrollbar>
          <el-pagination
            v-if="bankTotal > bankPageSize"
            v-model:current-page="bankPage"
            :total="bankTotal"
            :page-size="bankPageSize"
            layout="prev, pager, next"
            small
            @current-change="handleBankPageChange"
          />
        </div>

        <div class="right-panel">
          <template v-if="selectedBank">
            <div class="panel-header">
              <span class="panel-title">{{ selectedBank.bankName }} - 题目列表</span>
              <div class="filter-bar">
                <el-select
                  v-model="questionFilterType"
                  placeholder="题型筛选"
                  clearable
                  size="small"
                  style="width: 120px"
                  @change="handleFilterSearch"
                >
                  <el-option label="单选题" :value="0" />
                  <el-option label="多选题" :value="1" />
                  <el-option label="判断题" :value="2" />
                  <el-option label="填空题" :value="3" />
                  <el-option label="简答题" :value="4" />
                </el-select>
                <el-input
                  v-model="questionKeyword"
                  placeholder="搜索题干"
                  clearable
                  size="small"
                  style="width: 160px"
                  @keyup.enter="handleFilterSearch"
                  @clear="handleFilterSearch"
                />
                <el-button type="primary" :icon="Plus" size="small" @click="openCreateQuestion">创建题目</el-button>
              </div>
            </div>

            <el-table v-loading="questionLoading" :data="questions" stripe style="width: 100%">
              <el-table-column type="index" label="#" width="50" />
              <el-table-column prop="stem" label="题干" min-width="200" show-overflow-tooltip />
              <el-table-column label="题型" width="100">
                <template #default="{ row }">
                  <el-tag :type="questionTypeTagType[row.questionType]" size="small">
                    {{ questionTypeMap[row.questionType] }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="难度" width="120">
                <template #default="{ row }">
                  <el-rate v-model="row.difficulty" disabled :max="5" size="small" />
                </template>
              </el-table-column>
              <el-table-column prop="score" label="分值" width="70" />
              <el-table-column label="操作" width="150">
                <template #default="{ row }">
                  <el-button size="small" link @click="openEditQuestion(row)">编辑</el-button>
                  <el-button size="small" link type="danger" @click="handleDeleteQuestion(row)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-pagination
              v-if="questionTotal > questionPageSize"
              v-model:current-page="questionPage"
              :total="questionTotal"
              :page-size="questionPageSize"
              layout="prev, pager, next"
              style="margin-top: 12px"
              @current-change="handleQuestionPageChange"
            />
          </template>

          <el-empty v-else description="请从左侧选择题库" />
        </div>
      </div>
    </el-card>

    <el-dialog v-model="bankDialogVisible" :title="isEditBank ? '编辑题库' : '新建题库'" width="400px">
      <el-form ref="bankFormRef" :model="bankForm" :rules="bankRules" label-width="80px">
        <el-form-item label="题库名称" prop="bankName">
          <el-input v-model="bankForm.bankName" placeholder="请输入题库名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="bankForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bankDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBankSubmit">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="questionDialogVisible"
      :title="isEditQuestion ? '编辑题目' : '创建题目'"
      width="700px"
      :close-on-click-modal="false"
    >
      <el-form :model="questionForm" label-width="80px">
        <el-form-item label="题型">
          <el-radio-group v-model="questionForm.questionType" :disabled="isEditQuestion">
            <el-radio :value="0">单选题</el-radio>
            <el-radio :value="1">多选题</el-radio>
            <el-radio :value="2">判断题</el-radio>
            <el-radio :value="3">填空题</el-radio>
            <el-radio :value="4">简答题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="题干">
          <el-input v-model="questionForm.stem" type="textarea" :rows="3" placeholder="请输入题干" />
        </el-form-item>

        <template v-if="hasOptions">
          <el-form-item label="选项">
            <div class="options-editor">
              <div v-for="(opt, idx) in questionForm.options" :key="idx" class="option-row">
                <el-tag size="small" style="width: 28px; text-align: center">{{ opt.label }}</el-tag>
                <el-input v-model="opt.content" placeholder="选项内容" style="flex: 1" />
                <el-checkbox
                  v-model="opt.isCorrect"
                  :label="questionForm.questionType === 0 ? '正确' : '正确'"
                  @change="handleCorrectChange(idx)"
                />
                <el-button
                  :icon="Delete"
                  size="small"
                  link
                  type="danger"
                  @click="removeOption(idx)"
                />
              </div>
              <el-button :icon="Plus" size="small" @click="addOption">添加选项</el-button>
            </div>
          </el-form-item>
        </template>

        <template v-if="questionForm.questionType === QuestionType.TrueFalse">
          <el-form-item label="答案">
            <el-radio-group v-model="questionForm.answer">
              <el-radio value="正确">正确</el-radio>
              <el-radio value="错误">错误</el-radio>
            </el-radio-group>
          </el-form-item>
        </template>

        <template v-if="questionForm.questionType === QuestionType.FillBlank || questionForm.questionType === QuestionType.ShortAnswer">
          <el-form-item label="标准答案">
            <el-input
              v-model="questionForm.answer"
              :type="questionForm.questionType === QuestionType.ShortAnswer ? 'textarea' : 'text'"
              :rows="questionForm.questionType === QuestionType.ShortAnswer ? 3 : 1"
              placeholder="请输入标准答案"
            />
          </el-form-item>
        </template>

        <el-form-item label="分值">
          <el-input-number v-model="questionForm.score" :min="1" :max="100" />
        </el-form-item>
        <el-form-item label="难度">
          <el-rate v-model="questionForm.difficulty" :max="5" />
        </el-form-item>
        <el-form-item label="解析">
          <el-input v-model="questionForm.analysis" type="textarea" :rows="2" placeholder="请输入解析" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="questionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleQuestionSubmit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.content {
  display: flex;
  gap: 16px;
}

.left-panel {
  width: 280px;
  flex-shrink: 0;
  border-right: 1px solid #ebeef5;
  padding-right: 16px;
}

.right-panel {
  flex: 1;
  min-width: 0;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;

  .panel-title {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
  }
}

.filter-bar {
  display: flex;
  gap: 8px;
  align-items: center;
}

.bank-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  margin-bottom: 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #f5f7fa;
  }

  &.active {
    background: #ecf5ff;
    border: 1px solid #409eff;
  }

  .bank-info {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .bank-name {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
    }

    .bank-count {
      font-size: 12px;
      color: #909399;
    }
  }
}

.options-editor {
  width: 100%;

  .option-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
  }
}
</style>
