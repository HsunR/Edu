<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getKnowledgeTree, getPointQuestions, getPointSections } from '@/api/knowledge/index'
import type { KnowledgeTreeVO } from '@/api/knowledge/types'
import OutlineMode from './OutlineMode.vue'
import MindMappingMode from './MindMappingMode.vue'
import AtlasMode from './AtlasMode.vue'

const route = useRoute()
const courseId = Number(route.params.id)

const loading = ref(false)
const treeData = ref<KnowledgeTreeVO[]>([])
const activeTab = ref('outline')

const selectedPoint = ref<KnowledgeTreeVO | null>(null)
const selectedPointQuestions = ref<number[]>([])
const selectedPointSections = ref<number[]>([])
const detailDrawerVisible = ref(false)

async function loadTree() {
  loading.value = true
  try {
    treeData.value = await getKnowledgeTree(courseId)
  } finally {
    loading.value = false
  }
}

async function handlePointClick(point: KnowledgeTreeVO) {
  selectedPoint.value = point
  detailDrawerVisible.value = true
  try {
    const [qIds, sIds] = await Promise.all([
      getPointQuestions(point.pointId),
      getPointSections(point.pointId)
    ])
    selectedPointQuestions.value = qIds
    selectedPointSections.value = sIds
  } catch {
    selectedPointQuestions.value = []
    selectedPointSections.value = []
  }
}

onMounted(loadTree)
</script>

<template>
  <div class="curriculum-map">
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/LearningCourses' }">我学的课</el-breadcrumb-item>
            <el-breadcrumb-item>知识图谱</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="card">
        <el-tab-pane label="大纲模式" name="outline">
          <OutlineMode :tree-data="treeData" :loading="loading" @point-click="handlePointClick" />
        </el-tab-pane>
        <el-tab-pane label="思维导图模式" name="mindmap">
          <MindMappingMode :tree-data="treeData" :loading="loading" @point-click="handlePointClick" />
        </el-tab-pane>
        <el-tab-pane label="图谱模式" name="atlas">
          <AtlasMode :tree-data="treeData" :loading="loading" @point-click="handlePointClick" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <el-drawer v-model="detailDrawerVisible" :title="selectedPoint?.pointName || '知识点详情'" size="350px">
      <template v-if="selectedPoint">
        <div class="point-detail">
          <h3>{{ selectedPoint.pointName }}</h3>
          <p v-if="selectedPoint.description" class="point-desc">{{ selectedPoint.description }}</p>

          <el-divider />

          <div class="detail-section">
            <h4>关联题目</h4>
            <el-empty v-if="selectedPointQuestions.length === 0" description="暂无" :image-size="40" />
            <div v-else class="tag-list">
              <el-tag v-for="qId in selectedPointQuestions" :key="qId" size="small" style="margin: 0 4px 4px 0">
                题目 #{{ qId }}
              </el-tag>
            </div>
          </div>

          <el-divider />

          <div class="detail-section">
            <h4>关联章节</h4>
            <el-empty v-if="selectedPointSections.length === 0" description="暂无" :image-size="40" />
            <div v-else class="tag-list">
              <el-tag v-for="sId in selectedPointSections" :key="sId" type="success" size="small" style="margin: 0 4px 4px 0">
                小节 #{{ sId }}
              </el-tag>
            </div>
          </div>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<style scoped lang="scss">
:deep(.el-tabs__nav) {
  border: 2px solid #d8e6fa;
  border-radius: 25px;
}

:deep(.el-tabs__item) {
  color: #666;
  padding: 0 20px;
  height: 40px;
  line-height: 40px;
  border-radius: 20px;
  border: none;
  font-weight: 600;
}

:deep(.el-tabs__item.is-active) {
  color: #409eff;
  background-color: #edf4ff;
}

.point-detail {
  h3 {
    font-size: 18px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 8px;
  }

  .point-desc {
    font-size: 14px;
    color: #606266;
    margin: 0;
    line-height: 1.6;
  }
}

.detail-section {
  h4 {
    margin: 0 0 8px;
    font-size: 15px;
    color: #303133;
  }
}
</style>
