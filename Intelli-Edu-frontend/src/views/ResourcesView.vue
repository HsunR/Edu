<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { Download, File, FileImage, FileText, Search, Trash2, Upload, Video } from 'lucide-vue-next'
import { resourceApi } from '@/api/services'
import { useUiStore } from '@/stores/ui'
import type { ResourceItem } from '@/types'
import AppPagination from '@/components/AppPagination.vue'

const ui = useUiStore()
const resources = ref<ResourceItem[]>([])
const loading = ref(true)
const uploading = ref(false)
const uploadProgress = ref(0)
const error = ref('')
const query = ref('')
const type = ref<number | undefined>()
const input = ref<HTMLInputElement | null>(null)
const page = ref(1)
const pageSize = 15
const total = ref(0)
const icon = (value: number) =>
  value === 1 ? Video : value === 2 ? FileText : value === 3 ? FileImage : File

function formatSize(bytes: number) {
  if (!bytes) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  const index = Math.min(Math.floor(Math.log(bytes) / Math.log(1024)), 3)
  return `${(bytes / 1024 ** index).toFixed(index ? 1 : 0)} ${units[index]}`
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    const result = await resourceApi.list({
      current: page.value,
      pageSize,
      resourceName: query.value.trim() || undefined,
      resourceType: type.value,
    })
    resources.value = result.records
    total.value = result.total
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}

function fileType(file: File): 'image' | 'document' | 'video' | null {
  if (file.type.startsWith('image/')) return 'image'
  if (file.type.startsWith('video/')) return 'video'
  const extension = file.name.split('.').pop()?.toLowerCase()
  if (['pdf', 'doc', 'docx', 'ppt', 'pptx', 'xls', 'xlsx', 'txt', 'md'].includes(extension || ''))
    return 'document'
  return null
}

async function choose(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return
  const kind = fileType(file)
  if (!kind) {
    error.value = '仅支持图片、视频和常见文档格式'
    return
  }
  uploading.value = true
  uploadProgress.value = 0
  error.value = ''
  try {
    const ticket = await resourceApi.presign(kind, file)
    await resourceApi.upload(ticket.uploadUrl, file, (percent) => (uploadProgress.value = percent))
    await resourceApi.confirm(ticket.resourceId, kind === 'video')
    page.value = 1
    await load()
    ui.notify('资源上传成功', 'success')
  } catch (e) {
    error.value = e instanceof Error ? e.message : '上传失败'
  } finally {
    uploading.value = false
    if (input.value) input.value.value = ''
  }
}

async function remove(item: ResourceItem) {
  const confirmed = await ui.confirm({
    title: '删除资源',
    message: `确定删除“${item.resourceName}”吗？已关联到课程的资源可能会受影响。`,
    confirmLabel: '删除',
    danger: true,
  })
  if (!confirmed) return
  try {
    await resourceApi.remove(item.resourceId)
    if (resources.value.length === 1 && page.value > 1) page.value--
    await load()
    ui.notify('资源已删除', 'success')
  } catch (e) {
    error.value = e instanceof Error ? e.message : '删除失败'
  }
}

function search() {
  page.value = 1
  load()
}
function changeType() {
  page.value = 1
  load()
}
function changePage(nextPage: number) {
  page.value = nextPage
  load()
}

onMounted(load)
</script>

<template>
  <div>
    <div class="flex flex-col gap-5 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <h2 class="text-3xl font-black">我的资源</h2>
        <p class="mt-2 text-sm text-[#7c857f]">上传并管理课程需要使用的图片、视频和文档。</p>
      </div>
      <input
        ref="input"
        type="file"
        class="hidden"
        accept="image/*,video/*,.pdf,.doc,.docx,.ppt,.pptx,.xls,.xlsx,.txt,.md"
        @change="choose"
      />
      <button class="btn-primary" :disabled="uploading" @click="input?.click()">
        <Upload :size="17" />{{ uploading ? `上传中 ${uploadProgress}%` : '上传资源' }}
      </button>
    </div>
    <form class="mt-7 flex flex-col gap-3 sm:flex-row" @submit.prevent="search">
      <div class="relative flex-1">
        <Search class="absolute left-4 top-1/2 -translate-y-1/2 text-[#8b938d]" :size="17" />
        <input v-model="query" class="field pl-11" placeholder="搜索文件名…" />
      </div>
      <select v-model="type" class="field sm:w-44" aria-label="资源类型" @change="changeType">
        <option :value="undefined">全部类型</option>
        <option :value="1">视频</option>
        <option :value="2">文档</option>
        <option :value="3">图片</option>
      </select>
      <button class="btn-secondary sm:px-7">搜索</button>
    </form>
    <p v-if="error" class="mt-5 rounded-2xl bg-red-50 p-4 text-sm text-red-600">{{ error }}</p>
    <div class="card mt-5 overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full min-w-[720px] text-left">
          <thead class="bg-[#f8f9f5] text-[10px] font-bold tracking-wider text-[#8c948e]">
            <tr>
              <th class="px-6 py-4">文件名称</th>
              <th>类型</th>
              <th>大小</th>
              <th>状态</th>
              <th>上传时间</th>
              <th class="pr-6 text-right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="loading">
              <td colspan="6" class="p-12 text-center text-sm text-[#8b938d]">正在加载…</td>
            </tr>
            <tr v-for="item in resources" v-else :key="item.resourceId" class="border-t text-sm">
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <span class="grid size-10 place-items-center rounded-xl bg-[#eef3e7] text-leaf"
                    ><component :is="icon(item.resourceType)" :size="19" /></span
                  ><b class="max-w-xs truncate">{{ item.resourceName }}</b>
                </div>
              </td>
              <td>{{ ['', '视频', '文档', '图片'][item.resourceType] || '其他' }}</td>
              <td>{{ formatSize(item.fileSize) }}</td>
              <td>
                <span
                  :class="[
                    'pill py-1',
                    item.uploadStatus === 2
                      ? 'bg-[#eef4e5] text-leaf'
                      : item.uploadStatus === 3
                        ? 'bg-red-50 text-red-600'
                        : 'bg-amber-50 text-amber-700',
                  ]"
                  >{{ ['', '待确认', '成功', '失败'][item.uploadStatus] || '未知' }}</span
                >
              </td>
              <td>{{ new Date(item.createdAt).toLocaleString() }}</td>
              <td class="pr-6">
                <div class="flex justify-end gap-1">
                  <a
                    v-if="item.accessUrl"
                    :href="item.accessUrl"
                    target="_blank"
                    rel="noopener noreferrer"
                    class="icon-btn size-8"
                    aria-label="打开资源"
                    ><Download :size="15" /></a
                  ><button
                    class="icon-btn size-8 text-red-500"
                    aria-label="删除资源"
                    @click="remove(item)"
                  >
                    <Trash2 :size="15" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!loading && !resources.length">
              <td colspan="6" class="p-12 text-center text-sm text-[#8b938d]">暂无资源</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
    <AppPagination
      :page="page"
      :page-size="pageSize"
      :total="total"
      :disabled="loading"
      @change="changePage"
    />
  </div>
</template>
