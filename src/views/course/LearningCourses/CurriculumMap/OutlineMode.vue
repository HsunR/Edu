<script setup lang="ts">
import { onMounted } from 'vue';
import { useRoute } from 'vue-router';
const route = useRoute();

onMounted(() => {
})

defineProps({
  courseId: String || Number
})
</script>

<template>
  <div>
    大纲模式{{ courseId }}
  </div>
</template>

<style scoped>

</style>
