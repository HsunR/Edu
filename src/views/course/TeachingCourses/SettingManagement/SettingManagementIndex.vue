<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { useCourseStore } from '@/stores/course'
import { updateCourse, publishCourse, archiveCourse } from '@/api/course/course'
import type { CourseUpdateRequest } from '@/api/course/types'
import { CourseStatus } from '@/types/enums'
import { presignImage, confirmUpload } from '@/api/resource/resource'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import axios from 'axios'

const route = useRoute()
const courseStore = useCourseStore()
const courseId = Number(route.params.id)

const loading = ref(false)
const saving = ref(false)
const formRef = ref<FormInstance>()

const form = reactive<CourseUpdateRequest & { isPublic: 0 | 1 | null }>({
  courseName: '',
  description: '',
  coverUrl: '',
  categoryId: undefined,
  isPublic: null
})

const formRules = reactive<FormRules>({
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }]
})

const imageUrl = ref('')
const selectedFile = ref<File | null>(null)
const uploadStatus = ref<'idle' | 'uploading' | 'confirming' | 'success' | 'error'>('idle')
const errorMessage = ref('')

const course = computed(() => courseStore.currentCourse)

const statusMap: Record<number, { label: string; type: 'info' | 'success' | 'warning'; desc: string }> = {
  [CourseStatus.Draft]: { label: '草稿', type: 'info', desc: '课程尚未发布，仅自己可见' },
  [CourseStatus.Published]: { label: '已发布', type: 'success', desc: '课程已发布，学生可浏览' },
  [CourseStatus.Archived]: { label: '已归档', type: 'warning', desc: '课程已归档，不再展示' }
}

async function loadCourse() {
  loading.value = true
  try {
    await courseStore.fetchCourseDetail(courseId)
    await courseStore.fetchCategoryTree()
    if (course.value) {
      form.courseName = course.value.courseName
      form.description = course.value.description
      form.coverUrl = course.value.coverUrl
      form.categoryId = course.value.categoryId || undefined
      form.isPublic = course.value.isPublic
      imageUrl.value = course.value.coverUrl || ''
    }
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saving.value = true
    try {
      await updateCourse(courseId, {
        courseName: form.courseName,
        description: form.description,
        coverUrl: form.coverUrl,
        categoryId: form.categoryId,
        isPublic: form.isPublic ?? undefined
      })
      ElMessage.success('保存成功')
      await courseStore.fetchCourseDetail(courseId)
    } catch (error) {
      const msg = error instanceof Error ? error.message : '保存失败'
      ElMessage.error(msg)
    } finally {
      saving.value = false
    }
  })
}

async function handlePublish() {
  try {
    await ElMessageBox.confirm('确认发布该课程吗？发布后学生将可浏览。', '发布确认', {
      confirmButtonText: '确认发布',
      cancelButtonText: '取消'
    })
    await publishCourse(courseId)
    ElMessage.success('发布成功')
    await courseStore.fetchCourseDetail(courseId)
  } catch {
    // cancelled
  }
}

async function handleArchive() {
  try {
    await ElMessageBox.confirm('确认归档该课程吗？归档后课程将不再展示。', '归档确认', {
      confirmButtonText: '确认归档',
      cancelButtonText: '取消'
    })
    await archiveCourse(courseId)
    ElMessage.success('归档成功')
    await courseStore.fetchCourseDetail(courseId)
  } catch {
    // cancelled
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
    form.coverUrl = accessUrl
    await axios.put(uploadUrl, selectedFile.value, {
      headers: { 'Content-Type': selectedFile.value.type }
    })
    uploadStatus.value = 'confirming'
    await confirmUpload({ resourceId })
    uploadStatus.value = 'success'
  } catch (err) {
    const msg = err instanceof Error ? err.message : '上传失败'
    errorMessage.value = msg
    uploadStatus.value = 'error'
  }
}

function handleRemoveCover() {
  imageUrl.value = ''
  form.coverUrl = ''
  selectedFile.value = null
  uploadStatus.value = 'idle'
  errorMessage.value = ''
}

onMounted(loadCourse)
</script>

<template>
  <div v-loading="loading" class="course-settings">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/TeachingCourses' }">我教的课</el-breadcrumb-item>
            <el-breadcrumb-item>课程设置</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <template v-if="course">
        <div class="status-bar">
          <div class="status-info">
            <el-tag :type="statusMap[course.status]?.type || 'info'" size="large">
              {{ statusMap[course.status]?.label || '未知' }}
            </el-tag>
            <span class="status-desc">{{ statusMap[course.status]?.desc }}</span>
          </div>
          <div class="status-actions">
            <el-button
              v-if="course.status === CourseStatus.Draft"
              type="success"
              @click="handlePublish"
            >
              发布课程
            </el-button>
            <el-button
              v-if="course.status === CourseStatus.Published"
              type="warning"
              @click="handleArchive"
            >
              归档课程
            </el-button>
          </div>
        </div>

        <el-divider />

        <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px" style="max-width: 600px">
          <el-form-item label="课程名称" prop="courseName">
            <el-input v-model="form.courseName" placeholder="请输入课程名称" />
          </el-form-item>

          <el-form-item label="课程封面" prop="coverUrl">
            <el-upload
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
                <div v-if="uploadStatus === 'uploading'" class="upload-status">正在上传...</div>
                <div v-if="uploadStatus === 'success'" class="upload-status success">上传成功！</div>
                <div v-if="uploadStatus === 'error'" class="upload-status error">上传失败: {{ errorMessage }}</div>
              </template>
            </el-upload>
          </el-form-item>

          <el-form-item label="课程描述" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入课程描述" />
          </el-form-item>

          <el-form-item label="课程分类" prop="categoryId">
            <el-tree-select
              v-model="form.categoryId"
              :data="courseStore.categoryTree"
              :props="{ children: 'children', label: 'name', value: 'categoryId' }"
              placeholder="请选择课程分类"
              check-strictly
              clearable
              style="width: 100%"
            />
          </el-form-item>

          <el-form-item label="是否公开" prop="isPublic">
            <el-radio-group v-model="form.isPublic">
              <el-radio :value="0">私有</el-radio>
              <el-radio :value="1">公开</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" :loading="saving" @click="handleSave">保存设置</el-button>
          </el-form-item>
        </el-form>
      </template>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.course-settings {
  padding: 0;
}

.status-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 12px;

  .status-desc {
    font-size: 14px;
    color: #909399;
  }
}

.avatar-uploader {
  :deep(.el-upload) {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
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
