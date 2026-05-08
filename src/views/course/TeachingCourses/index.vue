<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCourseStore } from '@/stores/course'
import { createCourse, updateCourse, deleteCourse, publishCourse, archiveCourse } from '@/api/course/course'
import type { CourseVO, CourseCreateRequest, CourseUpdateRequest } from '@/api/course/types'
import { CourseStatus, YesNo } from '@/types/enums'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import axios from 'axios'
import { presignImage, confirmUpload } from '@/api/resource/resource'
import CourseCard from '../components/CourseCard.vue'

const courseStore = useCourseStore()

const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(12)

const dialogFormVisible = ref(false)
const isEdit = ref(false)
const courseFormRef = ref<FormInstance>()
const uploadRef = ref()
const uploadKey = ref(0)
const editingCourseId = ref<string | null>(null)

const CourseForm = reactive<CourseCreateRequest>({
  courseName: '',
  description: '',
  coverUrl: '',
  categoryId: undefined,
  isPublic: undefined
})

const DRAFT_STORAGE_KEY = 'course_create_draft'

function saveDraft() {
  const draft = {
    courseName: CourseForm.courseName,
    description: CourseForm.description,
    coverUrl: CourseForm.coverUrl,
    categoryId: CourseForm.categoryId,
    isPublic: CourseForm.isPublic,
    imageUrl: imageUrl.value
  }
  localStorage.setItem(DRAFT_STORAGE_KEY, JSON.stringify(draft))
}

function restoreDraft() {
  const draftStr = localStorage.getItem(DRAFT_STORAGE_KEY)
  if (!draftStr) return false
  try {
    const draft = JSON.parse(draftStr)
    CourseForm.courseName = draft.courseName || ''
    CourseForm.description = draft.description || ''
    CourseForm.coverUrl = draft.coverUrl || ''
    CourseForm.categoryId = draft.categoryId
    CourseForm.isPublic = draft.isPublic
    imageUrl.value = draft.imageUrl || ''
    return true
  } catch {
    return false
  }
}

function clearDraft() {
  localStorage.removeItem(DRAFT_STORAGE_KEY)
}

const formRules = reactive<FormRules>({
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }]
})

const imageUrl = ref('')
const selectedFile = ref<File | null>(null)
const uploadStatus = ref<'idle' | 'uploading' | 'confirming' | 'success' | 'error'>('idle')
const errorMessage = ref('')

async function loadTeachingCourses() {
  loading.value = true
  try {
    await courseStore.fetchTeachingCourses({
      current: currentPage.value,
      pageSize: pageSize.value
    })
  } finally {
    loading.value = false
  }
}

function handlePageChange(page: number) {
  currentPage.value = page
  loadTeachingCourses()
}

function addCourse() {
  isEdit.value = false
  editingCourseId.value = null
  dialogFormVisible.value = true
  const hasDraft = restoreDraft()
  if (!hasDraft) {
    resetFormFields()
  }
}

async function editCourse(course: CourseVO) {
  isEdit.value = true
  editingCourseId.value = course.courseId
  CourseForm.courseName = course.courseName
  CourseForm.description = course.description
  CourseForm.coverUrl = course.coverUrl
  CourseForm.categoryId = course.categoryId || undefined
  CourseForm.isPublic = course.isPublic ?? undefined
  imageUrl.value = course.coverUrl || ''
  dialogFormVisible.value = true
}

