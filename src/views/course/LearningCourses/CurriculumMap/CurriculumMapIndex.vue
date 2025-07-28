<script setup lang="ts">
import { onMounted, ref } from 'vue'
import type { TabsPaneContext } from 'element-plus'
import OutlineMode from './OutlineMode.vue'
import MindMappingMode from './MindMappingMode.vue'
import AtlasMode from './AtlasMode.vue'

import { useRoute } from 'vue-router';
const route = useRoute()

const activeName = ref('first')

const courseId = ref()
onMounted(() => {
  courseId.value = route.params.id
})
const handleClick = (tab: TabsPaneContext, event: Event) => {
  console.log(tab, event)
}
</script>

<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/LearningCourses' }">我学的课</el-breadcrumb-item>
            <el-breadcrumb-item>课程图谱</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <el-tabs v-model="activeName" type="card" class="demo-tabs" @tab-click="handleClick">
        <el-tab-pane label="大纲模式" name="first">
          <OutlineMode :courseId="courseId" />
        </el-tab-pane>
        <el-tab-pane label="思维导图模式" name="second">
          <MindMappingMode :courseId="courseId" />
        </el-tab-pane>
        <el-tab-pane label="图谱模式" name="third">
          <AtlasMode :courseId="courseId" />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.demo-tabs>.el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}

::v-deep .demo-tabs .el-tabs__nav {
  border: 2px solid #d8e6fa;
  border-radius: 25px;
}

::v-deep .demo-tabs .el-tabs__item {
  color: #666;
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
  border-radius: 20px;
  border: none;
  font-weight: 600;
}

::v-deep .demo-tabs .el-tabs__item.is-active {
  color: #409eff;
  background-color: #edf4ff;
}
</style>
