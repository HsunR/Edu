<script setup lang="ts">
import type { KnowledgeTreeVO } from '@/api/knowledge/types'

defineProps<{
  treeData: KnowledgeTreeVO[]
  loading: boolean
}>()

const emit = defineEmits<{
  pointClick: [point: KnowledgeTreeVO]
}>()
</script>

<template>
  <div v-loading="loading" class="mindmap-mode">
    <el-empty v-if="!loading && treeData.length === 0" description="暂无知识点数据" />

    <div v-else class="mindmap-container">
      <div class="mindmap-center">
        <div class="center-node">知识点体系</div>
      </div>
      <div class="mindmap-branches">
        <div v-for="root in treeData" :key="root.pointId" class="branch">
          <div class="branch-root" @click="emit('pointClick', root)">
            <span class="branch-dot"></span>
            <span class="branch-name">{{ root.pointName }}</span>
          </div>
          <div v-if="root.children?.length" class="branch-children">
            <div
              v-for="child in root.children"
              :key="child.pointId"
              class="branch-leaf"
              @click="emit('pointClick', child)"
            >
              <span class="leaf-dot"></span>
              <span class="leaf-name">{{ child.pointName }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.mindmap-mode {
  min-height: 300px;
}

.mindmap-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px;
}

.mindmap-center {
  margin-bottom: 24px;

  .center-node {
    padding: 12px 32px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: #fff;
    border-radius: 24px;
    font-size: 18px;
    font-weight: 600;
  }
}

.mindmap-branches {
  display: flex;
  flex-wrap: wrap;
  gap: 24px;
  justify-content: center;
  width: 100%;
}

.branch {
  min-width: 180px;
  max-width: 280px;
}

.branch-root {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: #ecf5ff;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
  margin-bottom: 8px;

  &:hover {
    background: #d9ecff;
    transform: scale(1.02);
  }

  .branch-dot {
    width: 10px;
    height: 10px;
    border-radius: 50%;
    background: #409eff;
    flex-shrink: 0;
  }

  .branch-name {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
  }
}

.branch-children {
  margin-left: 20px;
  padding-left: 12px;
  border-left: 2px dashed #c6e2ff;
}

.branch-leaf {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  margin-bottom: 4px;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #f5f7fa;
  }

  .leaf-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #67c23a;
    flex-shrink: 0;
  }

  .leaf-name {
    font-size: 13px;
    color: #606266;
  }
}
</style>
