<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  getKnowledgeTree,
  createPoint,
  updatePoint,
  deletePoint,
  getPointQuestions,
  bindQuestions,
  unbindQuestion,
  getPointSections,
  bindSections,
  unbindSection
} from '@/api/knowledge/index'
import { getQuestionList } from '@/api/exam/index'
import type {
  KnowledgeTreeVO,
  PointCreateRequest,
  PointUpdateRequest
} from '@/api/knowledge/types'
import type { QuestionVO } from '@/api/exam/types'
import { useCourseStore } from '@/stores/course'

const route = useRoute()
const courseStore = useCourseStore()
const courseId = Number(route.params.id)

const loading = ref(false)
const treeData = ref<KnowledgeTreeVO[]>([])
const selectedPoint = ref<KnowledgeTreeVO | null>(null)
const linkedQuestionIds = ref<number[]>([])
const linkedSectionIds = ref<number[]>([])

const pointDialogVisible = ref(false)
const isEdit = ref(false)
const editingPointId = ref<number | null>(null)
const pointForm = reactive<PointCreateRequest & { pointName: string; description: string }>({
  pointName: '',
  courseId,
  parentId: undefined,
  description: ''
})

const bindQuestionDialogVisible = ref(false)
const searchQuestions = ref<QuestionVO[]>([])
const searchLoading = ref(false)
const selectedQuestionIds = ref<number[]>([])

const bindSectionDialogVisible = ref(false)
const selectedSectionIds = ref<number[]>([])

const treeProps = {
  children: 'children',
  label: 'pointName',
  value: 'pointId'
}

async function loadTree() {
  loading.value = true
  try {
    treeData.value = await getKnowledgeTree(courseId)
  } finally {
    loading.value = false
  }
}

function handleNodeClick(data: KnowledgeTreeVO) {
  selectedPoint.value = data
  loadLinkedData(data.pointId)
}

async function loadLinkedData(pointId: number) {
  try {
    const [qIds, sIds] = await Promise.all([
      getPointQuestions(pointId),
      getPointSections(pointId)
    ])
    linkedQuestionIds.value = qIds
    linkedSectionIds.value = sIds
  } catch {
    linkedQuestionIds.value = []
    linkedSectionIds.value = []
  }
}

function openAddRoot() {
  isEdit.value = false
  editingPointId.value = null
  pointForm.pointName = ''
  pointForm.parentId = undefined
  pointForm.description = ''
  pointDialogVisible.value = true
}

function openAddChild(parent: KnowledgeTreeVO) {
  isEdit.value = false
  editingPointId.value = null
  pointForm.pointName = ''
  pointForm.parentId = parent.pointId
  pointForm.description = ''
  pointDialogVisible.value = true
}

function openEdit(node: KnowledgeTreeVO) {
  isEdit.value = true
  editingPointId.value = node.pointId
  pointForm.pointName = node.pointName
  pointForm.parentId = node.parentId || undefined
  pointForm.description = node.description
  pointDialogVisible.value = true
}

async function handlePointSubmit() {
  if (!pointForm.pointName.trim()) {
    ElMessage.warning('请输入知识点名称')
    return
  }
  try {
    if (isEdit.value && editingPointId.value) {
      await updatePoint(editingPointId.value, {
        pointName: pointForm.pointName,
        description: pointForm.description
      })
      ElMessage.success('修改成功')
    } else {
      await createPoint({
        pointName: pointForm.pointName,
        courseId,
        parentId: pointForm.parentId,
        description: pointForm.description || undefined
      })
      ElMessage.success('创建成功')
    }
    pointDialogVisible.value = false
    await loadTree()
  } catch (error) {
    const msg = error instanceof Error ? error.message : '操作失败'
    ElMessage.error(msg)
  }
}

