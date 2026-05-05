<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { getQuestionBankList, getQuestionList } from '@/api/exam/index'
import type { QuestionBankVO, QuestionVO, QuestionItem, QuestionQueryRequest } from '@/api/exam/types'
import { QuestionType, Difficulty } from '@/types/enums'
import { Search, StarFilled } from '@element-plus/icons-vue'

const props = withDefaults(defineProps<{
  modelValue: boolean
  courseId: string
  targetSectionIndex?: number
  simple?: boolean
  title?: string
  existingQuestionIds?: string[]
}>(), {
  simple: false,
  title: '从题库选择题目',
  existingQuestionIds: () => []
})

const emit = defineEmits<{
  (e: 'update:modelValue', val: boolean): void
  (e: 'confirm', items: QuestionItem[]): void
  (e: 'remove', questionId: string): void
}>()

const visible = ref(false)
watch(() => props.modelValue, v => { visible.value = v })
watch(visible, v => { emit('update:modelValue', v) })

const banks = ref<QuestionBankVO[]>([])
const bankTotal = ref(0)
const bankPage = ref(1)
const bankPageSize = ref(20)
const selectedBank = ref<QuestionBankVO | null>(null)

const questions = ref<QuestionVO[]>([])
const questionTotal = ref(0)
const questionPage = ref(1)
const questionPageSize = ref(10)
const questionFilterType = ref<QuestionType | undefined>(undefined)
const questionKeyword = ref('')
const questionLoading = ref(false)
const bankLoading = ref(false)

const selectedQuestions = ref<QuestionItem[]>([])
const selectedQuestionIdsSet = computed(() => new Set(selectedQuestions.value.map(sq => sq.questionId)))
const existingIdsSet = computed(() => new Set(props.existingQuestionIds))

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

const difficultyLabels: Record<number, string> = {
  [Difficulty.VeryEasy]: '非常简单',
  [Difficulty.Easy]: '简单',
  [Difficulty.Medium]: '中等',
  [Difficulty.Hard]: '困难',
  [Difficulty.VeryHard]: '非常困难'
}

async function loadBanks() {
  bankLoading.value = true
  try {
    const result = await getQuestionBankList({
      current: bankPage.value,
      pageSize: bankPageSize.value,
      courseId: props.courseId
    })
    banks.value = result.records
    bankTotal.value = result.total
    if (banks.value.length > 0 && !selectedBank.value) {
      selectBank(banks.value[0])
    }
  } finally {
    bankLoading.value = false
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
    const params: QuestionQueryRequest = {
      current: questionPage.value,
      pageSize: questionPageSize.value,
      bankId: selectedBank.value.bankId,
      questionType: questionFilterType.value,
      keyword: questionKeyword.value || undefined
    }
    const result = await getQuestionList(params)
    questions.value = result.records
    questionTotal.value = result.total
  } finally {
    questionLoading.value = false
  }
}

function toggleSelect(q: QuestionVO) {
  if (existingIdsSet.value.has(q.questionId)) return
  if (selectedQuestionIdsSet.value.has(q.questionId)) {
    selectedQuestions.value = selectedQuestions.value.filter(sq => sq.questionId !== q.questionId)
  } else {
    selectedQuestions.value.push({
      questionId: q.questionId,
      score: q.score,
      sectionIndex: props.targetSectionIndex ?? 1
    })
  }
}

function isSelected(qId: string) {
  return selectedQuestionIdsSet.value.has(qId)
}

function isExisting(qId: string) {
  return existingIdsSet.value.has(qId)
}

function handleRemoveExisting(qId: string) {
  emit('remove', qId)
}

function handleConfirm() {
  if (selectedQuestions.value.length === 0) return
  emit('confirm', [...selectedQuestions.value])
  selectedQuestions.value = []
  visible.value = false
}

function handleOpen() {
  selectedQuestions.value = []
  selectedBank.value = null
  bankPage.value = 1
  loadBanks()
}

function handleQuestionPageChange(page: number) {
  questionPage.value = page
  loadQuestions()
}

function handleFilterSearch() {
  questionPage.value = 1
  loadQuestions()
}

function removeSelectedItem(idx: number) {
  selectedQuestions.value.splice(idx, 1)
}

function clearAllSelected() {
  selectedQuestions.value = []
}
</script>

