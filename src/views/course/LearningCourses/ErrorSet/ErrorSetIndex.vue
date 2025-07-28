<script setup lang="ts">

</script>

<template>
  <div>
    <el-card>
      <template #header>
        <div class="card-header">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/course/LearningCourses' }">我学的课</el-breadcrumb-item>
            <el-breadcrumb-item>错题集</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
      </template>

      错题集
    </el-card>
  </div>
</template>

<style scoped>

</style>
