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
  <div v-loading="loading" class="outline-mode">
    <el-empty v-if="!loading && treeData.length === 0" description="暂无知识点数据" />

    <div v-else class="outline-list">
      <div v-for="root in treeData" :key="root.pointId" class="outline-group">
        <div class="outline-root" @click="emit('pointClick', root)">
          <span class="root-icon">📘</span>
          <span class="root-name">{{ root.pointName }}</span>
          <span v-if="root.description" class="root-desc">{{ root.description }}</span>
        </div>
        <div v-if="root.children?.length" class="outline-children">
          <div
            v-for="child in root.children"
            :key="child.pointId"
            class="outline-child"
            @click="emit('pointClick', child)"
          >
            <span class="child-icon">📄</span>
            <span class="child-name">{{ child.pointName }}</span>
            <span v-if="child.description" class="child-desc">{{ child.description }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.outline-mode {
  min-height: 300px;
}

.outline-list {
  padding: 8px;
}

.outline-group {
  margin-bottom: 16px;
}

.outline-root {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f0f7ff;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #dceeff;
  }

  .root-icon {
    font-size: 20px;
  }

  .root-name {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
  }

  .root-desc {
    font-size: 13px;
    color: #909399;
    margin-left: 8px;
  }
}

.outline-children {
  margin-left: 32px;
  margin-top: 8px;
}

.outline-child {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-left: 3px solid #409eff;
  margin-bottom: 4px;
  border-radius: 0 6px 6px 0;
  cursor: pointer;
  transition: background 0.2s;

  &:hover {
    background: #f5f7fa;
  }

  .child-icon {
    font-size: 16px;
  }

  .child-name {
    font-size: 14px;
    color: #303133;
  }

  .child-desc {
    font-size: 12px;
    color: #909399;
    margin-left: 8px;
  }
}
</style>