<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="900px"
    :close-on-click-modal="false"
    @open="handleOpen"
  >
    <div class="question-selector">
      <div class="selector-left">
        <div class="panel-title">题库列表</div>
        <el-scrollbar height="50vh" v-loading="bankLoading">
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
          </div>
          <el-empty v-if="banks.length === 0" description="暂无题库" :image-size="60" />
        </el-scrollbar>
      </div>

      <div class="selector-right">
        <template v-if="selectedBank">
          <div class="filter-bar">
            <el-select
              v-model="questionFilterType"
              placeholder="题型筛选"
              clearable
              size="small"
              style="width: 110px"
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
              :prefix-icon="Search"
              @keyup.enter="handleFilterSearch"
              @clear="handleFilterSearch"
            />
          </div>

          <el-table
            v-loading="questionLoading"
            :data="questions"
            stripe
            max-height="40vh"
            @row-click="toggleSelect"
            :row-class="({ row }: { row: QuestionVO }) => isExisting(row.questionId) ? 'existing-row' : ''"
            style="cursor: pointer"
          >
            <el-table-column width="70">
              <template #default="{ row }">
                <template v-if="isExisting(row.questionId)">
                  <el-tag type="success" size="small">已添加</el-tag>
                </template>
                <template v-else>
                  <el-checkbox :model-value="isSelected(row.questionId)" @click.stop />
                </template>
              </template>
            </el-table-column>
            <el-table-column prop="stem" label="题干" min-width="180" show-overflow-tooltip />
            <el-table-column label="题型" width="80">
              <template #default="{ row }">
                <el-tag :type="questionTypeTagType[row.questionType]" size="small">
                  {{ questionTypeMap[row.questionType] }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="难度" width="100">
              <template #default="{ row }">
                <span class="difficulty-stars">
                  <el-icon v-for="i in row.difficulty" :key="i" class="star-icon">
                    <StarFilled />
                  </el-icon>
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="score" label="默认分值" width="80" />
            <el-table-column label="操作" width="70">
              <template #default="{ row }">
                <el-button
                  v-if="isExisting(row.questionId)"
                  size="small"
                  link
                  type="danger"
                  @click.stop="handleRemoveExisting(row.questionId)"
                >
                  移除
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-if="questionTotal > questionPageSize"
            v-model:current-page="questionPage"
            :total="questionTotal"
            :page-size="questionPageSize"
            layout="prev, pager, next"
            small
            style="margin-top: 8px"
            @current-change="handleQuestionPageChange"
          />
        </template>
        <el-empty v-else description="请从左侧选择题库" />
      </div>
    </div>

    <div v-if="selectedQuestions.length > 0" class="selected-summary">
      <div class="summary-header">
        <span>已选 <strong>{{ selectedQuestions.length }}</strong> 道题目</span>
        <el-button size="small" link type="danger" @click="clearAllSelected">清空已选</el-button>
      </div>
      <div class="summary-list">
        <div v-for="(sq, idx) in selectedQuestions" :key="sq.questionId" class="summary-item">
          <span class="summary-index">{{ idx + 1 }}.</span>
          <span class="summary-id">ID: {{ sq.questionId }}</span>
          <template v-if="!simple">
            <el-input-number v-model="sq.score" :min="1" size="small" style="width: 90px" />
            <span class="summary-label">分</span>
            <el-select v-model="sq.sectionIndex" size="small" style="width: 90px">
              <el-option
                v-for="sec in 5"
                :key="sec"
                :label="'第' + sec + '节'"
                :value="sec"
              />
            </el-select>
          </template>
          <el-button size="small" link type="danger" @click="removeSelectedItem(idx)">移除</el-button>
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :disabled="selectedQuestions.length === 0" @click="handleConfirm">
        确认{{ simple ? '关联' : '添加' }} ({{ selectedQuestions.length }})
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.question-selector {
  display: flex;
  gap: 16px;
  min-height: 50vh;
}

.selector-left {
  width: 240px;
  flex-shrink: 0;
  border-right: 1px solid #ebeef5;
  padding-right: 12px;

  .panel-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 10px;
  }
}

.bank-item {
  padding: 8px 10px;
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
    gap: 2px;

    .bank-name {
      font-size: 13px;
      font-weight: 500;
      color: #303133;
    }

    .bank-count {
      font-size: 11px;
      color: #909399;
    }
  }
}

.selector-right {
  flex: 1;
  min-width: 0;
}

.filter-bar {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
}

.selected-summary {
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;

  .summary-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 13px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 8px;

    strong {
      color: #409eff;
    }
  }

  .summary-list {
    max-height: 150px;
    overflow-y: auto;
  }

  .summary-item {
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 4px 0;
    font-size: 13px;

    .summary-index {
      color: #606266;
      font-weight: 500;
    }

    .summary-id {
      color: #909399;
      font-size: 12px;
    }

    .summary-label {
      color: #909399;
      font-size: 12px;
    }
  }
}

.difficulty-stars {
  display: inline-flex;
  align-items: center;
  gap: 2px;

  .star-icon {
    font-size: 12px;
    color: #f7ba2a;
  }
}
</style>

<style lang="scss">
.existing-row {
  background-color: #f0f9eb !important;
}
</style>
