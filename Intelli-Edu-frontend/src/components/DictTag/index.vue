<template>
  <div>
    <template v-for="(item, index) in options" :key="item.value + ''">
      <span
        v-if="(item.elTagType == 'default' || item.elTagType == '') && (item.elTagClass == '' || item.elTagClass == null) && values.includes(item.value)"
      >{{ item.label + " " }}</span>
      <el-tag
        v-else-if="values.includes(item.value)"
        :disable-transitions="true"
        :type="item.elTagType === 'default' || item.elTagType === '' ? '' : item.elTagType"
        :class="item.elTagClass"
      >{{ item.label + " " }}</el-tag>
    </template>
    <span v-if="unmatchedLabels.length">{{ unmatchedLabels.join(' ') }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

interface DictOption {
  label: string
  value: string | number
  elTagType?: '' | 'default' | 'success' | 'warning' | 'danger' | 'info'
  elTagClass?: string
}

const props = withDefaults(defineProps<{
  options: DictOption[]
  value?: string | number | Array<string | number>
  showValue?: boolean
  separator?: string
}>(), {
  showValue: true,
  separator: ',',
})

const values = computed(() => {
  if (props.value === null || props.value === undefined || props.value === '') return []
  return Array.isArray(props.value)
    ? props.value.map(item => '' + item)
    : String(props.value).split(props.separator)
})

const unmatchedLabels = computed(() => {
  if (!Array.isArray(props.options) || props.options.length === 0) return []
  const matchedValues = props.options
    .filter(opt => values.value.includes(String(opt.value)))
    .map(opt => String(opt.value))
  return values.value
    .filter(v => !matchedValues.includes(v))
    .map(v => String(v))
})
</script>

<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
</style>