async function handleDelete(node: KnowledgeTreeVO) {
  try {
    await ElMessageBox.confirm(`确认删除知识点「${node.pointName}」吗？`, '删除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deletePoint(node.pointId)
    ElMessage.success('删除成功')
    if (selectedPoint.value?.pointId === node.pointId) {
      selectedPoint.value = null
    }
    await loadTree()
  } catch {
    // cancelled
  }
}

async function openBindQuestions() {
  if (!selectedPoint.value) return
  selectedQuestionIds.value = []
  bindQuestionDialogVisible.value = true
  searchLoading.value = true
  try {
    const result = await getQuestionList({ current: 1, pageSize: 100 })
    searchQuestions.value = result.records
  } finally {
    searchLoading.value = false
  }
}

function toggleQuestionSelect(qId: number) {
  const idx = selectedQuestionIds.value.indexOf(qId)
  if (idx >= 0) {
    selectedQuestionIds.value.splice(idx, 1)
  } else {
    selectedQuestionIds.value.push(qId)
  }
}

async function handleBindQuestions() {
  if (!selectedPoint.value || selectedQuestionIds.value.length === 0) {
    ElMessage.warning('请选择至少一道题目')
    return
  }
  try {
    await bindQuestions(selectedPoint.value.pointId, { questionIds: selectedQuestionIds.value })
    ElMessage.success('绑定成功')
    bindQuestionDialogVisible.value = false
    await loadLinkedData(selectedPoint.value.pointId)
  } catch (error) {
    const msg = error instanceof Error ? error.message : '绑定失败'
    ElMessage.error(msg)
  }
}

async function handleUnbindQuestion(questionId: number) {
  if (!selectedPoint.value) return
  try {
    await unbindQuestion(selectedPoint.value.pointId, questionId)
    ElMessage.success('解绑成功')
    await loadLinkedData(selectedPoint.value.pointId)
  } catch (error) {
    const msg = error instanceof Error ? error.message : '解绑失败'
    ElMessage.error(msg)
  }
}

function openBindSections() {
  if (!selectedPoint.value) return
  selectedSectionIds.value = []
  bindSectionDialogVisible.value = true
  courseStore.fetchCourseDetail(courseId)
}

function toggleSectionSelect(sId: number) {
  const idx = selectedSectionIds.value.indexOf(sId)
  if (idx >= 0) {
    selectedSectionIds.value.splice(idx, 1)
  } else {
    selectedSectionIds.value.push(sId)
  }
}

async function handleBindSections() {
  if (!selectedPoint.value || selectedSectionIds.value.length === 0) {
    ElMessage.warning('请选择至少一个小节')
    return
  }
  try {
    await bindSections(selectedPoint.value.pointId, { sectionIds: selectedSectionIds.value })
    ElMessage.success('绑定成功')
    bindSectionDialogVisible.value = false
    await loadLinkedData(selectedPoint.value.pointId)
  } catch (error) {
    const msg = error instanceof Error ? error.message : '绑定失败'
    ElMessage.error(msg)
  }
}

async function handleUnbindSection(sectionId: number) {
  if (!selectedPoint.value) return
  try {
    await unbindSection(selectedPoint.value.pointId, sectionId)
    ElMessage.success('解绑成功')
    await loadLinkedData(selectedPoint.value.pointId)
  } catch (error) {
    const msg = error instanceof Error ? error.message : '解绑失败'
    ElMessage.error(msg)
  }
}

function getAllSections(): { sectionId: number; title: string; chapterTitle: string }[] {
  const sections: { sectionId: number; title: string; chapterTitle: string }[] = []
  for (const chapter of courseStore.currentCourse?.chapters || []) {
    for (const section of chapter.sections || []) {
      sections.push({
        sectionId: section.sectionId,
        title: section.title,
        chapterTitle: chapter.title
      })
    }
  }
  return sections
}

onMounted(loadTree)
</script>

<template>
  <div class="knowledge-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/TeachingCourses' }">我教的课</el-breadcrumb-item>
            <el-breadcrumb-item>知识点管理</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <div class="content">
        <div class="left-panel">
          <div class="panel-header">
            <span class="panel-title">知识点树</span>
            <el-button type="primary" :icon="Plus" size="small" @click="openAddRoot">新建根节点</el-button>
          </div>
          <el-scrollbar height="65vh">
            <el-tree
              :data="treeData"
              :props="treeProps"
              node-key="pointId"
              highlight-current
              default-expand-all
              :expand-on-click-node="false"
              @node-click="handleNodeClick"
            >
              <template #default="{ node, data }">
                <div class="tree-node">
                  <span class="node-label">{{ data.pointName }}</span>
                  <span class="node-actions" @click.stop>
                    <el-button size="small" link :icon="Plus" @click="openAddChild(data)" />
                    <el-button size="small" link :icon="Edit" @click="openEdit(data)" />
                    <el-button size="small" link type="danger" :icon="Delete" @click="handleDelete(data)" />
                  </span>
                </div>
              </template>
            </el-tree>
            <el-empty v-if="treeData.length === 0" description="暂无知识点" :image-size="60" />
          </el-scrollbar>
        </div>

        <div class="right-panel">
          <template v-if="selectedPoint">
            <div class="point-info">
              <h3>{{ selectedPoint.pointName }}</h3>
              <p v-if="selectedPoint.description" class="point-desc">{{ selectedPoint.description }}</p>
            </div>

            <el-divider />

            <div class="link-section">
              <div class="section-header">
                <h4>关联题目</h4>
                <el-button type="primary" size="small" @click="openBindQuestions">添加关联</el-button>
              </div>
              <div class="link-list">
                <el-empty v-if="linkedQuestionIds.length === 0" description="暂无关联题目" :image-size="40" />
                <el-tag
                  v-for="qId in linkedQuestionIds"
                  :key="qId"
                  closable
                  style="margin: 0 4px 4px 0"
                  @close="handleUnbindQuestion(qId)"
                >
                  题目 #{{ qId }}
                </el-tag>
              </div>
            </div>

            <el-divider />

            <div class="link-section">
              <div class="section-header">
                <h4>关联章节</h4>
                <el-button type="primary" size="small" @click="openBindSections">添加关联</el-button>
              </div>
              <div class="link-list">
                <el-empty v-if="linkedSectionIds.length === 0" description="暂无关联章节" :image-size="40" />
                <el-tag
                  v-for="sId in linkedSectionIds"
                  :key="sId"
                  closable
                  type="success"
                  style="margin: 0 4px 4px 0"
                  @close="handleUnbindSection(sId)"
                >
                  小节 #{{ sId }}
                </el-tag>
              </div>
            </div>
          </template>

          <el-empty v-else description="请从左侧选择知识点" />
        </div>
      </div>
    </el-card>

    <el-dialog v-model="pointDialogVisible" :title="isEdit ? '编辑知识点' : '新建知识点'" width="400px">
      <el-form :model="pointForm" label-width="80px">
        <el-form-item label="名称" required>
          <el-input v-model="pointForm.pointName" placeholder="请输入知识点名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="pointForm.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pointDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePointSubmit">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="bindQuestionDialogVisible" title="关联题目" width="600px">
      <el-table v-loading="searchLoading" :data="searchQuestions" stripe max-height="400" @row-click="(row: QuestionVO) => toggleQuestionSelect(row.questionId)">
        <el-table-column width="50">
          <template #default="{ row }">
            <el-checkbox :model-value="selectedQuestionIds.includes(row.questionId)" />
          </template>
        </el-table-column>
        <el-table-column prop="stem" label="题干" min-width="200" show-overflow-tooltip />
        <el-table-column prop="questionType" label="题型" width="80" />
        <el-table-column prop="score" label="分值" width="60" />
      </el-table>
      <div v-if="selectedQuestionIds.length > 0" style="margin-top: 8px; color: #409eff">
        已选 {{ selectedQuestionIds.length }} 道题目
      </div>
      <template #footer>
        <el-button @click="bindQuestionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBindQuestions">确认绑定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="bindSectionDialogVisible" title="关联章节" width="500px">
      <div class="section-select-list">
        <div v-for="sec in getAllSections()" :key="sec.sectionId" class="section-select-item">
          <el-checkbox
            :model-value="selectedSectionIds.includes(sec.sectionId)"
            @change="toggleSectionSelect(sec.sectionId)"
          >
            {{ sec.chapterTitle }} / {{ sec.title }}
          </el-checkbox>
        </div>
        <el-empty v-if="getAllSections().length === 0" description="暂无章节" :image-size="40" />
      </div>
      <div v-if="selectedSectionIds.length > 0" style="margin-top: 8px; color: #409eff">
        已选 {{ selectedSectionIds.length }} 个小节
      </div>
      <template #footer>
        <el-button @click="bindSectionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBindSections">确认绑定</el-button>
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
  width: 320px;
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

.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
  padding-right: 8px;

  .node-label {
    font-size: 14px;
    color: #303133;
  }

  .node-actions {
    display: none;
  }

  &:hover .node-actions {
    display: flex;
    gap: 2px;
  }
}

.point-info {
  h3 {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px;
  }

  .point-desc {
    font-size: 14px;
    color: #606266;
    margin: 0;
  }
}

.link-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;

    h4 {
      margin: 0;
      font-size: 15px;
      color: #303133;
    }
  }
}

.section-select-list {
  max-height: 400px;
  overflow-y: auto;

  .section-select-item {
    padding: 6px 0;
  }
}
</style>
