<script setup lang="ts">
import { Open } from '@element-plus/icons-vue';
import { ref, watch } from 'vue'
import {ElButton, FilterNodeMethodFunction, TreeInstance} from 'element-plus'

// 完成任务点
const CompletedPoints = ref(0)
const TotalPoints = ref(0)
const percentage = ref(0)

// 
interface Tree {
  [key: string]: any
}

const defaultProps = {
  children: 'children',
  label: 'label',
}


const dataSource: Tree[] = [
  {
    id: 1,
    label: 'Level one 1',
    children: [
      {
        id: 4,
        label: 'Level two 1-1',
        children: [
          {
            id: 9,
            label: 'Level three 1-1-1',
          },
          {
            id: 10,
            label: 'Level three 1-1-2',
          },
        ],
      },
    ],
  },
  {
    id: 2,
    label: 'Level one 2',
    children: [
      {
        id: 5,
        label: 'Level two 2-1',
      },
      {
        id: 6,
        label: 'Level two 2-2',
      },
    ],
  },
  {
    id: 3,
    label: 'Level one 3',
    children: [
      {
        id: 7,
        label: 'Level two 3-1',
      },
      {
        id: 8,
        label: 'Level two 3-2',
      },
    ],
  },
]

const filterText = ref('')
const treeRef = ref<TreeInstance>()

watch(filterText, (val) => {
  treeRef.value!.filter(val)
})

const filterNode = (value: string, data: Tree) => {
  if (!value) return true
  return data.label.includes(value)
}

const handleNodeClick = (data: Tree) => {
  console.log(data)
  if (data.children && data.children.length) {
    return
  }
}
</script>

<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/LearningCourses' }">我学的课</el-breadcrumb-item>
            <el-breadcrumb-item>章节学习</el-breadcrumb-item>
          </el-breadcrumb>

          <div class="top">

            <div class="finish">
              <span>已完成任务点：{{ CompletedPoints }} / {{ TotalPoints }}</span>
              <span><el-progress :text-inside="true" :stroke-width="20" :percentage="percentage" /></span>
            </div>

            <div class="empty"></div>

            <el-input v-model="filterText" placeholder="搜索" class="input">
              <template #prefix>
                <el-icon class="el-input__icon">
                  <search />
                </el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </template>

      <div class="chapter">
        <el-tree ref="treeRef" style="border-right: 1px solid #eee; padding: 20px;" :data="dataSource" node-key="id"
                 default-expand-all :expand-on-click-node="false" :filter-node-method="filterNode" >
          <template #default="{ node, data }">
            <div class="custom-tree-node"><span class="custom-node">
              <i v-if="data.children && data.children.length" class="icon-parent"></i>
              <i class="icon-cicle" v-else>{{ node.id }}</i>
              <span>{{ node.label }}</span>
            </span>
            </div>
          </template>
        </el-tree>
      </div>

    </el-card>
  </div>
</template>

<style scoped lang="scss">
.card-header {
  height: 10vh;
}

.finish {

  span {
    float: left;
    margin: 30px 10px 10px 10px;
  }
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;

  .custom-node{
    width: 200px;
  }

  .icon-parent {
    padding: 5px 8px;
    margin: 20px 10px;
  }

  .icon-cicle {
    background-color: orange;
    display: inline-block;
    width: 25px;
    height: 25px;
    padding: 7px;
    margin: 0 10px;
    border-radius: 60%;
    font-size: 10px;
    color: #f0f7ff;
  }
}

.el-progress {
  width: 100px;
}


.el-tree {
  --el-tree-node-hover-bg-color: #f5f7fa;
  --el-tree-node-content-height: 60px;
}

.el-tree-node {
  padding: 5px 0;
}

.el-tree-node__content {
  height: 60px;
  line-height: 60px;
}

.top{
  display: flex;

  .gradient-btn{
    flex: 1;
  }

  .empty{
    flex: 4;
  }

  .input{
    flex: 2;
    margin-top: 20px;
    height: 40px;
  }

}
</style>
