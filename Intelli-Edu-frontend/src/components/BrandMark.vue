<script setup lang="ts">
defineProps<{ compact?: boolean }>()
</script>

<template>
  <RouterLink to="/" class="inline-flex items-center gap-3">
    <span
      class="relative grid size-10 place-items-center rounded-2xl bg-ink text-lime shadow-lg shadow-ink/15"
    >
      <span class="text-xl font-black -tracking-widest">IE</span>
      <span class="absolute -right-0.5 -top-0.5 size-2.5 rounded-full bg-lime ring-2 ring-white" />
    </span>
    <span v-if="!compact" class="leading-none">
      <span class="block text-base font-extrabold tracking-tight">Intelli Edu</span>
      <span class="mt-1 block text-[9px] font-bold tracking-[0.22em] text-[#8c948e]"
        >LEARN BEYOND</span
      >
    </span>
  </RouterLink>
</template>
