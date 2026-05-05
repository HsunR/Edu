<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { useCourseStore } from '@/stores/course'
import {
  addChapter,
  updateChapter,
  deleteChapter,
  reorderChapters
} from '@/api/course/chapter'
import {
  addSection,
  updateSection,
  deleteSection,
  reorderSections,
  addSectionResource,
  removeSectionResource
} from '@/api/course/section'
import ResourceSelector from '@/components/ResourceSelector/index.vue'
import type {
  ChapterVO,
  SectionVO,
  ChapterCreateRequest,
  SectionCreateRequest,
  SectionResourceAddRequest,
  OrderItem
} from '@/api/course/types'
import type { ResourceVO } from '@/api/resource/types'
import type { FormInstance } from 'element-plus'
import { ResourceType, SectionResourceType, YesNo } from '@/types/enums'

const route = useRoute()
const courseStore = useCourseStore()
const courseId = Number(route.params.id)

const loading = ref(false)
const chapterDialogVisible = ref(false)
const sectionDialogVisible = ref(false)
const isEditChapter = ref(false)
const isEditSection = ref(false)
const editingChapterId = ref<number | null>(null)
const editingSectionId = ref<number | null>(null)
const currentChapterIdForSection = ref<number | null>(null)
const resourceSelectorVisible = ref(false)
const currentSectionIdForResource = ref<number | null>(null)

const resourceTypeMap: Record<ResourceType, SectionResourceType> = {
  [ResourceType.Video]: SectionResourceType.Video,
  [ResourceType.Document]: SectionResourceType.Document,
  [ResourceType.Image]: SectionResourceType.Image
}

const chapterFormRef = ref<FormInstance>()
const sectionFormRef = ref<FormInstance>()

const chapterForm = ref<ChapterCreateRequest>({ title: '' })
const sectionForm = ref<SectionCreateRequest & { isFree: YesNo }>({ title: '', isFree: YesNo.No })

const course = computed(() => courseStore.currentCourse)
const chapters = computed(() => course.value?.chapters || [])

async function loadCourse() {
  loading.value = true
  try {
    await courseStore.fetchCourseDetail(courseId)
  } finally {
    loading.value = false
  }
}

function openAddChapter() {
  isEditChapter.value = false
  editingChapterId.value = null
  chapterForm.value = { title: '' }
  chapterDialogVisible.value = true
}

function openEditChapter(chapter: ChapterVO) {
  isEditChapter.value = true
  editingChapterId.value = chapter.chapterId
  chapterForm.value = { title: chapter.title }
  chapterDialogVisible.value = true
}

async function handleChapterSubmit() {
  if (!chapterForm.value.title.trim()) {
    ElMessage.warning('请输入章节标题')
    return
  }
  try {
    if (isEditChapter.value && editingChapterId.value) {
      await updateChapter(editingChapterId.value, { title: chapterForm.value.title })
      ElMessage.success('修改成功')
    } else {
      await addChapter(courseId, chapterForm.value)
      ElMessage.success('添加成功')
    }
    chapterDialogVisible.value = false
    await loadCourse()
  } catch (error) {
    const msg = error instanceof Error ? error.message : '操作失败'
    ElMessage.error(msg)
  }
}

