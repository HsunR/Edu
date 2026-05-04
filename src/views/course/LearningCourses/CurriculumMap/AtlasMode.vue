<script setup lang="ts">
import type { KnowledgeTreeVO } from '@/api/knowledge/types'

defineProps<{
  treeData: KnowledgeTreeVO[]
  loading: boolean
}>()

const emit = defineEmits<{
  pointClick: [point: KnowledgeTreeVO]
}>()

const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#9b59b6', '#1abc9c']
</script>

<template>
  <div v-loading="loading" class="atlas-mode">
    <el-empty v-if="!loading && treeData.length === 0" description="暂无知识点数据" />

    <div v-else class="atlas-container">
      <svg class="atlas-svg" viewBox="0 0 800 500" xmlns="http://www.w3.org/2000/svg">
        <line
          v-for="(root, idx) in treeData"
          :key="'line-' + root.pointId"
          :x1="400"
          :y1="250"
          :x2="80 + idx * 140"
          :y2="80"
          :stroke="colors[idx % colors.length]"
          stroke-width="2"
          stroke-opacity="0.4"
        />
        <template v-for="(root, idx) in treeData" :key="'children-' + root.pointId">
          <line
            v-for="(child, cidx) in root.children || []"
            :key="'cline-' + child.pointId"
            :x1="80 + idx * 140"
            :y1="80"
            :x2="40 + idx * 140 + cidx * 60"
            :y2="400"
            :stroke="colors[idx % colors.length]"
            stroke-width="1"
            stroke-opacity="0.3"
          />
        </template>

        <circle cx="400" cy="250" r="30" fill="#667eea" />
        <text x="400" y="255" text-anchor="middle" fill="#fff" font-size="12" font-weight="600">知识体系</text>

        <g
          v-for="(root, idx) in treeData"
          :key="'root-' + root.pointId"
          class="atlas-node"
          @click="emit('pointClick', root)"
        >
          <circle
            :cx="80 + idx * 140"
            :cy="80"
            r="22"
            :fill="colors[idx % colors.length]"
            class="node-circle"
          />
          <text
            :x="80 + idx * 140"
            :y="85"
            text-anchor="middle"
            fill="#fff"
            font-size="10"
            font-weight="600"
          >
            {{ root.pointName.length > 4 ? root.pointName.substring(0, 4) + '..' : root.pointName }}
          </text>
          <text
            :x="80 + idx * 140"
            :y="115"
            text-anchor="middle"
            fill="#606266"
            font-size="10"
          >
            {{ root.pointName }}
          </text>
        </g>

        <g
          v-for="(root, idx) in treeData"
          :key="'leaves-' + root.pointId"
        >
          <g
            v-for="(child, cidx) in root.children || []"
            :key="'leaf-' + child.pointId"
            class="atlas-node"
            @click="emit('pointClick', child)"
          >
            <circle
              :cx="40 + idx * 140 + cidx * 60"
              :cy="400"
              r="16"
              :fill="colors[idx % colors.length]"
              fill-opacity="0.6"
              class="node-circle"
            />
            <text
              :x="40 + idx * 140 + cidx * 60"
              :y="404"
              text-anchor="middle"
              fill="#fff"
              font-size="8"
            >
              {{ child.pointName.length > 3 ? child.pointName.substring(0, 3) + '..' : child.pointName }}
            </text>
            <text
              :x="40 + idx * 140 + cidx * 60"
              :y="428"
              text-anchor="middle"
              fill="#909399"
              font-size="9"
            >
              {{ child.pointName }}
            </text>
          </g>
        </g>
      </svg>
    </div>
  </div>
</template>

<style scoped lang="scss">
.atlas-mode {
  min-height: 300px;
}

.atlas-container {
  display: flex;
  justify-content: center;
  padding: 16px;
}

.atlas-svg {
  width: 100%;
  max-width: 800px;
  height: auto;
}

.atlas-node {
  cursor: pointer;

  .node-circle {
    transition: r 0.2s;
  }

  &:hover .node-circle {
    r: 28;
  }
}
</style>
