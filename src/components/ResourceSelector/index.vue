<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getResourceList } from '@/api/resource/resource'
import ResourceUpload from '@/components/ResourceUpload/index.vue'
import type { ResourceVO, ResourceQueryRequest } from '@/api/resource/types'
import { ResourceType, ResourceTypeLabels, UploadStatus } from '@/types/enums'

const props = withDefaults(defineProps<{
  modelValue: boolean
  multiple?: boolean
  resourceType?: ResourceType
  excludeIds?: number[]
}>(), {
  multiple: false,
  resourceType: undefined,
  excludeIds: () => []
})

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  confirm: [resources: ResourceVO[]]
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const loading = ref(false)
const resources = ref<ResourceVO[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterType = ref<ResourceType | undefined>(props.resourceType)
const selectedResources = ref<ResourceVO[]>([])
const uploadVisible = ref(false)

const uploadStatusTag: Record<UploadStatus, { type: 'info' | 'success' | 'danger'; label: string }> = {
  [UploadStatus.Pending]: { type: 'info', label: '待确认' },
  [UploadStatus.Success]: { type: 'success', label: '成功' },
  [UploadStatus.Failed]: { type: 'danger', label: '失败' }
}

function formatFileSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  if (bytes < 1024 * 1024 * 1024) return (bytes / 1024 / 1024).toFixed(1) + ' MB'
  return (bytes / 1024 / 1024 / 1024).toFixed(1) + ' GB'
}

const filteredResources = computed(() => {
  return resources.value.filter(r => !props.excludeIds.includes(r.resourceId))
})

async function loadResources() {
  loading.value = true
  try {
    const params: ResourceQueryRequest = {
      current: currentPage.value,
      pageSize: pageSize.value,
      resourceName: keyword.value || undefined,
      resourceType: filterType.value
    }
    const result = await getResourceList(params)
    resources.value = result.records
    total.value = result.total
  } catch (error) {
    const msg = error instanceof Error ? error.message : '加载资源列表失败'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  loadResources()
}

function handlePageChange(page: number) {
  currentPage.value = page
  loadResources()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  loadResources()
}

function handleSelect(resource: ResourceVO) {
  if (props.multiple) {
    const idx = selectedResources.value.findIndex(r => r.resourceId === resource.resourceId)
    if (idx >= 0) {
      selectedResources.value.splice(idx, 1)
    } else {
      selectedResources.value.push(resource)
    }
  } else {
    if (selectedResources.value.length > 0 && selectedResources.value[0].resourceId === resource.resourceId) {
      selectedResources.value = []
    } else {
      selectedResources.value = [resource]
    }
  }
}

function isSelected(resource: ResourceVO): boolean {
  return selectedResources.value.some(r => r.resourceId === resource.resourceId)
}

function handleConfirm() {
  if (selectedResources.value.length === 0) {
    ElMessage.warning('请选择至少一个资源')
    return
  }
  emit('confirm', [...selectedResources.value])
  visible.value = false
}

function handleUploadSuccess(resource: ResourceVO) {
  ElMessage.success('资源上传成功')
  uploadVisible.value = false
  loadResources()
}

watch(visible, (val) => {
  if (val) {
    selectedResources.value = []
    keyword.value = ''
    filterType.value = props.resourceType
    currentPage.value = 1
    loadResources()
  }
})
</script>

<template>
  <el-dialog
    v-model="visible"
    title="选择资源"
    width="800px"
    :close-on-click-modal="false"
    destroy-on-close
  >
    <div class="resource-selector">
      <div class="selector-toolbar">
        <div class="selector-filters">
          <el-input
            v-model="keyword"
            placeholder="搜索资源名称"
            :prefix-icon="Search"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          />
          <el-select
            v-model="filterType"
            placeholder="资源类型"
            clearable
            style="width: 120px"
            @change="handleSearch"
          >
            <el-option label="视频" :value="ResourceType.Video" />
            <el-option label="文档" :value="ResourceType.Document" />
            <el-option label="图片" :value="ResourceType.Image" />
          </el-select>
        </div>
        <el-button type="primary" @click="uploadVisible = true">上传新资源</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="filteredResources"
        height="400"
        border
        @row-click="handleSelect"
        style="cursor: pointer"
      >
        <el-table-column width="55" align="center">
          <template #default="{ row }">
            <el-checkbox :model-value="isSelected(row)" @click.stop />
          </template>
        </el-table-column>
        <el-table-column prop="resourceName" label="文件名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="resourceType" label="类型" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ ResourceTypeLabels[row.resourceType as ResourceType] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileFormat" label="格式" width="80" align="center" />
        <el-table-column prop="fileSize" label="大小" width="100" align="center">
          <template #default="{ row }">{{ formatFileSize(row.fileSize) }}</template>
        </el-table-column>
        <el-table-column prop="uploadStatus" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag
              :type="uploadStatusTag[row.uploadStatus]?.type || 'info'"
              size="small"
            >
              {{ uploadStatusTag[row.uploadStatus]?.label || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="上传时间" width="160" align="center" />
      </el-table>

      <div class="selector-pagination">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>

      <div v-if="selectedResources.length > 0" class="selected-info">
        已选择 {{ selectedResources.length }} 个资源：
        <el-tag
          v-for="r in selectedResources"
          :key="r.resourceId"
          closable
          size="small"
          style="margin: 2px"
          @close="handleSelect(r)"
        >
          {{ r.resourceName }}
        </el-tag>
      </div>
    </div>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :disabled="selectedResources.length === 0" @click="handleConfirm">
        确认选择（{{ selectedResources.length }}）
      </el-button>
    </template>
  </el-dialog>

  <el-dialog v-model="uploadVisible" title="上传新资源" width="500px" append-to-body destroy-on-close>
    <el-form label-width="80px">
      <el-form-item label="资源类型">
        <el-radio-group v-model="uploadResourceType">
          <el-radio :value="ResourceType.Video">视频</el-radio>
          <el-radio :value="ResourceType.Document">文档</el-radio>
          <el-radio :value="ResourceType.Image">图片</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <ResourceUpload
      :resource-type="uploadResourceType"
      @success="handleUploadSuccess"
    />
  </el-dialog>
</template>

<script lang="ts">
export default {
  data() {
    return {
      uploadResourceType: ResourceType.Document as ResourceType
    }
  }
}
</script>

<style scoped lang="scss">
.resource-selector {
  .selector-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;

    .selector-filters {
      display: flex;
      gap: 12px;
    }
  }

  .selector-pagination {
    display: flex;
    justify-content: flex-end;
    margin-top: 12px;
  }

  .selected-info {
    margin-top: 12px;
    padding: 8px 12px;
    background: #f5f7fa;
    border-radius: 4px;
    font-size: 13px;
    color: #606266;
  }
}
</style>
