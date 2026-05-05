<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, ArrowLeft, Check, Close, StarFilled } from '@element-plus/icons-vue'
import {
  getPaperList,
  getPaperDetail,
  createPaper,
  updatePaper,
  deletePaper,
  publishPaper,
  addPaperQuestions,
  removePaperQuestion
} from '@/api/exam/index'
import type {
  PaperVO,
  PaperDetailVO,
  PaperCreateRequest,
  PaperUpdateRequest,
  PaperQuestionVO,
  PaperSection,
  QuestionItem
} from '@/api/exam/types'
import { PaperStatus, QuestionType, Difficulty } from '@/types/enums'
import QuestionSelector from './QuestionSelector.vue'

const route = useRoute()
const courseId = route.params.id as string

const loading = ref(false)
const papers = ref<PaperVO[]>([])
const paperTotal = ref(0)
const paperPage = ref(1)
const paperPageSize = ref(10)
const paperStatusFilter = ref<0 | 1 | undefined>(undefined)
const paperKeyword = ref('')

const currentView = ref<'list' | 'editor'>('list')
const currentPaper = ref<PaperDetailVO | null>(null)
const detailLoading = ref(false)

const isEditingName = ref(false)
const editingName = ref('')
const isEditingSections = ref(false)
const editSections = reactive<{ sections: PaperSection[] }>({ sections: [] })

const questionSelectorVisible = ref(false)
const targetSectionIndex = ref(1)

