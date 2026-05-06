<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCourseStore } from '@/stores/course'
import { getSectionDetail } from '@/api/course/section'
import type { SectionDetailVO, ChapterVO, SectionVO } from '@/api/course/types'
import { YesNo, ResourceType } from '@/types/enums'

const route = useRoute()
const router = useRouter()
const courseStore = useCourseStore()
const courseId = route.params.id as string

const loading = ref(false)
const currentSectionDetail = ref<SectionDetailVO | null>(null)
const sectionLoading = ref(false)

const course = computed(() => courseStore.currentCourse)
const chapters = computed(() => course.value?.chapters || [])

function getSectionOrder(chapterIndex: number, sectionIndex: number): number {
  let order = 0
  for (let i = 0; i < chapterIndex; i++) {
    order += chapters.value[i]?.sections?.length || 0
  }
  return order + sectionIndex + 1
}

async function loadCourse() {
  loading.value = true
  try {
    await courseStore.fetchCourseDetail(courseId)
  } finally {
    loading.value = false
  }
}

function openWindow(url: string) {
  window.open(url, '_blank')
}

async function handleSectionClick(section: SectionVO) {
  sectionLoading.value = true
  try {
    currentSectionDetail.value = await getSectionDetail(section.sectionId)
  } catch {
    currentSectionDetail.value = null
  } finally {
    sectionLoading.value = false
  }
}

function goBack() {
  router.push('/course/learning')
}

onMounted(loadCourse)
</script>

<template>
  <div v-loading="loading" class="chapter-study">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/learning' }">我学的课</el-breadcrumb-item>
            <el-breadcrumb-item>章节学习</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <div class="study-content">
        <div class="chapter-sidebar">
          <h3 class="sidebar-title">{{ course?.courseName || '课程目录' }}</h3>
          <el-scrollbar height="70vh">
            <el-empty v-if="chapters.length === 0" description="暂无章节" :image-size="60" />

            <div v-for="(chapter, chapterIndex) in chapters" :key="chapter.chapterId" class="chapter-group">
              <div class="chapter-label">{{ chapter.title }}</div>
              <div
                v-for="(section, sectionIndex) in chapter.sections"
                :key="section.sectionId"
                class="section-link"
                :class="{ active: currentSectionDetail?.sectionId === section.sectionId }"
                @click="handleSectionClick(section)"
              >
                <i class="order-badge">{{ getSectionOrder(chapterIndex, sectionIndex) }}</i>
                <el-tag v-if="section.isFree === YesNo.Yes" size="small" type="warning" style="margin-right: 6px">免费</el-tag>
                <span>{{ section.title }}</span>
              </div>
              <el-empty
                v-if="!chapter.sections?.length"
                description="暂无小节"
                :image-size="40"
              />
            </div>
          </el-scrollbar>
        </div>

        <div class="section-detail" v-loading="sectionLoading">
          <template v-if="currentSectionDetail">
            <h2 class="section-title">{{ currentSectionDetail.title }}</h2>

            <div v-if="currentSectionDetail.resourceDetails?.length" class="resources-list">
              <h4>资源列表</h4>
              <div
                v-for="res in currentSectionDetail.resourceDetails"
                :key="res.resourceId"
                class="resource-item"
              >
                <div class="resource-info">
                  <el-tag
                    :type="res.resourceType === ResourceType.Video ? 'danger' : res.resourceType === ResourceType.Document ? undefined : 'success'"
                    size="small"
                  >
                    {{ res.resourceType === ResourceType.Video ? '视频' : res.resourceType === ResourceType.Document ? '文档' : '图片' }}
                  </el-tag>
                  <span class="resource-name">{{ res.resourceName }}</span>
                  <span class="resource-format">({{ res.fileFormat }})</span>
                </div>
                <div class="resource-actions">
                  <el-button
                    v-if="res.resourceType === ResourceType.Video"
                    size="small"
                    type="primary"
                    @click="router.push(`/course/learning/${courseId}/section/${currentSectionDetail!.sectionId}/video/${res.resourceId}`)"
                  >
                    播放
                  </el-button>
                  <el-button
                    v-else
                    size="small"
                    @click="openWindow(res.accessUrl)"
                  >
                    查看
                  </el-button>
                </div>
              </div>
            </div>

            <el-empty v-else description="该小节暂无资源" />
          </template>

          <el-empty v-else description="请从左侧目录选择小节学习" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.chapter-study {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.study-content {
  display: flex;
  gap: 20px;
}

.chapter-sidebar {
  width: 280px;
  flex-shrink: 0;
  border-right: 1px solid #ebeef5;
  padding-right: 16px;

  .sidebar-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 12px;
    padding-bottom: 8px;
    border-bottom: 1px solid #ebeef5;
  }
}

.chapter-group {
  margin-bottom: 16px;

  .chapter-label {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    padding: 6px 0;
  }
}

.section-link {
  display: flex;
  align-items: center;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
  font-size: 13px;
  color: #606266;

  .order-badge {
    background-color: #ffa500;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 24px;
    height: 24px;
    min-width: 24px;
    margin-right: 8px;
    border-radius: 50%;
    font-size: 11px;
    color: #ffffff;
    font-style: normal;
    font-weight: 500;
  }

  &:hover {
    background: #f5f7fa;
  }

  &.active {
    background: #ecf5ff;
    color: #409eff;
  }
}

.section-detail {
  flex: 1;
  min-width: 0;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px;
}

.resources-list {
  h4 {
    font-size: 15px;
    font-weight: 500;
    color: #303133;
    margin: 0 0 12px;
  }
}

.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 12px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  margin-bottom: 8px;

  &:hover {
    background: #f5f7fa;
  }

  .resource-info {
    display: flex;
    align-items: center;
    gap: 8px;

    .resource-name {
      font-size: 14px;
      color: #303133;
    }

    .resource-format {
      font-size: 12px;
      color: #909399;
    }
  }
}
</style>
