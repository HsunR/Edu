<script setup lang="ts">
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { presignDocument, presignImage, presignVideo, confirmUpload, confirmVideoUpload } from '@/api/resource/resource'
import type { ResourceVO, PresignedUrlVO, VodPresignedUrlVO } from '@/api/resource/types'
import axios from 'axios'

const props = withDefaults(defineProps<{
  accept?: string
  resourceType: 1 | 2 | 3
  maxFileSize?: number
  autoUpload?: boolean
}>(), {
  accept: '',
  maxFileSize: 500 * 1024 * 1024,
  autoUpload: true
})

const emit = defineEmits<{
  success: [resource: ResourceVO]
  error: [message: string]
}>()

const uploading = ref(false)
const progress = ref(0)
const fileName = ref('')

const acceptComputed = computed(() => {
  if (props.accept) return props.accept
  if (props.resourceType === 1) return 'video/*'
  if (props.resourceType === 2) return '.pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.txt'
  return 'image/*'
})

const typeLabel = computed(() => {
  if (props.resourceType === 1) return '视频'
  if (props.resourceType === 2) return '文档'
  return '图片'
})

async function handleUpload(file: File) {
  if (file.size > props.maxFileSize) {
    const msg = `文件大小超过限制（${(props.maxFileSize / 1024 / 1024).toFixed(0)}MB）`
    ElMessage.warning(msg)
    emit('error', msg)
    return
  }

  uploading.value = true
  progress.value = 10
  fileName.value = file.name

  try {
    let presignResult: PresignedUrlVO | VodPresignedUrlVO

    if (props.resourceType === 1) {
      presignResult = await presignVideo({ fileName: file.name, fileSize: file.size })
    } else if (props.resourceType === 2) {
      presignResult = await presignDocument({ fileName: file.name, fileSize: file.size })
    } else {
      presignResult = await presignImage({ fileName: file.name, fileSize: file.size })
    }

    progress.value = 40

    if (props.resourceType === 1) {
      const vodResult = presignResult as VodPresignedUrlVO
      if (vodResult.mediaUploadUrls?.length > 0) {
        await axios.put(vodResult.mediaUploadUrls[0], file, {
          headers: { 'Content-Type': file.type },
          onUploadProgress: (e) => {
            if (e.total) {
              progress.value = 40 + Math.floor((e.loaded / e.total) * 40)
            }
          }
        })
      }
      progress.value = 85
      const result = await confirmVideoUpload({
        resourceId: vodResult.resourceId,
        vodSessionKey: vodResult.vodSessionKey
      })
      progress.value = 100
      ElMessage.success('视频上传成功')
      emit('success', result)
    } else {
      const docResult = presignResult as PresignedUrlVO
      await axios.put(docResult.uploadUrl, file, {
        headers: { 'Content-Type': file.type },
        onUploadProgress: (e) => {
          if (e.total) {
            progress.value = 40 + Math.floor((e.loaded / e.total) * 40)
          }
        }
      })
      progress.value = 85
      const result = await confirmUpload({ resourceId: docResult.resourceId })
      progress.value = 100
      ElMessage.success(`${typeLabel.value}上传成功`)
      emit('success', result)
    }
  } catch (error) {
    const msg = error instanceof Error ? error.message : '上传失败'
    ElMessage.error(msg)
    emit('error', msg)
  } finally {
    uploading.value = false
    progress.value = 0
    fileName.value = ''
  }
}

function handleFileChange(uploadFile: any) {
  if (uploadFile.raw && props.autoUpload) {
    handleUpload(uploadFile.raw)
  }
}

function handleExceed() {
  ElMessage.warning('请先删除已上传的文件再重新上传')
}

defineExpose({ handleUpload })
</script>

<template>
  <div class="resource-upload">
    <el-upload
      :auto-upload="autoUpload"
      :show-file-list="false"
      :accept="acceptComputed"
      :on-change="handleFileChange"
      :on-exceed="handleExceed"
      :limit="1"
      drag
    >
      <div v-if="uploading" class="upload-progress">
        <el-progress :percentage="progress" :stroke-width="8" style="width: 200px" />
        <p class="upload-filename">{{ fileName }}</p>
      </div>
      <div v-else class="upload-trigger">
        <el-icon class="upload-icon"><UploadFilled /></el-icon>
        <div class="upload-text">
          将{{ typeLabel }}文件拖到此处，或<em>点击上传</em>
        </div>
        <div class="upload-tip">
          最大文件大小：{{ (maxFileSize / 1024 / 1024).toFixed(0) }}MB
        </div>
      </div>
    </el-upload>
  </div>
</template>

<style scoped lang="scss">
.resource-upload {
  :deep(.el-upload-dragger) {
    padding: 20px;
  }
}

.upload-progress {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;

  .upload-filename {
    font-size: 12px;
    color: #909399;
    margin: 0;
    max-width: 200px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.upload-trigger {
  display: flex;
  flex-direction: column;
  align-items: center;

  .upload-icon {
    font-size: 40px;
    color: #c0c4cc;
    margin-bottom: 8px;
  }

  .upload-text {
    font-size: 14px;
    color: #606266;

    em {
      color: #409eff;
      font-style: normal;
    }
  }

  .upload-tip {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
  }
}
</style>
