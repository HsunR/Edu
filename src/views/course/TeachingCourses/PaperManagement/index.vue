<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, View } from '@element-plus/icons-vue'
import {
  getPaperList,
  getPaperDetail,
  createPaper,
  updatePaper,
  deletePaper,
  publishPaper,
  addPaperQuestions,
  removePaperQuestion,
  reorderPaperQuestions,
  getQuestionList
} from '@/api/exam/index'
import type {
  PaperVO,
  PaperDetailVO,
  PaperCreateRequest,
  PaperQuestionVO,
  QuestionVO,
  QuestionItem
} from '@/api/exam/types'
import { PaperStatus, QuestionType } from '@/types/enums'
import { getCourseClasses } from '@/api/course/course'
import type { ClassVO } from '@/api/course/types'

const route = useRoute()
const router = useRouter()
const courseId = route.params.id as string

const loading = ref(false)
const papers = ref<PaperVO[]>([])
const paperTotal = ref(0)
const paperPage = ref(1)
const paperPageSize = ref(10)
const paperStatusFilter = ref<0 | 1 | undefined>(undefined)
const paperKeyword = ref('')

const createDialogVisible = ref(false)
const createForm = reactive<PaperCreateRequest>({
  paperName: '',
  courseId,
  sections: [{ index: 1, title: '默认节' }]
})

const detailVisible = ref(false)
const currentPaper = ref<PaperDetailVO | null>(null)
const detailLoading = ref(false)

const addQuestionDialogVisible = ref(false)
const searchQuestions = ref<QuestionVO[]>([])
const searchLoading = ref(false)
const searchKeyword = ref('')
const selectedQuestions = ref<QuestionItem[]>([])

const questionTypeMap: Record<number, string> = {
  [QuestionType.SingleChoice]: '单选题',
  [QuestionType.MultipleChoice]: '多选题',
  [QuestionType.TrueFalse]: '判断题',
  [QuestionType.FillBlank]: '填空题',
  [QuestionType.ShortAnswer]: '简答题'
}

const paperStatusMap: Record<number, { label: string; type: string }> = {
  [PaperStatus.Draft]: { label: '草稿', type: 'info' },
  [PaperStatus.Published]: { label: '已发布', type: 'success' }
}

async function loadPapers() {
  loading.value = true
  try {
    const result = await getPaperList({
      current: paperPage.value,
      pageSize: paperPageSize.value,
      courseId,
      status: paperStatusFilter.value,
      keyword: paperKeyword.value || undefined
    })
    papers.value = result.records
    paperTotal.value = result.total
  } finally {
    loading.value = false
  }
}

function openCreateDialog() {
  createForm.paperName = ''
  createForm.sections = [{ index: 1, title: '默认节' }]
  createDialogVisible.value = true
}

function addSection() {
  const nextIndex = (createForm.sections?.length || 0) + 1
  createForm.sections = [...(createForm.sections || []), { index: nextIndex, title: '' }]
}

function removeSection(index: number) {
  const sections = [...(createForm.sections || [])]
  sections.splice(index, 1)
  sections.forEach((s, i) => { s.index = i + 1 })
  createForm.sections = sections
}

async function handleCreate() {
  if (!createForm.paperName.trim()) {
    ElMessage.warning('请输入试卷名称')
    return
  }
  try {
    await createPaper(createForm)
    ElMessage.success('创建成功')
    createDialogVisible.value = false
    await loadPapers()
  } catch (error) {
    const msg = error instanceof Error ? error.message : '创建失败'
    ElMessage.error(msg)
  }
}

async function handleViewDetail(paper: PaperVO) {
  detailLoading.value = true
  detailVisible.value = true
  try {
    currentPaper.value = await getPaperDetail(paper.paperId)
  } catch {
    currentPaper.value = null
  } finally {
    detailLoading.value = false
  }
}

async function handlePublish(paper: PaperVO) {
  try {
    await ElMessageBox.confirm('发布后试卷题目将被冻结，不可再修改。确认发布？', '发布确认', {
      confirmButtonText: '确认发布',
      cancelButtonText: '取消'
    })
    await publishPaper(paper.paperId)
    ElMessage.success('发布成功')
    await loadPapers()
  } catch {
    // cancelled
  }
}

async function handleDelete(paper: PaperVO) {
  if (paper.status !== PaperStatus.Draft) {
    ElMessage.warning('仅草稿状态可删除')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除该试卷吗？', '删除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deletePaper(paper.paperId)
    ElMessage.success('删除成功')
    await loadPapers()
  } catch {
    // cancelled
  }
}