async function handleDelete(course: CourseVO) {
  if (course.status !== CourseStatus.Draft) {
    ElMessage.warning('仅草稿状态课程可删除')
    return
  }
  try {
    await ElMessageBox.confirm('确认删除该课程吗？删除后不可恢复。', '删除确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteCourse(course.courseId)
    ElMessage.success('删除成功')
    await loadTeachingCourses()
  } catch {
    // cancelled or error
  }
}

async function handlePublish(course: CourseVO) {
  try {
    await ElMessageBox.confirm('确认发布该课程吗？', '发布确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
    await publishCourse(course.courseId)
    ElMessage.success('发布成功')
    await loadTeachingCourses()
  } catch {
    // cancelled or error
  }
}

async function handleArchive(course: CourseVO) {
  try {
    await ElMessageBox.confirm('确认归档该课程吗？', '归档确认', {
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
    await archiveCourse(course.courseId)
    ElMessage.success('归档成功')
    await loadTeachingCourses()
  } catch {
    // cancelled or error
  }
}

const handleFileChange: UploadProps['onChange'] = (file) => {
  if (file.raw) {
    selectedFile.value = file.raw
    imageUrl.value = URL.createObjectURL(file.raw)
  }
}

async function handleUpload() {
  if (!selectedFile.value) return
  uploadStatus.value = 'uploading'
  errorMessage.value = ''
  try {
    const presignData = await presignImage({
      fileName: selectedFile.value.name,
      fileSize: selectedFile.value.size
    })
    if (!presignData?.uploadUrl) throw new Error('获取预签名URL失败')
    const { resourceId, uploadUrl, accessUrl } = presignData
    CourseForm.coverUrl = accessUrl
    await axios.put(uploadUrl, selectedFile.value, {
      headers: { 'Content-Type': selectedFile.value.type }
    })
    uploadStatus.value = 'confirming'
    await confirmUpload({
      resourceId
    })
    uploadStatus.value = 'success'
  } catch (err) {
    const msg = err instanceof Error ? err.message : '上传失败'
    errorMessage.value = msg
    uploadStatus.value = 'error'
  }
}

function handleRemoveCover() {
  imageUrl.value = ''
  CourseForm.coverUrl = ''
  selectedFile.value = null
  uploadStatus.value = 'idle'
  errorMessage.value = ''
  uploadKey.value++
}

function handleCancel() {
  dialogFormVisible.value = false
  if (!isEdit.value) {
    saveDraft()
  } else {
    resetFormFields()
  }
}

function handleDialogClose() {
  if (!isEdit.value) {
    saveDraft()
  } else {
    resetFormFields()
  }
}

function resetFormFields() {
  CourseForm.courseName = ''
  CourseForm.description = ''
  CourseForm.coverUrl = ''
  CourseForm.categoryId = undefined
  CourseForm.isPublic = undefined
  imageUrl.value = ''
  selectedFile.value = null
  uploadStatus.value = 'idle'
  errorMessage.value = ''
  uploadKey.value++
}

async function handleSubmit() {
  if (!courseFormRef.value) return
  await courseFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (isEdit.value && editingCourseId.value) {
        const data: CourseUpdateRequest = {
          courseName: CourseForm.courseName,
          description: CourseForm.description,
          coverUrl: CourseForm.coverUrl,
          categoryId: CourseForm.categoryId,
          isPublic: CourseForm.isPublic
        }
        await updateCourse(editingCourseId.value, data)
        ElMessage.success('修改成功')
      } else {
        await createCourse(CourseForm as CourseCreateRequest)
        ElMessage.success('创建成功')
        clearDraft()
      }
      dialogFormVisible.value = false
      resetFormFields()
      await loadTeachingCourses()
    } catch (error) {
      const msg = error instanceof Error ? error.message : '操作失败'
      ElMessage.error(msg)
    }
  })
}

onMounted(async () => {
  await courseStore.fetchCategoryTree()
  await loadTeachingCourses()
})
</script>

<template>
  <div class="teaching-courses">
    <div class="page-header">
      <h2 class="page-title">我教的课</h2>
      <el-button type="primary" :icon="Plus" @click="addCourse">添加课程</el-button>
    </div>

    <div v-loading="loading" class="courses-grid">
      <el-empty v-if="!loading && courseStore.teachingCourses.length === 0" description="暂无课程，点击上方按钮创建" />

      <CourseCard
        v-for="course in courseStore.teachingCourses"
        :key="course.courseId"
        :course="course"
        mode="teaching"
      />
    </div>

    <div v-if="courseStore.teachingTotal > pageSize" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        :total="courseStore.teachingTotal"
        :page-size="pageSize"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>

    <el-dialog
      v-model="dialogFormVisible"
      :title="isEdit ? '编辑课程' : '新增课程'"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="courseFormRef" :model="CourseForm" :rules="formRules" label-width="100px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="CourseForm.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程封面" prop="coverUrl">
          <el-upload
            ref="uploadRef"
            :key="uploadKey"
            class="avatar-uploader"
            action=""
            :auto-upload="false"
            :on-change="handleFileChange"
            :show-file-list="false"
            :limit="1"
          >
            <img v-if="imageUrl" :src="imageUrl" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            <template #tip>
              <div v-if="imageUrl" class="upload-actions">
                <el-button size="small" @click.stop="handleRemoveCover">删除封面</el-button>
                <el-button size="small" type="primary" @click.stop="handleUpload">上传封面</el-button>
              </div>
              <div v-if="uploadStatus === 'uploading'" class="upload-status">正在上传到云端...</div>
              <div v-if="uploadStatus === 'confirming'" class="upload-status">正在确认资源...</div>
              <div v-if="uploadStatus === 'success'" class="upload-status success">上传成功！</div>
              <div v-if="uploadStatus === 'error'" class="upload-status error">上传失败: {{ errorMessage }}</div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input
            v-model="CourseForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入课程描述"
          />
        </el-form-item>
        <el-form-item label="课程分类" prop="categoryId">
          <el-tree-select
            v-model="CourseForm.categoryId"
            :data="courseStore.categoryTree"
            :props="{ children: 'children', label: 'name' }"
            node-key="categoryId"
            placeholder="请选择课程分类"
            check-strictly
            clearable
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="是否公开" prop="isPublic">
          <el-radio-group v-model="CourseForm.isPublic">
            <el-radio :value="YesNo.No">私有</el-radio>
            <el-radio :value="YesNo.Yes">公开</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.teaching-courses {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.courses-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
  min-height: 200px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: border-color 0.3s;

    &:hover {
      border-color: #409eff;
    }
  }

  .avatar {
    width: 178px;
    height: 178px;
    display: block;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 178px;
    height: 178px;
    line-height: 178px;
    text-align: center;
  }
}

.upload-actions {
  margin-top: 8px;
  display: flex;
  gap: 8px;
}

.upload-status {
  margin-top: 8px;
  font-size: 13px;
  color: #409eff;

  &.success {
    color: #67c23a;
  }

  &.error {
    color: #f56c6c;
  }
}
</style>
