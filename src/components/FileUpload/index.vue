<template>
  <div class="upload-file">
    <el-upload
      multiple
      :action="uploadFileUrl"
      :before-upload="handleBeforeUpload"
      :file-list="fileList"
      :data="data"
      :limit="limit"
      :on-error="handleUploadError"
      :on-exceed="handleExceed"
      :on-success="handleUploadSuccess"
      :show-file-list="false"
      :headers="headers"
      class="upload-file-uploader"
      ref="fileUploadRef"
      v-if="!disabled"
    >
      <el-button type="primary">选取文件</el-button>
    </el-upload>
    <div class="el-upload__tip" v-if="showTip && !disabled">
      请上传
      <template v-if="fileSize"> 大小不超过 <b style="color: #f56c6c">{{ fileSize }}MB</b> </template>
      <template v-if="fileType"> 格式为 <b style="color: #f56c6c">{{ fileType.join("/") }}</b> </template>
      的文件
    </div>
    <transition-group ref="uploadFileListRef" class="upload-file-list el-upload-list el-upload-list--text" name="el-fade-in-linear" tag="ul">
      <li :key="file.uid" class="el-upload-list__item ele-upload-list__item-content" v-for="(file, index) in fileList">
        <el-link :href="`${baseUrl}${file.url}`" :underline="false" target="_blank">
          <span class="el-icon-document"> {{ getFileName(file.name) }} </span>
        </el-link>
        <div class="ele-upload-list__item-content-action">
          <el-link :underline="false" @click="handleDelete(index)" type="danger" v-if="!disabled">&nbsp;删除</el-link>
        </div>
      </li>
    </transition-group>
  </div>
</template>

<script setup lang="ts">
import { getToken } from "@/utils/auth"
import Sortable from 'sortablejs'
import type { UploadRawFile } from 'element-plus'

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
  fileType: () => ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'pptx', 'txt', 'pdf'],
  isShowTip: true,
  disabled: false,
  drag: true,
})

const emit = defineEmits<{
  'update:modelValue': [value: string]
}>()

const fileUploadRef = ref()
const uploadFileListRef = ref()
const number = ref(0)
const uploadList = ref<UploadItem[]>([])
const baseUrl = import.meta.env.VITE_APP_BASE_API
const uploadFileUrl = ref(import.meta.env.VITE_APP_BASE_API + props.action)
const headers = ref({ Authorization: 'Bearer ' + getToken() })
const fileList = ref<UploadItem[]>([])
const showTip = computed(() => props.isShowTip && (props.fileType.length > 0 || props.fileSize))

watch(() => props.modelValue, val => {
  if (val) {
    let temp = 1
    const list = Array.isArray(val) ? val : String(val).split(',')
    fileList.value = list.map(item => {
      if (typeof item === 'string') {
        return { name: item, url: item, uid: new Date().getTime() + temp++ }
      }
      return { ...item, uid: item.uid || new Date().getTime() + temp++ }
    })
  } else {
    fileList.value = []
  }
}, { deep: true, immediate: true })

function handleBeforeUpload(file: UploadRawFile) {
  if (props.fileType.length) {
    const fileName = file.name.split('.')
    const fileExt = fileName[fileName.length - 1]
    const isTypeOk = props.fileType.indexOf(fileExt) >= 0
    if (!isTypeOk) {
      ElMessage.error(`文件格式不正确，请上传${props.fileType.join('/')}格式文件!`)
      return false
    }
  }
  if (file.name.includes(',')) {
    ElMessage.error('文件名不正确，不能包含英文逗号!')
    return false
  }
  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize
    if (!isLt) {
      ElMessage.error(`上传文件大小不能超过 ${props.fileSize} MB!`)
      return false
    }
  }
  ElLoading.service({ text: '正在上传文件，请稍候' })
  number.value++
  return true
}

function handleExceed() {
  ElMessage.error(`上传文件数量不能超过 ${props.limit} 个!`)
}

function handleUploadError() {
  ElMessage.error('上传文件失败')
  ElLoading.service().close()
}

function handleUploadSuccess(res: { code: number; fileName: string; msg?: string }, file: unknown) {
  if (res.code === 200) {
    uploadList.value.push({ name: res.fileName, url: res.fileName })
    uploadedSuccessfully()
  } else {
    number.value--
    ElLoading.service().close()
    ElMessage.error(res.msg || '上传失败')
    fileUploadRef.value?.handleRemove(file)
    uploadedSuccessfully()
  }
}

function handleDelete(index: number) {
  fileList.value.splice(index, 1)
  emit('update:modelValue', listToString(fileList.value))
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

function getFileName(name: string) {
  if (name.lastIndexOf('/') > -1) {
    return name.slice(name.lastIndexOf('/') + 1)
  }
  return name
}

function listToString(list: UploadItem[], separator = ','): string {
  let strs = ''
  for (const item of list) {
    if (item.url) {
      strs += item.url + separator
    }
  }
  return strs !== '' ? strs.slice(0, -1) : ''
}

onMounted(() => {
  if (props.drag && !props.disabled) {
    nextTick(() => {
      const element = uploadFileListRef.value?.$el || uploadFileListRef.value
      if (element) {
        Sortable.create(element, {
          ghostClass: 'file-upload-darg',
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
.file-upload-darg {
  opacity: 0.5;
  background: #c8ebfb;
}
.upload-file-uploader {
  margin-bottom: 5px;
}
.upload-file-list .el-upload-list__item {
  border: 1px solid #e4e7ed;
  line-height: 2;
  margin-bottom: 10px;
  position: relative;
  transition: none !important;
}
.upload-file-list .ele-upload-list__item-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: inherit;
}
.ele-upload-list__item-content-action .el-link {
  margin-right: 10px;
}
</style>
