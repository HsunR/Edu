<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { useCourseStore } from '@/stores/course'
import type { CourseQueryRequest } from '@/api/course/types'
import type { CategoryVO } from '@/api/course/types'
import { CourseStatus } from '@/types/enums'
import CourseCard from '../components/CourseCard.vue'

const courseStore = useCourseStore()

const loading = ref(false)
const searchKeyword = ref('')
const selectedCategoryId = ref<string | null>(null)
const currentPage = ref(1)
const pageSize = ref(12)

const queryParams = reactive<CourseQueryRequest>({
  current: 1,
  pageSize: 12,
  courseName: '',
  categoryId: undefined,
  status: CourseStatus.Published
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

        <CourseCard
          v-for="course in courseStore.courseList"
          :key="course.courseId"
          :course="course"
          mode="browse"
        />
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

.browse-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: center;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}
</style>
