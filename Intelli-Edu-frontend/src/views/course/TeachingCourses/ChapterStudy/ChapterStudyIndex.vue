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
  removeSectionResource,
  getSectionDetail
} from '@/api/course/section'
import ResourceSelector from '@/components/ResourceSelector/index.vue'
import type {
  ChapterVO,
  SectionVO,
  SectionDetailVO,
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
const courseId = route.params.id as string

const loading = ref(false)
const sectionLoading = ref(false)
const chapterDialogVisible = ref(false)
const sectionDialogVisible = ref(false)
const isEditChapter = ref(false)
const isEditSection = ref(false)
const editingChapterId = ref<string | null>(null)
const editingSectionId = ref<string | null>(null)
const currentChapterIdForSection = ref<string | null>(null)
const resourceSelectorVisible = ref(false)
const currentSectionIdForResource = ref<string | null>(null)
const currentSectionDetail = ref<SectionDetailVO | null>(null)

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

function getSectionOrder(chapterIndex: number, sectionIndex: number): number {
  let order = 0
  for (let i = 0; i < chapterIndex; i++) {
    order += chapters.value[i]?.sections?.length || 0
  }
  return order + sectionIndex + 1
}

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
    if (currentSectionDetail.value?.chapterId === chapter.chapterId) {
      currentSectionDetail.value = null
    }
    await loadCourse()
  } catch {
    // cancelled
  }
}

function openAddSection(chapterId: string) {
  isEditSection.value = false
  editingSectionId.value = null
  currentChapterIdForSection.value = chapterId
  sectionForm.value = { title: '', isFree: YesNo.No }
  sectionDialogVisible.value = true
}

function openEditSection(section: SectionVO, chapterId: string) {
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
    if (currentSectionDetail.value?.sectionId === section.sectionId) {
      currentSectionDetail.value = null
    }
    await loadCourse()
  } catch {
    // cancelled
  }
}

function openAddResource(id: string, isChapter = false) {
  if (isChapter) {
    const chapter = chapters.value.find(c => c.chapterId === id)
    if (!chapter || !chapter.sections?.length) {
      ElMessage.warning('该章节暂无小节，请先添加小节')
      return
    }
    currentSectionIdForResource.value = chapter.sections[0].sectionId
  } else {
    currentSectionIdForResource.value = id
  }
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
    if (currentSectionDetail.value?.sectionId === currentSectionIdForResource.value) {
      currentSectionDetail.value = await getSectionDetail(currentSectionIdForResource.value)
    }
    await loadCourse()
  } catch (error) {
    const msg = error instanceof Error ? error.message : '添加资源失败'
    ElMessage.error(msg)
  }
}