const createDialogVisible = ref(false)
const createForm = reactive<PaperCreateRequest>({
  paperName: '',
  courseId,
  sections: [{ index: 1, title: '默认节' }]
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

const difficultyLabels: Record<number, string> = {
  [Difficulty.VeryEasy]: '非常简单',
  [Difficulty.Easy]: '简单',
  [Difficulty.Medium]: '中等',
  [Difficulty.Hard]: '困难',
  [Difficulty.VeryHard]: '非常困难'
}

const paperStatusMap: Record<number, { label: string; type: string }> = {
  [PaperStatus.Draft]: { label: '草稿', type: 'info' },
  [PaperStatus.Published]: { label: '已发布', type: 'success' }
}

const isDraft = computed(() => currentPaper.value?.status === PaperStatus.Draft)

const sectionQuestionsMap = computed(() => {
  if (!currentPaper.value?.questions) return new Map<number, PaperQuestionVO[]>()
  const map = new Map<number, PaperQuestionVO[]>()
  for (const pq of currentPaper.value.questions) {
    const sec = pq.sectionIndex || 1
    if (!map.has(sec)) map.set(sec, [])
    map.get(sec)!.push(pq)
  }
  for (const [, qs] of map) {
    qs.sort((a, b) => a.orderIndex - b.orderIndex)
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

const existingQuestionIds = computed(() => {
  if (!currentPaper.value?.questions) return []
  return currentPaper.value.questions.map(pq => pq.questionId)
})

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

async function openPaperEditor(paper: PaperVO) {
  currentView.value = 'editor'
  detailLoading.value = true
  try {
    currentPaper.value = await getPaperDetail(paper.paperId)
  } catch {
    currentPaper.value = null
  } finally {
    detailLoading.value = false
  }
}

function backToList() {
  currentView.value = 'list'
  currentPaper.value = null
  isEditingName.value = false
  isEditingSections.value = false
  loadPapers()
}

async function refreshCurrentPaper() {
  if (!currentPaper.value) return
  try {
    currentPaper.value = await getPaperDetail(currentPaper.value.paperId)
  } catch {
    // ignore
  }
}

function startEditName() {
  if (!isDraft.value) return
  editingName.value = currentPaper.value?.paperName || ''
  isEditingName.value = true
}

async function saveName() {
  if (!currentPaper.value || !editingName.value.trim()) return
  try {
    await updatePaper(currentPaper.value.paperId, { paperName: editingName.value.trim() })
    ElMessage.success('试卷名称已更新')
    isEditingName.value = false
    await refreshCurrentPaper()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  }
}

function cancelEditName() {
  isEditingName.value = false
}

function startEditSections() {
  if (!isDraft.value || !currentPaper.value) return
  editSections.sections = currentPaper.value.sections
    ? currentPaper.value.sections.map(s => ({ ...s }))
    : [{ index: 1, title: '默认节' }]
  isEditingSections.value = true
}

function addEditSection() {
  const nextIndex = (editSections.sections?.length || 0) + 1
  editSections.sections = [...(editSections.sections || []), { index: nextIndex, title: '' }]
}

function removeEditSection(index: number) {
  const sections = [...(editSections.sections || [])]
  sections.splice(index, 1)
  sections.forEach((s, i) => { s.index = i + 1 })
  editSections.sections = sections
}

async function saveSections() {
  if (!currentPaper.value) return
  try {
    await updatePaper(currentPaper.value.paperId, { sections: editSections.sections })
    ElMessage.success('分节设置已更新')
    isEditingSections.value = false
    await refreshCurrentPaper()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '更新失败')
  }
}

function cancelEditSections() {
  isEditingSections.value = false
}

function openAddQuestion(sectionIndex: number) {
  if (!isDraft.value) return
  targetSectionIndex.value = sectionIndex
  questionSelectorVisible.value = true
}

async function handleQuestionsSelected(items: QuestionItem[]) {
  if (!currentPaper.value) return
  const adjustedItems = items.map(item => ({
    ...item,
    sectionIndex: targetSectionIndex.value
  }))
  try {
    await addPaperQuestions(currentPaper.value.paperId, { questions: adjustedItems })
    ElMessage.success('添加题目成功')
    await refreshCurrentPaper()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '添加失败')
  }
}

async function handleRemoveFromSelector(questionId: string) {
  if (!currentPaper.value || !isDraft.value) return
  try {
    await removePaperQuestion(currentPaper.value.paperId, questionId)
    ElMessage.success('移除成功')
    await refreshCurrentPaper()
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '移除失败')
  }
}

async function handleRemoveQuestion(pq: PaperQuestionVO) {
  if (!currentPaper.value || !isDraft.value) return
  try {
    await ElMessageBox.confirm('确认从试卷中移除该题目？', '移除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
    await removePaperQuestion(currentPaper.value.paperId, pq.questionId)
    ElMessage.success('移除成功')
    await refreshCurrentPaper()
  } catch {
    // cancelled
  }
}

async function handlePublish() {
  if (!currentPaper.value) return
  try {
    await ElMessageBox.confirm('发布后试卷题目将被冻结，不可再修改。确认发布？', '发布确认', {
      confirmButtonText: '确认发布',
      cancelButtonText: '取消'
    })
    await publishPaper(currentPaper.value.paperId)
    ElMessage.success('发布成功')
    await refreshCurrentPaper()
  } catch {
    // cancelled
  }
}

async function handlePublishFromList(paper: PaperVO) {
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
    await ElMessageBox.confirm('确认删除该试卷吗？此操作不可恢复。', '删除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deletePaper(paper.paperId)
    ElMessage.success('删除成功')
    backToList()
  } catch {
    // cancelled
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
    ElMessage.error(error instanceof Error ? error.message : '创建失败')
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

function getSectionTitle(sectionIndex: number): string {
  if (!currentPaper.value?.sections) return `第${sectionIndex}节`
  const sec = currentPaper.value.sections.find(s => s.index === sectionIndex)
  return sec?.title || `第${sectionIndex}节`
}

onMounted(loadPapers)
</script>

<template>
  <div class="paper-management">
    <!-- ===== 列表视图 ===== -->
    <template v-if="currentView === 'list'">
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
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <el-button size="small" link type="primary" @click="openPaperEditor(row)">
                {{ row.status === PaperStatus.Draft ? '编辑' : '查看' }}
              </el-button>
              <el-button v-if="row.status === PaperStatus.Draft" size="small" link type="success" @click="handlePublishFromList(row)">发布</el-button>
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
    </template>

    <!-- ===== 编辑器/预览视图 ===== -->
    <template v-if="currentView === 'editor'">
      <div v-loading="detailLoading" class="paper-editor">
        <div class="editor-topbar">
          <el-button :icon="ArrowLeft" @click="backToList">返回列表</el-button>
          <div class="topbar-actions">
            <el-button v-if="isDraft" type="success" @click="handlePublish">发布试卷</el-button>
            <el-button v-if="isDraft" type="danger" :icon="Delete" @click="handleDelete(currentPaper!)">删除试卷</el-button>
          </div>
        </div>

        <template v-if="currentPaper">
          <!-- 试卷头部信息 -->
          <div class="paper-header-card">
            <div class="paper-title-row">
              <template v-if="isEditingName">
                <el-input v-model="editingName" style="max-width: 400px" size="large" @keyup.enter="saveName" />
                <el-button type="primary" :icon="Check" circle size="small" @click="saveName" />
                <el-button :icon="Close" circle size="small" @click="cancelEditName" />
              </template>
              <template v-else>
                <h1 class="paper-title">{{ currentPaper.paperName }}</h1>
                <el-button v-if="isDraft" size="small" link :icon="Edit" @click="startEditName">修改名称</el-button>
              </template>
            </div>
            <div class="paper-meta">
              <el-tag :type="paperStatusMap[currentPaper.status]?.type" size="large">
                {{ paperStatusMap[currentPaper.status]?.label }}
              </el-tag>
              <span class="meta-item">题目数：<strong>{{ currentPaper.questionCount }}</strong></span>
              <span class="meta-item">总分：<strong>{{ currentPaper.totalScore }}</strong></span>
              <span class="meta-item">创建时间：{{ currentPaper.createdAt?.split(' ')[0] }}</span>
            </div>
          </div>

          <!-- 分节设置 -->
          <div class="paper-sections-card">
            <div class="section-card-header">
              <span class="section-card-title">分节设置</span>
              <template v-if="isDraft && !isEditingSections">
                <el-button size="small" link :icon="Edit" @click="startEditSections">编辑分节</el-button>
              </template>
            </div>
            <template v-if="isEditingSections">
              <div class="sections-editor">
                <div v-for="(sec, idx) in editSections.sections" :key="idx" class="section-row">
                  <el-tag size="small">第{{ sec.index }}节</el-tag>
                  <el-input v-model="sec.title" placeholder="节标题" style="flex: 1" />
                  <el-button v-if="editSections.sections && editSections.sections.length > 1" size="small" link type="danger" @click="removeEditSection(idx)">删除</el-button>
                </div>
                <div class="section-actions">
                  <el-button size="small" @click="addEditSection">添加节</el-button>
                  <el-button type="primary" size="small" @click="saveSections">保存</el-button>
                  <el-button size="small" @click="cancelEditSections">取消</el-button>
                </div>
              </div>
            </template>
            <template v-else>
              <div class="sections-display">
                <div v-for="sec in currentPaper.sections" :key="sec.index" class="section-tag">
                  <el-tag type="info" size="large">第{{ sec.index }}节：{{ sec.title || '未命名' }}</el-tag>
                  <span class="section-score" v-if="sectionScoreMap.get(sec.index)">
                    ({{ sectionQuestionsMap.get(sec.index)?.length || 0 }}题 / {{ sectionScoreMap.get(sec.index) }}分)
                  </span>
                </div>
              </div>
            </template>
          </div>

          <!-- 试卷内容 - 按节分组展示 -->
          <div class="paper-body">
            <template v-if="currentPaper.sections && currentPaper.sections.length > 0">
              <div
                v-for="sec in currentPaper.sections"
                :key="sec.index"
                class="paper-section"
              >
                <div class="section-header">
                  <div class="section-title-bar">
                    <span class="section-number">第{{ sec.index }}部分</span>
                    <span class="section-name">{{ sec.title || '未命名' }}</span>
                    <span class="section-stats">
                      (共 {{ sectionQuestionsMap.get(sec.index)?.length || 0 }} 题，
                      {{ sectionScoreMap.get(sec.index) || 0 }} 分)
                    </span>
                  </div>
                  <el-button
                    v-if="isDraft"
                    type="primary"
                    :icon="Plus"
                    size="small"
                    @click="openAddQuestion(sec.index)"
                  >
                    添加题目
                  </el-button>
                </div>

                <div class="section-questions">
                  <template v-if="sectionQuestionsMap.get(sec.index)?.length">
                    <div
                      v-for="(pq, idx) in sectionQuestionsMap.get(sec.index)"
                      :key="pq.id"
                      class="question-card"
                    >
                      <div class="question-top">
                        <span class="question-number">{{ idx + 1 }}.</span>
                        <el-tag :type="questionTypeTagType[pq.question?.questionType]" size="small">
                          {{ questionTypeMap[pq.question?.questionType] || '未知' }}
                        </el-tag>
                        <span class="question-difficulty" v-if="pq.question?.difficulty">
                          <el-icon v-for="i in pq.question.difficulty" :key="i" class="star-icon">
                            <StarFilled />
                          </el-icon>
                        </span>
                        <span class="question-score-badge">{{ pq.score }} 分</span>
                        <el-button
                          v-if="isDraft"
                          size="small"
                          link
                          type="danger"
                          class="question-remove"
                          @click="handleRemoveQuestion(pq)"
                        >
                          移除
                        </el-button>
                      </div>
                      <div class="question-stem">{{ pq.question?.stem }}</div>
                      <div v-if="pq.question?.options?.length" class="question-options">
                        <div v-for="opt in pq.question.options" :key="opt.optionId" class="option-item">
                          <span class="option-label" :class="{ correct: opt.isCorrect }">{{ opt.label }}.</span>
                          <span class="option-content" :class="{ correct: opt.isCorrect }">{{ opt.content }}</span>
                          <el-tag v-if="opt.isCorrect" type="success" size="small" class="correct-tag">正确答案</el-tag>
                        </div>
                      </div>
                      <div v-if="pq.question?.answer && (pq.question.questionType === QuestionType.FillBlank || pq.question.questionType === QuestionType.ShortAnswer)" class="question-answer">
                        <span class="answer-label">参考答案：</span>
                        <span class="answer-content">{{ pq.question.answer }}</span>
                      </div>
                    </div>
                  </template>
                  <template v-else>
                    <div class="section-empty">
                      <span>暂无题目</span>
                      <el-button v-if="isDraft" type="primary" link :icon="Plus" @click="openAddQuestion(sec.index)">添加题目</el-button>
                    </div>
                  </template>
                </div>
              </div>
            </template>
            <template v-else>
              <el-empty description="暂无分节信息">
                <el-button v-if="isDraft" type="primary" @click="startEditSections">设置分节</el-button>
              </el-empty>
            </template>
          </div>
        </template>

        <el-empty v-else-if="!detailLoading" description="试卷信息加载失败" />
      </div>

      <QuestionSelector
        v-model="questionSelectorVisible"
        :course-id="courseId"
        :target-section-index="targetSectionIndex"
        :existing-question-ids="existingQuestionIds"
        @confirm="handleQuestionsSelected"
        @remove="handleRemoveFromSelector"
      />
    </template>
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

  .section-actions {
    display: flex;
    gap: 8px;
    margin-top: 8px;
  }
}

.paper-editor {
  max-width: 960px;
  margin: 0 auto;
}

.editor-topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .topbar-actions {
    display: flex;
    gap: 8px;
  }
}

.paper-header-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .paper-title-row {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 12px;

    .paper-title {
      font-size: 22px;
      font-weight: 700;
      color: #303133;
      margin: 0;
    }
  }

  .paper-meta {
    display: flex;
    align-items: center;
    gap: 16px;
    color: #606266;
    font-size: 14px;

    .meta-item {
      strong {
        color: #303133;
        font-weight: 600;
      }
    }
  }
}

.paper-sections-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px 24px;
  margin-bottom: 16px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .section-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .section-card-title {
      font-size: 15px;
      font-weight: 600;
      color: #303133;
    }
  }

  .sections-display {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;

    .section-tag {
      display: flex;
      align-items: center;
      gap: 6px;
    }

    .section-score {
      font-size: 12px;
      color: #909399;
    }
  }
}

