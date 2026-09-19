<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { Download, File, FileImage, FileText, Search, Trash2, Upload, Video } from 'lucide-vue-next'
import { resourceApi } from '@/api/services'
import type { ResourceItem } from '@/types'
const resources = ref<ResourceItem[]>([])
const loading = ref(true)
const uploading = ref(false)
const error = ref('')
const query = ref('')
const type = ref<number | undefined>()
const input = ref<HTMLInputElement | null>(null)
const shown = computed(() =>
  resources.value.filter(
    (r) =>
      (!query.value || r.resourceName.includes(query.value)) &&
      (!type.value || r.resourceType === type.value),
  ),
)
const icon = (t: number) => (t === 1 ? Video : t === 2 ? FileText : t === 3 ? FileImage : File)
function formatSize(bytes: number) {
  if (!bytes) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  const i = Math.min(Math.floor(Math.log(bytes) / Math.log(1024)), 3)
  return `${(bytes / 1024 ** i).toFixed(i ? 1 : 0)} ${units[i]}`
}
async function load() {
  loading.value = true
  error.value = ''
  try {
    resources.value = (
      await resourceApi.list({
        current: 1,
        pageSize: 100,
        resourceName: query.value || undefined,
        resourceType: type.value,
      })
    ).records
  } catch (e) {
    error.value = e instanceof Error ? e.message : '加载失败'
  } finally {
    loading.value = false
  }
}
function fileType(file: File): 'image' | 'document' | 'video' | null {
  if (file.type.startsWith('image/')) return 'image'
  if (file.type.startsWith('video/')) return 'video'
  const ext = file.name.split('.').pop()?.toLowerCase()
  if (['pdf', 'doc', 'docx', 'ppt', 'pptx', 'xls', 'xlsx', 'txt', 'md'].includes(ext || ''))
    return 'document'
  return null
}
async function choose(event: Event) {
  const file = (event.target as HTMLInputElement).files?.[0]
  if (!file) return
  const kind = fileType(file)
  if (!kind) {
    error.value = '后端仅支持图片、视频和常见文档格式'
    return
  }
  uploading.value = true
  error.value = ''
  try {
    const ticket = await resourceApi.presign(kind, file)
    await resourceApi.upload(ticket.uploadUrl, file)
    await resourceApi.confirm(ticket.resourceId, kind === 'video')
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '上传失败'
  } finally {
    uploading.value = false
    if (input.value) input.value.value = ''
  }
}
async function remove(item: ResourceItem) {
  if (!confirm(`确定删除“${item.resourceName}”吗？`)) return
  try {
    await resourceApi.remove(item.resourceId)
    await load()
  } catch (e) {
    error.value = e instanceof Error ? e.message : '删除失败'
  }
}
onMounted(load)
</script>
<template>
  <div>
    <div class="flex flex-col gap-5 sm:flex-row sm:items-end sm:justify-between">
      <div>
        <h2 class="text-3xl font-black">我的资源</h2>
        <p class="mt-2 text-sm text-[#7c857f]">通过预签名 URL 直传对象存储。</p>
      </div>
      <input ref="input" type="file" class="hidden" @change="choose" /><button
        class="btn-primary"
        :disabled="uploading"
        @click="input?.click()"
      >
        <Upload :size="17" />{{ uploading ? '上传中…' : '上传资源' }}
      </button>
    </div>
    <div class="mt-7 flex flex-col gap-3 sm:flex-row">
      <div class="relative flex-1">
        <Search class="absolute left-4 top-1/2 -translate-y-1/2 text-[#8b938d]" :size="17" /><input
          v-model="query"
          class="field pl-11"
          placeholder="搜索文件名…"
          @keyup.enter="load"
        />
      </div>
      <select v-model="type" class="field sm:w-44" @change="load">
        <option :value="undefined">全部类型</option>
        <option :value="1">视频</option>
        <option :value="2">文档</option>
        <option :value="3">图片</option>
      </select>
    </div>
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
            <tr v-for="item in shown" v-else :key="item.resourceId" class="border-t text-sm">
              <td class="px-6 py-4">
                <div class="flex items-center gap-3">
                  <span class="grid size-10 place-items-center rounded-xl bg-[#eef3e7] text-leaf"
                    ><component :is="icon(item.resourceType)" :size="19" /></span
                  ><b>{{ item.resourceName }}</b>
                </div>
              </td>
              <td>{{ ['', '视频', '文档', '图片'][item.resourceType] }}</td>
              <td>{{ formatSize(item.fileSize) }}</td>
              <td>
                <span class="pill bg-[#eef4e5] py-1 text-leaf">{{
                  ['', '待确认', '成功', '失败'][item.uploadStatus]
                }}</span>
              </td>
              <td>{{ new Date(item.createdAt).toLocaleString() }}</td>
              <td class="pr-6">
                <div class="flex justify-end gap-1">
                  <a
                    v-if="item.accessUrl"
                    :href="item.accessUrl"
                    target="_blank"
                    class="icon-btn size-8"
                    ><Download :size="15" /></a
                  ><button class="icon-btn size-8 text-red-500" @click="remove(item)">
                    <Trash2 :size="15" />
                  </button>
                </div>
              </td>
            </tr>
            <tr v-if="!loading && !shown.length">
              <td colspan="6" class="p-12 text-center text-sm text-[#8b938d]">暂无资源</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