async function handleRemoveResource(sectionId: string, resourceId: string) {
  try {
    await ElMessageBox.confirm('确认移除该资源吗？', '移除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
    await removeSectionResource(sectionId, resourceId)
    ElMessage.success('移除成功')
    if (currentSectionDetail.value?.sectionId === sectionId) {
      currentSectionDetail.value = await getSectionDetail(sectionId)
    }
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

async function handleSectionDragEnd(chapterId: string, sections: SectionVO[]) {
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

async function handleSectionClick(section: SectionVO) {
  sectionLoading.value = true
  try {
    currentSectionDetail.value = await getSectionDetail(section.sectionId)
  } catch {
    currentSectionDetail.value = null
  } finally {
    sectionLoading.value = false
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
        </div>
      </template>

      <div class="study-content">
        <div class="chapter-sidebar">
          <div class="sidebar-header">
            <h3 class="sidebar-title">{{ course?.courseName || '课程目录' }}</h3>
            <el-button type="primary" :icon="Plus" size="small" @click="openAddChapter">新建章节</el-button>
          </div>
          <el-scrollbar height="70vh">
            <el-empty v-if="chapters.length === 0" description="暂无章节，点击上方按钮添加" :image-size="60" />

            <div v-for="(chapter, chapterIndex) in chapters" :key="chapter.chapterId" class="chapter-group">
              <div class="chapter-header">
                <span class="chapter-label">{{ chapter.title }}</span>
                <div class="chapter-actions" @click.stop>
                  <el-button size="small" :icon="Plus" circle @click="openAddSection(chapter.chapterId)" />
                  <el-button size="small" :icon="Edit" circle @click="openEditChapter(chapter)" />
                  <el-button size="small" :icon="Delete" type="danger" circle @click="handleDeleteChapter(chapter)" />
                </div>
              </div>

              <div
                v-for="(section, sectionIndex) in chapter.sections"
                :key="section.sectionId"
                class="section-link"
                :class="{ active: currentSectionDetail?.sectionId === section.sectionId }"
                @click="handleSectionClick(section)"
              >
                <i class="order-badge">{{ getSectionOrder(chapterIndex, sectionIndex) }}</i>
                <el-tag v-if="section.isFree === YesNo.Yes" size="small" type="warning" style="margin-right: 6px">免费</el-tag>
                <span class="section-title-text">{{ section.title }}</span>
                <div class="section-actions" @click.stop>
                  <el-button size="small" link type="primary" @click="openAddResource(section.sectionId)">添加资源</el-button>
                  <el-button size="small" link @click="openEditSection(section, chapter.chapterId)">编辑</el-button>
                  <el-button size="small" link type="danger" @click="handleDeleteSection(section)">删除</el-button>
                </div>
              </div>

              <el-empty
                v-if="!chapter.sections?.length"
                description="暂无小节"
                :image-size="40"
              />
            </div>
          </el-scrollbar>
        </div>

        <div class="section-detail" v-loading="sectionLoading">
          <template v-if="currentSectionDetail">
            <h2 class="section-title">{{ currentSectionDetail.title }}</h2>

            <div class="section-toolbar">
              <el-button size="small" type="primary" :icon="Plus" @click="openAddResource(currentSectionDetail!.sectionId)">
                添加资源
              </el-button>
            </div>

            <div v-if="currentSectionDetail.resourceDetails?.length" class="resources-list">
              <h4>资源列表（{{ currentSectionDetail.resourceDetails.length }}）</h4>
              <div
                v-for="res in currentSectionDetail.resourceDetails"
                :key="res.resourceId"
                class="resource-item"
              >
                <div class="resource-info">
                  <el-tag
                    :type="res.resourceType === ResourceType.Video ? 'danger' : res.resourceType === ResourceType.Document ? undefined : 'success'"
                    size="small"
                  >
                    {{ res.resourceType === ResourceType.Video ? '视频' : res.resourceType === ResourceType.Document ? '文档' : '图片' }}
                  </el-tag>
                  <span class="resource-name">{{ res.resourceName }}</span>
                  <span class="resource-format">({{ res.fileFormat }})</span>
                </div>
                <div class="resource-actions">
                  <el-button
                    size="small"
                    link
                    type="danger"
                    @click="handleRemoveResource(currentSectionDetail!.sectionId, res.resourceId)"
                  >
                    移除
                  </el-button>
                </div>
              </div>
            </div>

            <el-empty v-else description="该小节暂无资源" />
          </template>

          <el-empty v-else description="请从左侧目录选择小节查看和管理资源" />
        </div>
      </div>
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

<style scoped lang="scss">
.chapter-management {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.study-content {
  display: flex;
  gap: 20px;
}

.chapter-sidebar {
  width: 280px;
  flex-shrink: 0;
  border-right: 1px solid #ebeef5;
  padding-right: 16px;

  .sidebar-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 12px;
    gap: 8px;
  }

  .sidebar-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0;
    padding-bottom: 8px;
    border-bottom: 1px solid #ebeef5;
    flex: 1;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.chapter-group {
  margin-bottom: 16px;

  .chapter-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 4px;
    gap: 4px;

    .chapter-label {
      font-size: 14px;
      font-weight: 600;
      color: #303133;
      padding: 6px 0;
      flex-shrink: 0;
    }

    .chapter-actions {
      display: flex;
      align-items: center;
      gap: 2px;
      flex-shrink: 0;
    }
  }
}

.section-link {
  display: flex;
  align-items: center;
  padding: 6px 8px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
  font-size: 13px;
  color: #606266;
  margin: 2px 0;

  .order-badge {
    background-color: #ffa500;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 22px;
    height: 22px;
    min-width: 22px;
    margin-right: 6px;
    border-radius: 50%;
    font-size: 11px;
    color: #ffffff;
    font-style: normal;
    font-weight: 500;
  }

  .section-title-text {
    flex: 1;
    min-width: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .section-actions {
    display: flex;
    align-items: center;
    gap: 2px;
    flex-shrink: 0;
    opacity: 0;
    transition: opacity 0.2s;
  }

  &:hover {
    background: #f5f7fa;

    .section-actions {
      opacity: 1;
    }
  }

  &.active {
    background: #ecf5ff;
    color: #409eff;

    .order-badge {
      background-color: #409eff;
    }
  }
}

.section-detail {
  flex: 1;
  min-width: 0;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px;
}

.section-toolbar {
  margin-bottom: 16px;
}

.resources-list {
  h4 {
    font-size: 15px;
    font-weight: 500;
    color: #303133;
    margin: 0 0 12px;
  }
}

.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  margin-bottom: 8px;

  &:hover {
    background: #f5f7fa;
  }

  .resource-info {
    display: flex;
    align-items: center;
    gap: 8px;

    .resource-name {
      font-size: 14px;
      color: #303133;
    }

    .resource-format {
      font-size: 12px;
      color: #909399;
    }
  }
}
</style>
