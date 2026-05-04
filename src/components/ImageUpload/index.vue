<template>
  <div class="component-upload-image">
    <el-upload
      multiple
      :disabled="disabled"
      :action="uploadImgUrl"
      list-type="picture-card"
      :on-success="handleUploadSuccess"
      :before-upload="handleBeforeUpload"
      :data="data"
      :limit="limit"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      ref="imageUploadRef"
      :before-remove="handleDelete"
      :show-file-list="true"
      :headers="headers"
      :file-list="fileList"
      :on-preview="handlePictureCardPreview"
      :class="{ hide: fileList.length >= limit }"
    >
      <el-icon class="avatar-uploader-icon"><plus /></el-icon>
    </el-upload>
    <div class="el-upload__tip" v-if="showTip && !disabled">
      请上传
      <template v-if="fileSize">
        大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b>
      </template>
      <template v-if="fileType">
        格式为 <b style="color: #f56c6c">{{ fileType.join("/") }}</b>
      </template>
      的文件
    </div>

    <el-dialog
      v-model="dialogVisible"
      title="预览"
      width="800px"
      append-to-body
    >
      <img
        :src="dialogImageUrl"
        style="display: block; max-width: 100%; margin: 0 auto"
      />
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { getToken } from "@/utils/auth"
import { isExternal } from "@/utils/validate"
import Sortable from 'sortablejs'
import type { UploadFile, UploadRawFile } from 'element-plus'

interface UploadItem {
  name: string
  url: string
  uid?: number
}

const props = withDefaults(defineProps<{
  modelValue?: string | string[] | UploadItem[]
  action?: string
  data?: Record<string, unknown>
  limit?: number
  fileSize?: number
  fileType?: string[]
  isShowTip?: boolean
  disabled?: boolean
  drag?: boolean
}>(), {
  action: '/common/upload',
  limit: 5,
  fileSize: 5,
  fileType: () => ['png', 'jpg', 'jpeg'],
  isShowTip: true,
  disabled: false,
  drag: true,
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const imageUploadRef = ref()
const number = ref(0)
const uploadList = ref<UploadItem[]>([])
const dialogImageUrl = ref('')
const dialogVisible = ref(false)
const baseUrl = import.meta.env.VITE_APP_BASE_API
const uploadImgUrl = ref(import.meta.env.VITE_APP_BASE_API + props.action)
const headers = ref({ Authorization: 'Bearer ' + getToken() })
const fileList = ref<UploadItem[]>([])
const showTip = computed(() => props.isShowTip && (props.fileType.length > 0 || props.fileSize))

watch(() => props.modelValue, val => {
  if (val) {
    const list = Array.isArray(val) ? val : String(val).split(',')
    fileList.value = list.map(item => {
      if (typeof item === 'string') {
        if (item.indexOf(baseUrl) === -1 && !isExternal(item)) {
          return { name: baseUrl + item, url: baseUrl + item }
        }
        return { name: item, url: item }
      }
      return item
    })
  } else {
    fileList.value = []
  }
}, { deep: true, immediate: true })

function handleBeforeUpload(file: UploadRawFile) {
  let isImg = false
  if (props.fileType.length) {
    let fileExtension = ''
    if (file.name.lastIndexOf('.') > -1) {
      fileExtension = file.name.slice(file.name.lastIndexOf('.') + 1)
    }
    isImg = props.fileType.some(type => {
      if (file.type.indexOf(type) > -1) return true
      if (fileExtension && fileExtension.indexOf(type) > -1) return true
      return false
    })
  } else {
    isImg = file.type.indexOf('image') > -1
  }
  if (!isImg) {
    ElMessage.error(`文件格式不正确，请上传${props.fileType.join('/')}图片格式文件!`)
    return false
  }
  if (file.name.includes(',')) {
    ElMessage.error('文件名不正确，不能包含英文逗号!')
    return false
  }
  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize
    if (!isLt) {
      ElMessage.error(`上传头像图片大小不能超过 ${props.fileSize} MB!`)
      return false
    }
  }
  ElLoading.service({ text: '正在上传图片，请稍候' })
  number.value++
  return true
}

function handleExceed() {
  ElMessage.error(`上传文件数量不能超过 ${props.limit} 个!`)
}

function handleUploadSuccess(res: { code: number; fileName: string; msg?: string }, file: UploadFile) {
  if (res.code === 200) {
    uploadList.value.push({ name: res.fileName, url: res.fileName })
    uploadedSuccessfully()
  } else {
    number.value--
    ElLoading.service().close()
    ElMessage.error(res.msg || '上传失败')
    imageUploadRef.value?.handleRemove(file)
    uploadedSuccessfully()
  }
}

function handleDelete(file: UploadFile) {
  const findex = fileList.value.map(f => f.name).indexOf(file.name)
  if (findex > -1 && uploadList.value.length === number.value) {
    fileList.value.splice(findex, 1)
    emit('update:modelValue', listToString(fileList.value))
    return false
  }
}

function uploadedSuccessfully() {
  if (number.value > 0 && uploadList.value.length === number.value) {
    fileList.value = fileList.value.filter(f => f.url !== undefined).concat(uploadList.value)
    uploadList.value = []
    number.value = 0
    emit('update:modelValue', listToString(fileList.value))
    ElLoading.service().close()
  }
}

function handleUploadError() {
  ElMessage.error('上传图片失败')
  ElLoading.service().close()
}

function handlePictureCardPreview(file: UploadFile) {
  dialogImageUrl.value = file.url!
  dialogVisible.value = true
}

function listToString(list: UploadItem[], separator = ','): string {
  let strs = ''
  for (const item of list) {
    if (item.url !== undefined && item.url.indexOf('blob:') !== 0) {
      strs += item.url.replace(baseUrl, '') + separator
    }
  }
  return strs !== '' ? strs.slice(0, -1) : ''
}

onMounted(() => {
  if (props.drag && !props.disabled) {
    nextTick(() => {
      const element = imageUploadRef.value?.$el?.querySelector('.el-upload-list')
      if (element) {
        Sortable.create(element, {
          onEnd: (evt) => {
            const movedItem = fileList.value.splice(evt.oldIndex!, 1)[0]
            fileList.value.splice(evt.newIndex!, 0, movedItem)
            emit('update:modelValue', listToString(fileList.value))
          }
        })
      }
    })
  }
})
</script>

<style scoped lang="scss">
:deep(.hide .el-upload--picture-card) {
    display: none;
}

:deep(.el-upload.el-upload--picture-card.is-disabled) {
  display: none !important;
}
</style>
