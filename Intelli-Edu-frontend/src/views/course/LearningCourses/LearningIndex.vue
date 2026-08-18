<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
const router = useRouter()
const route = useRoute()

import { ref, watch, onMounted } from 'vue'

import {
  StarFilled,
  Document,
  DocumentChecked,
  Monitor,
  FolderOpened,
  Share,
  DocumentDelete
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

const activeIndex = ref('/')

const menuItems = [
  { key: '/ai-assistant', label: 'AI助教', icon: StarFilled },
  { key: '/chapters', label: '章节学习', icon: Document },
  { key: '/homework', label: '课程作业', icon: DocumentChecked },
  { key: '/exams', label: '课程考试', icon: Monitor },
  { key: '/materials', label: '课程资料', icon: FolderOpened },
  { key: '/knowledge-map', label: '课程图谱', icon: Share },
  { key: '/error-set', label: '错题集', icon: DocumentDelete },
]

function handleSelect(key: string) {
  router.push(`/course/learning/${route.params.id}${key}`)
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

<style scoped lang="scss">
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
