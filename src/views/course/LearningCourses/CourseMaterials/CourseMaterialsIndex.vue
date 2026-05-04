<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, View } from '@element-plus/icons-vue'
import { getResourceList, getResourceDetail } from '@/api/resource/resource'
import VideoPlayer from '@/components/VideoPlayer/index.vue'
import type { ResourceVO, ResourceDetailVO, ResourceQueryRequest } from '@/api/resource/types'
import { ResourceType, ResourceTypeLabels, UploadStatus, UploadStatusLabels } from '@/types/enums'

const route = useRoute()
const courseId = route.params.id

const loading = ref(false)
const resources = ref<ResourceVO[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterType = ref<ResourceType | undefined>(undefined)

const previewVisible = ref(false)
const previewResource = ref<ResourceDetailVO | null>(null)
const previewLoading = ref(false)

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

async function handlePreview(resource: ResourceVO) {
  previewLoading.value = true
  previewVisible.value = true
  try {
    const detail = await getResourceDetail(resource.resourceId)
    previewResource.value = detail
  } catch (error) {
    const msg = error instanceof Error ? error.message : '获取资源详情失败'
    ElMessage.error(msg)
    previewVisible.value = false
  } finally {
    previewLoading.value = false
  }
}

function handleDownload(resource: ResourceVO) {
  window.open(resource.accessUrl, '_blank')
}

onMounted(loadResources)
</script>

<template>
  <div class="course-materials-learning">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/LearningCourses' }">我学的课</el-breadcrumb-item>
            <el-breadcrumb-item>课程资料</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <div class="filter-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索资源名称"
          :prefix-icon="Search"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        />
        <el-select v-model="filterType" placeholder="资源类型" clearable style="width: 120px" @change="handleSearch">
          <el-option label="视频" :value="ResourceType.Video" />
          <el-option label="文档" :value="ResourceType.Document" />
          <el-option label="图片" :value="ResourceType.Image" />
        </el-select>
      </div>

      <el-table v-loading="loading" :data="resources" border style="width: 100%">
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
        <el-table-column label="操作" width="150" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" :icon="View" @click="handlePreview(row)">
              预览
            </el-button>
            <el-button size="small" link @click="handleDownload(row)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
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
    </el-card>

    <el-dialog v-model="previewVisible" title="资源预览" width="700px" destroy-on-close>
      <div v-loading="previewLoading" class="preview-content">
        <template v-if="previewResource">
          <div v-if="previewResource.resourceType === ResourceType.Video" class="preview-video">
            <VideoPlayer :src="previewResource.accessUrl" :meta="previewResource.videoMeta || null" />
          </div>
          <div v-else-if="previewResource.resourceType === ResourceType.Image" class="preview-image">
            <img :src="previewResource.accessUrl" :alt="previewResource.resourceName" style="max-width: 100%" />
          </div>
          <div v-else class="preview-document">
            <el-empty description="该文档类型暂不支持在线预览，请下载后查看" />
            <el-button type="primary" @click="handleDownload(previewResource)">下载文件</el-button>
          </div>
          <el-descriptions :column="2" border style="margin-top: 16px">
            <el-descriptions-item label="文件名">{{ previewResource.resourceName }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{ ResourceTypeLabels[previewResource.resourceType as ResourceType] }}</el-descriptions-item>
            <el-descriptions-item label="格式">{{ previewResource.fileFormat }}</el-descriptions-item>
            <el-descriptions-item label="大小">{{ formatFileSize(previewResource.fileSize) }}</el-descriptions-item>
            <el-descriptions-item label="上传时间">{{ previewResource.createdAt }}</el-descriptions-item>
          </el-descriptions>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.course-materials-learning {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .filter-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;
    align-items: center;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }

  .preview-content {
    min-height: 200px;

    .preview-video {
      max-width: 100%;
    }

    .preview-image {
      text-align: center;
    }

    .preview-document {
      text-align: center;
      padding: 20px 0;
    }
  }
}
</style>
