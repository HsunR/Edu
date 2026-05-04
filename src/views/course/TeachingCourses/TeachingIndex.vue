<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()

import { ref, watch, onMounted } from 'vue'

import {
  Grid,
  Document,
  DocumentChecked,
  Monitor,
  FolderOpened,
  Reading,
  SetUp,
  Setting,
  CopyDocument
} from '@element-plus/icons-vue'

onMounted(() => {
  console.log(route.params.id)
})

watch(
  () => route.path,
  (newPath, oldPath) => {
    console.log(`路径变化: ${oldPath} → ${newPath}`)
  }
)

const activeIndex = ref('1')
const menuItems = [
  { key: '/ClassManagementTeaching', label: '班级管理', icon: Grid },
  { key: '/ChapterStudyTeaching', label: '章节学习', icon: Document },
  { key: '/CourseWorkTeaching', label: '课程作业', icon: DocumentChecked },
  { key: '/CourseExamsTeaching', label: '课程考试', icon: Monitor },
  { key: '/CourseMaterialsTeaching', label: '课程资料', icon: FolderOpened },
  { key: '/QuestionBankManagementTeaching', label: '题库管理', icon: Reading },
  { key: '/PaperManagementTeaching', label: '试卷管理', icon: CopyDocument },
  { key: '/KnowledgePointsTeaching', label: '知识点', icon: SetUp },
  { key: '/SettingManagementTeaching', label: '管理', icon: Setting },
]

function handleSelect(key: string) {
  router.push(`/course/TeachingCourses/CourseDetails/${route.params.id}${key}`)
}
</script>

<template>
  <div class="courseContainer">
    <div class="courseSider">
      <el-menu :default-active="activeIndex" @select="handleSelect">
        <el-menu-item v-for="item in menuItems" :key="item.key" :index="item.key">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </div>

    <div class="courseContent">
      <router-view v-slot="{ Component }">
        <keep-alive>
          <component :is="Component" />
        </keep-alive>
      </router-view>
    </div>
  </div>
</template>

<style scoped>
.courseContainer {
  display: flex;
  height: 100%;
}

.courseSider {
  flex: 1;
  height: 93vh;
}

.el-menu {
  height: 100%;
}

.courseContent {
  flex: 5;
  margin: 10px;
}
</style>
