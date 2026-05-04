<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { useCourseStore } from '@/stores/course'
import type { CourseQueryRequest } from '@/api/course/types'
import type { CategoryVO } from '@/api/course/types'

const router = useRouter()
const courseStore = useCourseStore()

const loading = ref(false)
const searchKeyword = ref('')
const selectedCategoryId = ref<number | null>(null)
const currentPage = ref(1)
const pageSize = ref(12)

const queryParams = reactive<CourseQueryRequest>({
  current: 1,
  pageSize: 12,
  courseName: '',
  categoryId: undefined,
  status: 1
})

async function loadCourses() {
  loading.value = true
  try {
    queryParams.current = currentPage.value
    queryParams.pageSize = pageSize.value
    queryParams.courseName = searchKeyword.value || undefined
    queryParams.categoryId = selectedCategoryId.value ?? undefined
    await courseStore.fetchCourseList(queryParams)
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  currentPage.value = 1
  loadCourses()
}

function handleCategorySelect(data: CategoryVO) {
  selectedCategoryId.value = data.categoryId
  currentPage.value = 1
  loadCourses()
}

function handleCategoryClear() {
  selectedCategoryId.value = null
  currentPage.value = 1
  loadCourses()
}

function handlePageChange(page: number) {
  currentPage.value = page
  loadCourses()
}

function handleSizeChange(size: number) {
  pageSize.value = size
  currentPage.value = 1
  loadCourses()
}

function goToCourseDetail(courseId: number) {
  router.push(`/course/detail/${courseId}`)
}

const treeProps = {
  children: 'children',
  label: 'name',
  value: 'categoryId'
}

onMounted(async () => {
  await courseStore.fetchCategoryTree()
  await loadCourses()
})
</script>

<template>
  <div class="course-browse">
    <div class="browse-sidebar">
      <div class="sidebar-header">
        <span class="sidebar-title">课程分类</span>
        <el-button v-if="selectedCategoryId" link type="primary" @click="handleCategoryClear">
          清除筛选
        </el-button>
      </div>
      <el-tree
        :data="courseStore.categoryTree"
        :props="treeProps"
        node-key="categoryId"
        highlight-current
        default-expand-all
        :expand-on-click-node="false"
        @node-click="handleCategorySelect"
      >
        <template #default="{ data }">
          <span class="category-node">
            {{ data.name }}
          </span>
        </template>
      </el-tree>
    </div>

    <div class="browse-main">
      <div class="browse-header">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索课程名称"
          clearable
          style="max-width: 400px"
          @keyup.enter="handleSearch"
          @clear="handleSearch"
        >
          <template #append>
            <el-button :icon="Search" @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <div v-loading="loading" class="course-grid">
        <el-empty v-if="!loading && courseStore.courseList.length === 0" description="暂无课程" />

        <el-card
          v-for="course in courseStore.courseList"
          :key="course.courseId"
          class="course-card"
          shadow="hover"
          @click="goToCourseDetail(course.courseId)"
        >
          <div class="card-cover">
            <el-image
              :src="course.coverUrl || '/src/assets/images/test.png'"
              fit="cover"
              class="cover-image"
            >
              <template #error>
                <div class="cover-fallback">
                  <el-icon :size="40"><Reading /></el-icon>
                </div>
              </template>
            </el-image>
          </div>
          <div class="card-body">
            <h3 class="card-title" :title="course.courseName">{{ course.courseName }}</h3>
            <p class="card-desc" :title="course.description">{{ course.description || '暂无简介' }}</p>
            <div class="card-meta">
              <div class="meta-teacher">
                <el-avatar :size="24" :src="course.teacherAvatar">
                  {{ course.teacherName?.charAt(0) }}
                </el-avatar>
                <span class="teacher-name">{{ course.teacherName }}</span>
              </div>
              <el-tag size="small" type="info">{{ course.categoryName }}</el-tag>
            </div>
          </div>
        </el-card>
      </div>

      <div v-if="courseStore.courseTotal > 0" class="browse-pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="courseStore.courseTotal"
          :page-sizes="[8, 12, 16, 24]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Reading } from '@element-plus/icons-vue'
export default {
  components: { Reading }
}
</script>

<style scoped lang="scss">
.course-browse {
  display: flex;
  height: calc(100vh - 120px);
  gap: 16px;
}

.browse-sidebar {
  width: 240px;
  flex-shrink: 0;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  overflow-y: auto;

  .sidebar-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
    padding-bottom: 12px;
    border-bottom: 1px solid #ebeef5;
  }

  .sidebar-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }
}

.category-node {
  font-size: 14px;
  color: #606266;
}

.browse-main {
  flex: 1;
  min-width: 0;
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

.browse-header {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.course-grid {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
  align-content: start;
  overflow-y: auto;
}

.course-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  border-radius: 8px;
  overflow: hidden;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  }

  :deep(.el-card__body) {
    padding: 0;
  }
}

.card-cover {
  height: 160px;
  overflow: hidden;

  .cover-image {
    width: 100%;
    height: 100%;
  }

  .cover-fallback {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
  }
}

.card-body {
  padding: 12px 16px 16px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-desc {
  font-size: 13px;
  color: #909399;
  margin: 0 0 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.meta-teacher {
  display: flex;
  align-items: center;
  gap: 6px;

  .teacher-name {
    font-size: 13px;
    color: #606266;
  }
}

.browse-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}
</style>
