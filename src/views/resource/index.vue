<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Delete, View, Upload } from '@element-plus/icons-vue'
import { getResourceList, getResourceDetail, deleteResource } from '@/api/resource/resource'
import ResourceUpload from '@/components/ResourceUpload/index.vue'
import VideoPlayer from '@/components/VideoPlayer/index.vue'
import type { ResourceVO, ResourceDetailVO, ResourceQueryRequest } from '@/api/resource/types'

const loading = ref(false)
const resources = ref<ResourceVO[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')
const filterType = ref<1 | 2 | 3 | undefined>(undefined)
const filterStatus = ref<'1' | '2' | '3' | undefined>(undefined)
const dateRange = ref<[string, string] | null>(null)
const viewMode = ref<'table' | 'card'>('table')

const uploadDialogVisible = ref(false)
const uploadResourceType = ref<1 | 2 | 3>(2)
const previewVisible = ref(false)
const previewResource = ref<ResourceDetailVO | null>(null)
const previewLoading = ref(false)

const resourceTypeLabel: Record<number, string> = {
  1: '视频',
  2: '文档',
  3: '图片'
}

const uploadStatusTag: Record<number, { type: 'info' | 'success' | 'danger'; label: string }> = {
  0: { type: 'info', label: '待确认' },
  1: { type: 'success', label: '成功' },
  2: { type: 'danger', label: '失败' }
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
      resourceType: filterType.value,
      uploadStatus: filterStatus.value,
      createdFrom: dateRange.value?.[0] || undefined,
      createdTo: dateRange.value?.[1] || undefined
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

function handleReset() {
  keyword.value = ''
  filterType.value = undefined
  filterStatus.value = undefined
  dateRange.value = null
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

async function handleDelete(resource: ResourceVO) {
  try {
    await ElMessageBox.confirm(
      `确认删除资源「${resource.resourceName}」吗？删除后不可恢复。`,
      '删除确认',
      { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteResource(resource.resourceId)
    ElMessage.success('删除成功')
    await loadResources()
  } catch {
    // cancelled
  }
}

function handleUploadSuccess() {
  ElMessage.success('资源上传成功')
  uploadDialogVisible.value = false
  loadResources()
}

function handleDownload(resource: ResourceVO) {
  window.open(resource.accessUrl, '_blank')
}

onMounted(loadResources)
</script>

<template>
  <div class="resource-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">我的资源</span>
          <div class="header-actions">
            <el-button-group>
              <el-button
                :type="viewMode === 'table' ? 'primary' : ''"
                size="small"
                @click="viewMode = 'table'"
              >
                列表
              </el-button>
              <el-button
                :type="viewMode === 'card' ? 'primary' : ''"
                size="small"
                @click="viewMode = 'card'"
              >
                卡片
              </el-button>
            </el-button-group>
            <el-button type="primary" :icon="Upload" @click="uploadDialogVisible = true">
              上传资源
            </el-button>
          </div>
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
          <el-option label="视频" :value="1" />
          <el-option label="文档" :value="2" />
          <el-option label="图片" :value="3" />
        </el-select>
        <el-select v-model="filterStatus" placeholder="上传状态" clearable style="width: 120px" @change="handleSearch">
          <el-option label="成功" value="1" />
          <el-option label="失败" value="2" />
          <el-option label="待确认" value="3" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          style="width: 260px"
          @change="handleSearch"
        />
        <el-button @click="handleReset">重置</el-button>
      </div>

      <div v-if="viewMode === 'table'">
        <el-table v-loading="loading" :data="resources" border style="width: 100%">
          <el-table-column prop="resourceName" label="文件名" min-width="200" show-overflow-tooltip />
          <el-table-column prop="resourceType" label="类型" width="80" align="center">
            <template #default="{ row }">
              <el-tag size="small">{{ resourceTypeLabel[row.resourceType] }}</el-tag>
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
          <el-table-column label="操作" width="200" align="center" fixed="right">
            <template #default="{ row }">
              <el-button size="small" link type="primary" :icon="View" @click="handlePreview(row)">
                预览
              </el-button>
              <el-button size="small" link @click="handleDownload(row)">下载</el-button>
              <el-button
                size="small"
                link
                type="danger"
                :icon="Delete"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-else class="card-grid">
        <el-empty v-if="resources.length === 0 && !loading" description="暂无资源" />
        <div
          v-for="resource in resources"
          :key="resource.resourceId"
          class="resource-card"
        >
          <div class="card-preview">
            <img
              v-if="resource.resourceType === 3"
              :src="resource.accessUrl"
              :alt="resource.resourceName"
            />
            <div v-else-if="resource.resourceType === 1" class="card-icon video-icon">
              ▶
            </div>
            <div v-else class="card-icon doc-icon">
              📄
            </div>
          </div>
          <div class="card-info">
            <div class="card-name" :title="resource.resourceName">{{ resource.resourceName }}</div>
            <div class="card-meta">
              <el-tag size="small">{{ resourceTypeLabel[resource.resourceType] }}</el-tag>
              <span class="card-size">{{ formatFileSize(resource.fileSize) }}</span>
            </div>
            <div class="card-status">
              <el-tag
                :type="uploadStatusTag[resource.uploadStatus]?.type || 'info'"
                size="small"
              >
                {{ uploadStatusTag[resource.uploadStatus]?.label || '未知' }}
              </el-tag>
            </div>
          </div>
          <div class="card-actions">
            <el-button size="small" link type="primary" @click="handlePreview(resource)">预览</el-button>
            <el-button size="small" link @click="handleDownload(resource)">下载</el-button>
            <el-button size="small" link type="danger" @click="handleDelete(resource)">删除</el-button>
          </div>
        </div>
      </div>

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

    <el-dialog v-model="uploadDialogVisible" title="上传资源" width="500px" destroy-on-close>
      <el-form label-width="80px">
        <el-form-item label="资源类型">
          <el-radio-group v-model="uploadResourceType">
            <el-radio :value="1">视频</el-radio>
            <el-radio :value="2">文档</el-radio>
            <el-radio :value="3">图片</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <ResourceUpload
        :resource-type="uploadResourceType"
        @success="handleUploadSuccess"
      />
    </el-dialog>

    <el-dialog v-model="previewVisible" title="资源预览" width="700px" destroy-on-close>
      <div v-loading="previewLoading" class="preview-content">
        <template v-if="previewResource">
          <div v-if="previewResource.resourceType === 1" class="preview-video">
            <VideoPlayer :src="previewResource.accessUrl" :meta="previewResource.videoMeta || null" />
          </div>
          <div v-else-if="previewResource.resourceType === 3" class="preview-image">
            <img :src="previewResource.accessUrl" :alt="previewResource.resourceName" style="max-width: 100%" />
          </div>
          <div v-else class="preview-document">
            <el-empty description="该文档类型暂不支持在线预览，请下载后查看" />
            <el-button type="primary" @click="handleDownload(previewResource)">下载文件</el-button>
          </div>
          <el-descriptions :column="2" border style="margin-top: 16px">
            <el-descriptions-item label="文件名">{{ previewResource.resourceName }}</el-descriptions-item>
            <el-descriptions-item label="类型">{{ resourceTypeLabel[previewResource.resourceType] }}</el-descriptions-item>
            <el-descriptions-item label="格式">{{ previewResource.fileFormat }}</el-descriptions-item>
            <el-descriptions-item label="大小">{{ formatFileSize(previewResource.fileSize) }}</el-descriptions-item>
            <el-descriptions-item label="上传时间">{{ previewResource.createdAt }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="uploadStatusTag[previewResource.uploadStatus]?.type || 'info'" size="small">
                {{ uploadStatusTag[previewResource.uploadStatus]?.label || '未知' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.resource-management {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .title {
      font-size: 16px;
      font-weight: 600;
    }

    .header-actions {
      display: flex;
      gap: 12px;
      align-items: center;
    }
  }

  .filter-bar {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;
    flex-wrap: wrap;
    align-items: center;
  }

  .pagination-wrapper {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
  }

  .card-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;
  }

  .resource-card {
    border: 1px solid #ebeef5;
    border-radius: 8px;
    overflow: hidden;
    transition: box-shadow 0.2s;

    &:hover {
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    }

    .card-preview {
      height: 120px;
      background: #f5f7fa;
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .card-icon {
        font-size: 40px;
      }

      .video-icon {
        color: #409eff;
      }

      .doc-icon {
        color: #e6a23c;
      }
    }

    .card-info {
      padding: 8px 12px;

      .card-name {
        font-size: 13px;
        color: #303133;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        margin-bottom: 4px;
      }

      .card-meta {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 4px;

        .card-size {
          font-size: 12px;
          color: #909399;
        }
      }
    }

    .card-actions {
      padding: 4px 12px 8px;
      display: flex;
      gap: 4px;
      border-top: 1px solid #f0f2f5;
    }
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