async function handleDeleteChapter(chapter: ChapterVO) {
  try {
    await ElMessageBox.confirm(
      `确认删除章节「${chapter.title}」吗？删除后其下所有小节和资源关联也将被删除。`,
      '删除确认',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteChapter(chapter.chapterId)
    ElMessage.success('删除成功')
    await loadCourse()
  } catch {
    // cancelled
  }
}

function openAddSection(chapterId: number) {
  isEditSection.value = false
  editingSectionId.value = null
  currentChapterIdForSection.value = chapterId
  sectionForm.value = { title: '', isFree: YesNo.No }
  sectionDialogVisible.value = true
}

function openEditSection(section: SectionVO, chapterId: number) {
  isEditSection.value = true
  editingSectionId.value = section.sectionId
  currentChapterIdForSection.value = chapterId
  sectionForm.value = { title: section.title, isFree: section.isFree }
  sectionDialogVisible.value = true
}

async function handleSectionSubmit() {
  if (!sectionForm.value.title.trim()) {
    ElMessage.warning('请输入小节标题')
    return
  }
  try {
    if (isEditSection.value && editingSectionId.value) {
      await updateSection(editingSectionId.value, {
        title: sectionForm.value.title,
        isFree: sectionForm.value.isFree
      })
      ElMessage.success('修改成功')
    } else if (currentChapterIdForSection.value) {
      await addSection(currentChapterIdForSection.value, sectionForm.value)
      ElMessage.success('添加成功')
    }
    sectionDialogVisible.value = false
    await loadCourse()
  } catch (error) {
    const msg = error instanceof Error ? error.message : '操作失败'
    ElMessage.error(msg)
  }
}

async function handleDeleteSection(section: SectionVO) {
  try {
    await ElMessageBox.confirm(
      `确认删除小节「${section.title}」吗？删除后其资源关联也将被删除。`,
      '删除确认',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteSection(section.sectionId)
    ElMessage.success('删除成功')
    await loadCourse()
  } catch {
    // cancelled
  }
}

function openAddResource(sectionId: number) {
  currentSectionIdForResource.value = sectionId
  resourceSelectorVisible.value = true
}

async function handleResourceSelect(selectedResources: ResourceVO[]) {
  if (!currentSectionIdForResource.value || selectedResources.length === 0) return
  try {
    for (const resource of selectedResources) {
      const data: SectionResourceAddRequest = {
        resourceId: resource.resourceId,
        resourceType: resourceTypeMap[resource.resourceType] || SectionResourceType.Video
      }
      await addSectionResource(currentSectionIdForResource.value, data)
    }
    ElMessage.success(`成功添加 ${selectedResources.length} 个资源`)
    resourceSelectorVisible.value = false
    await loadCourse()
  } catch (error) {
    const msg = error instanceof Error ? error.message : '添加资源失败'
    ElMessage.error(msg)
  }
}

async function handleRemoveResource(sectionId: number, resourceId: number) {
  try {
    await ElMessageBox.confirm('确认移除该资源吗？', '移除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
    await removeSectionResource(sectionId, resourceId)
    ElMessage.success('移除成功')
    await loadCourse()
  } catch {
    // cancelled
  }
}

async function handleChapterDragEnd() {
  const items: OrderItem[] = chapters.value.map((ch, idx) => ({
    id: ch.chapterId,
    orderIndex: idx
  }))
  try {
    await reorderChapters(courseId, items)
  } catch {
    ElMessage.error('排序失败')
    await loadCourse()
  }
}

async function handleSectionDragEnd(chapterId: number, sections: SectionVO[]) {
  const items: OrderItem[] = sections.map((sec, idx) => ({
    id: sec.sectionId,
    orderIndex: idx
  }))
  try {
    await reorderSections(chapterId, items)
  } catch {
    ElMessage.error('排序失败')
    await loadCourse()
  }
}

onMounted(loadCourse)
</script>

<template>
  <div v-loading="loading" class="chapter-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/teaching' }">我教的课</el-breadcrumb-item>
            <el-breadcrumb-item>章节管理</el-breadcrumb-item>
          </el-breadcrumb>
          <el-button type="primary" :icon="Plus" @click="openAddChapter">新建章节</el-button>
        </div>
      </template>

      <el-empty v-if="chapters.length === 0" description="暂无章节，点击上方按钮添加" />

      <el-collapse v-else v-model="expandedChapters">
        <el-collapse-item
          v-for="chapter in chapters"
          :key="chapter.chapterId"
          :name="chapter.chapterId"
        >
          <template #title>
            <div class="chapter-header">
              <span class="chapter-title">{{ chapter.title }}</span>
              <div class="chapter-actions" @click.stop>
                <el-button size="small" :icon="Plus" @click="openAddSection(chapter.chapterId)">
                  添加小节
                </el-button>
                <el-button size="small" :icon="Edit" @click="openEditChapter(chapter)" />
                <el-button size="small" :icon="Delete" type="danger" @click="handleDeleteChapter(chapter)" />
              </div>
            </div>
          </template>

          <div class="sections-list">
            <el-empty v-if="!chapter.sections?.length" description="暂无小节" :image-size="60" />

            <div
              v-for="section in chapter.sections"
              :key="section.sectionId"
              class="section-item"
            >
              <div class="section-info">
                <el-tag v-if="section.isFree === YesNo.Yes" size="small" type="warning" style="margin-right: 8px">免费</el-tag>
                <span class="section-title">{{ section.title }}</span>
                <span class="section-resource-count">
                  {{ section.resources?.length || 0 }} 个资源
                </span>
              </div>
              <div class="section-actions">
                <el-button size="small" link type="primary" @click="openAddResource(section.sectionId)">
                  添加资源
                </el-button>
                <el-button size="small" link @click="openEditSection(section, chapter.chapterId)">
                  编辑
                </el-button>
                <el-button size="small" link type="danger" @click="handleDeleteSection(section)">
                  删除
                </el-button>
              </div>

              <div v-if="section.resources?.length" class="section-resources">
                <div
                  v-for="res in section.resources"
                  :key="res.id || res.resourceId"
                  class="resource-item"
                >
                  <span class="resource-name">{{ res.resourceName || `资源 #${res.resourceId}` }}</span>
                  <el-button
                    size="small"
                    link
                    type="danger"
                    @click="handleRemoveResource(section.sectionId, res.id || res.resourceId)"
                  >
                    移除
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-card>

    <el-dialog v-model="chapterDialogVisible" :title="isEditChapter ? '编辑章节' : '新建章节'" width="400px">
      <el-form ref="chapterFormRef" :model="chapterForm" label-width="80px">
        <el-form-item label="章节标题" prop="title" :rules="[{ required: true, message: '请输入标题' }]">
          <el-input v-model="chapterForm.title" placeholder="请输入章节标题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="chapterDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChapterSubmit">确认</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="sectionDialogVisible" :title="isEditSection ? '编辑小节' : '新建小节'" width="400px">
      <el-form ref="sectionFormRef" :model="sectionForm" label-width="80px">
        <el-form-item label="小节标题" prop="title" :rules="[{ required: true, message: '请输入标题' }]">
          <el-input v-model="sectionForm.title" placeholder="请输入小节标题" />
        </el-form-item>
        <el-form-item label="免费预览">
          <el-switch
            v-model="sectionForm.isFree"
            :active-value="YesNo.Yes"
            :inactive-value="YesNo.No"
            active-text="是"
            inactive-text="否"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="sectionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSectionSubmit">确认</el-button>
      </template>
    </el-dialog>

    <ResourceSelector
      v-model="resourceSelectorVisible"
      :multiple="true"
      @confirm="handleResourceSelect"
    />
  </div>
</template>

<script lang="ts">
export default {
  data() {
    return {
      expandedChapters: [] as number[]
    }
  }
}
</script>

<style scoped lang="scss">
.chapter-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chapter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
  padding-right: 16px;

  .chapter-title {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
  }

  .chapter-actions {
    display: flex;
    gap: 4px;
  }
}

.sections-list {
  padding: 0 16px;
}

.section-item {
  padding: 12px;
  margin-bottom: 8px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  transition: background 0.2s;

  &:hover {
    background: #f5f7fa;
  }
}

.section-info {
  display: flex;
  align-items: center;
  margin-bottom: 8px;

  .section-title {
    font-size: 14px;
    color: #303133;
    flex: 1;
  }

  .section-resource-count {
    font-size: 12px;
    color: #909399;
    margin-left: 12px;
  }
}

.section-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.section-resources {
  background: #fafafa;
  border-radius: 4px;
  padding: 8px;
}

.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 8px;
  border-radius: 4px;

  &:hover {
    background: #f0f2f5;
  }

  .resource-name {
    font-size: 13px;
    color: #606266;
  }
}
</style>