async function openAddQuestion() {
  if (!currentPaper.value) return
  searchKeyword.value = ''
  selectedQuestions.value = []
  addQuestionDialogVisible.value = true
  await searchQuestionsFromBank()
}

async function searchQuestionsFromBank() {
  searchLoading.value = true
  try {
    const result = await getQuestionList({
      current: 1,
      pageSize: 50,
      keyword: searchKeyword.value || undefined
    })
    searchQuestions.value = result.records
  } finally {
    searchLoading.value = false
  }
}

function toggleQuestionSelect(q: QuestionVO) {
  const idx = selectedQuestions.value.findIndex(sq => sq.questionId === q.questionId)
  if (idx >= 0) {
    selectedQuestions.value.splice(idx, 1)
  } else {
    selectedQuestions.value.push({
      questionId: q.questionId,
      score: q.score,
      sectionIndex: 1
    })
  }
}

function isQuestionSelected(qId: string) {
  return selectedQuestions.value.some(sq => sq.questionId === qId)
}

async function handleAddQuestions() {
  if (!currentPaper.value || selectedQuestions.value.length === 0) {
    ElMessage.warning('请选择至少一道题目')
    return
  }
  try {
    await addPaperQuestions(currentPaper.value.paperId, { questions: selectedQuestions.value })
    ElMessage.success('添加题目成功')
    addQuestionDialogVisible.value = false
    currentPaper.value = await getPaperDetail(currentPaper.value.paperId)
    await loadPapers()
  } catch (error) {
    const msg = error instanceof Error ? error.message : '添加失败'
    ElMessage.error(msg)
  }
}

