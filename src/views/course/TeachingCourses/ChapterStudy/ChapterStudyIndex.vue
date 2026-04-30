<script setup lang="ts">
import { ref } from 'vue'
import { ElButton } from 'element-plus'
import { Open } from '@element-plus/icons-vue';
import type { RenderContentContext, RenderContentFunction } from 'element-plus'

interface Tree {
  id: number
  label: string
  children?: Tree[]
}
type Node = RenderContentContext['node']
type Data = RenderContentContext['data']

let id = 1000

const filterText = ref('')
const treeRef = ref()

watch(filterText, (val) => {
  treeRef.value.filter(val)
})

const filterNode = (value, data) => {
  if (!value) return true
  return data.label.includes(value)
}

const append = (data: Data) => {
  const newChild = { id: id++, label: 'testtest', children: [] }
  if (!data.children) {
    data.children = []
  }
  data.children.push(newChild)
  dataSource.value = [...dataSource.value]
}

const remove = (node: Node, data: Data) => {
  const parent = node.parent
  const children: Tree[] = parent?.data.children || parent?.data
  const index = children.findIndex((d) => d.id === data.id)
  children.splice(index, 1)
  dataSource.value = [...dataSource.value]
}

const dataSource = ref<Tree[]>([
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
])
</script>

<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/TeachingCourses' }">我教的课</el-breadcrumb-item>
            <el-breadcrumb-item>章节学习</el-breadcrumb-item>
          </el-breadcrumb>

        </div>
      </template>

      <div class="custom-tree-container">
        <div class="top">
          <el-button class="gradient-btn" round style="color: #fff; margin-bottom: 10px;">新建目录</el-button>
          <div class="empty"></div>
          <el-input
              v-model="filterText"
              placeholder="搜索"
              class="input"
              style="height: 5vh;"
          />
        </div>

        <el-tree ref="treeRef" style="border-right: 1px solid #eee; padding: 20px;" :data="dataSource" node-key="id"
          default-expand-all :expand-on-click-node="false" :filter-node-method="filterNode" >
          <template #default="{ node, data }">
            <div class="custom-tree-node">
              <span class="custom-node">
                <i v-if="data.children && data.children.length" class="icon-parent"></i>
                <i class="icon-cicle" v-else>{{ node.id }}</i>
                <span>{{ node.label }}</span>
              </span>
              <div>
                <el-button type="primary" link @click="append(data)">
                  添加子章节
                </el-button>
                <el-button style="margin: 0 8px" type="danger" link @click="remove(node, data)">
                  删除
                </el-button>
              </div>
            </div>
          </template>
        </el-tree>
      </div>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
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
    margin-top: 10px;
  }
  
}
</style>