.paper-body {
  .paper-section {
    background: #fff;
    border-radius: 8px;
    margin-bottom: 16px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
    overflow: hidden;
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 14px 20px;
    background: #f5f7fa;
    border-bottom: 1px solid #ebeef5;

    .section-title-bar {
      display: flex;
      align-items: baseline;
      gap: 8px;

      .section-number {
        font-size: 16px;
        font-weight: 700;
        color: #303133;
      }

      .section-name {
        font-size: 15px;
        font-weight: 600;
        color: #303133;
      }

      .section-stats {
        font-size: 13px;
        color: #909399;
      }
    }
  }

  .section-questions {
    padding: 16px 20px;
  }

  .section-empty {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding: 24px;
    color: #909399;
    font-size: 14px;
  }
}

.question-card {
  padding: 14px 16px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  margin-bottom: 10px;
  transition: border-color 0.2s;

  &:hover {
    border-color: #c0c4cc;
  }

  &:last-child {
    margin-bottom: 0;
  }

  .question-top {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;

    .question-number {
      font-size: 15px;
      font-weight: 700;
      color: #303133;
      min-width: 24px;
    }

    .question-difficulty {
      display: inline-flex;
      align-items: center;
      gap: 2px;

      .star-icon {
        font-size: 12px;
        color: #f7ba2a;
      }
    }

    .question-score-badge {
      font-size: 13px;
      font-weight: 600;
      color: #e6a23c;
      margin-left: auto;
    }

    .question-remove {
      margin-left: 4px;
    }
  }

  .question-stem {
    font-size: 14px;
    color: #303133;
    line-height: 1.6;
    margin-bottom: 8px;
    padding-left: 28px;
  }

  .question-options {
    padding-left: 28px;

    .option-item {
      display: flex;
      align-items: flex-start;
      gap: 4px;
      padding: 3px 0;
      font-size: 13px;
      color: #606266;
      line-height: 1.5;

      .option-label {
        font-weight: 500;
        min-width: 20px;

        &.correct {
          color: #67c23a;
          font-weight: 700;
        }
      }

      .option-content {
        &.correct {
          color: #67c23a;
          font-weight: 500;
        }
      }

      .correct-tag {
        margin-left: 6px;
        transform: scale(0.85);
      }
    }
  }

  .question-answer {
    padding-left: 28px;
    margin-top: 8px;
    font-size: 13px;

    .answer-label {
      color: #909399;
    }

    .answer-content {
      color: #409eff;
    }
  }
}
</style>