async function handleRemoveQuestion(pq: PaperQuestionVO) {
  if (!currentPaper.value) return
  try {
    await ElMessageBox.confirm('确认从试卷中移除该题目？', '移除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
    await removePaperQuestion(currentPaper.value.paperId, pq.id)
    ElMessage.success('移除成功')
    currentPaper.value = await getPaperDetail(currentPaper.value.paperId)
    await loadPapers()
  } catch {
    // cancelled
  }
}

function handlePageChange(page: number) {
  paperPage.value = page
  loadPapers()
}

function handleFilterSearch() {
  paperPage.value = 1
  loadPapers()
}

onMounted(loadPapers)
</script>

<template>
  <div class="paper-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/teaching' }">我教的课</el-breadcrumb-item>
            <el-breadcrumb-item>试卷管理</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <div class="toolbar">
        <div class="toolbar-left">
          <el-select v-model="paperStatusFilter" placeholder="状态筛选" clearable size="small" style="width: 120px" @change="handleFilterSearch">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
          </el-select>
          <el-input v-model="paperKeyword" placeholder="搜索试卷名称" clearable size="small" style="width: 200px" @keyup.enter="handleFilterSearch" @clear="handleFilterSearch" />
        </div>
        <el-button type="primary" :icon="Plus" @click="openCreateDialog">创建试卷</el-button>
      </div>

      <el-table v-loading="loading" :data="papers" stripe>
        <el-table-column prop="paperName" label="试卷名称" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="paperStatusMap[row.status]?.type" size="small">
              {{ paperStatusMap[row.status]?.label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="questionCount" label="题目数" width="80" />
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ row.createdAt?.split(' ')[0] }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" link :icon="View" @click="handleViewDetail(row)">详情</el-button>
            <el-button v-if="row.status === PaperStatus.Draft" size="small" link type="success" @click="handlePublish(row)">发布</el-button>
            <el-button v-if="row.status === PaperStatus.Draft" size="small" link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-if="paperTotal > paperPageSize"
        v-model:current-page="paperPage"
        :total="paperTotal"
        :page-size="paperPageSize"
        layout="prev, pager, next"
        style="margin-top: 12px"
        @current-change="handlePageChange"
      />
    </el-card>

    <el-dialog v-model="createDialogVisible" title="创建试卷" width="500px">
      <el-form :model="createForm" label-width="80px">
        <el-form-item label="试卷名称" required>
          <el-input v-model="createForm.paperName" placeholder="请输入试卷名称" />
        </el-form-item>
        <el-form-item label="分节设置">
          <div class="sections-editor">
            <div v-for="(sec, idx) in createForm.sections" :key="idx" class="section-row">
              <el-tag size="small">第{{ sec.index }}节</el-tag>
              <el-input v-model="sec.title" placeholder="节标题" style="flex: 1" />
              <el-button v-if="createForm.sections && createForm.sections.length > 1" size="small" link type="danger" @click="removeSection(idx)">删除</el-button>
            </div>
            <el-button size="small" @click="addSection">添加节</el-button>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确认</el-button>
      </template>
    </el-dialog>

    <el-drawer v-model="detailVisible" :title="currentPaper?.paperName || '试卷详情'" size="60%">
      <div v-loading="detailLoading">
        <template v-if="currentPaper">
          <div class="detail-header">
            <div>
              <el-tag :type="paperStatusMap[currentPaper.status]?.type" size="large">
                {{ paperStatusMap[currentPaper.status]?.label }}
              </el-tag>
              <span style="margin-left: 12px; font-size: 14px; color: #909399">
                题目数：{{ currentPaper.questionCount }} | 总分：{{ currentPaper.totalScore }}
              </span>
            </div>
            <el-button
              v-if="currentPaper.status === PaperStatus.Draft"
              type="primary"
              :icon="Plus"
              @click="openAddQuestion"
            >
              添加题目
            </el-button>
          </div>

          <el-empty v-if="!currentPaper.questions?.length" description="暂无题目" />

          <div v-else class="questions-list">
            <div v-for="(pq, idx) in currentPaper.questions" :key="pq.id" class="question-item">
              <div class="question-header">
                <span class="question-index">{{ idx + 1 }}.</span>
                <el-tag :type="questionTypeMap[pq.question?.questionType] ? 'primary' : 'info'" size="small">
                  {{ questionTypeMap[pq.question?.questionType] || '未知' }}
                </el-tag>
                <span class="question-score">分值：{{ pq.score }}</span>
                <el-button
                  v-if="currentPaper.status === PaperStatus.Draft"
                  size="small"
                  link
                  type="danger"
                  @click="handleRemoveQuestion(pq)"
                >
                  移除
                </el-button>
              </div>
              <div class="question-stem">{{ pq.question?.stem }}</div>
              <div v-if="pq.question?.options?.length" class="question-options">
                <div v-for="opt in pq.question.options" :key="opt.optionId" class="option-item">
                  <span :class="{ correct: opt.isCorrect }">{{ opt.label }}. {{ opt.content }}</span>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </el-drawer>

    <el-dialog v-model="addQuestionDialogVisible" title="从题库添加题目" width="700px">
      <el-input v-model="searchKeyword" placeholder="搜索题干" clearable size="small" style="margin-bottom: 12px" @keyup.enter="searchQuestionsFromBank" @clear="searchQuestionsFromBank" />
      <el-table v-loading="searchLoading" :data="searchQuestions" stripe max-height="400" @row-click="toggleQuestionSelect">
        <el-table-column width="50">
          <template #default="{ row }">
            <el-checkbox :model-value="isQuestionSelected(row.questionId)" />
          </template>
        </el-table-column>
        <el-table-column prop="stem" label="题干" min-width="200" show-overflow-tooltip />
        <el-table-column label="题型" width="80">
          <template #default="{ row }">
            {{ questionTypeMap[row.questionType] }}
          </template>
        </el-table-column>
        <el-table-column prop="score" label="默认分值" width="80" />
      </el-table>
      <div v-if="selectedQuestions.length > 0" style="margin-top: 12px">
        <span>已选 {{ selectedQuestions.length }} 题</span>
        <div v-for="(sq, idx) in selectedQuestions" :key="sq.questionId" style="display: flex; gap: 8px; align-items: center; margin-top: 4px">
          <span>题目{{ idx + 1 }} (ID:{{ sq.questionId }})</span>
          <el-input-number v-model="sq.score" :min="1" size="small" style="width: 100px" />
          <el-input-number v-model="sq.sectionIndex" :min="1" size="small" style="width: 100px" placeholder="节号" />
        </div>
      </div>
      <template #footer>
        <el-button @click="addQuestionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAddQuestions">确认添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.toolbar-left {
  display: flex;
  gap: 8px;
  align-items: center;
}

.sections-editor {
  width: 100%;

  .section-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
  }
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.questions-list {
  .question-item {
    padding: 12px;
    border: 1px solid #ebeef5;
    border-radius: 6px;
    margin-bottom: 8px;
  }

  .question-header {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;

    .question-index {
      font-weight: 600;
    }

    .question-score {
      font-size: 13px;
      color: #909399;
      margin-left: auto;
    }
  }

  .question-stem {
    font-size: 14px;
    color: #303133;
    margin-bottom: 8px;
  }

  .question-options {
    .option-item {
      font-size: 13px;
      color: #606266;
      padding: 2px 0;

      .correct {
        color: #67c23a;
        font-weight: 500;
      }
    }
  }
}
</style>
