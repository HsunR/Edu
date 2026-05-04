import { ref, reactive, type Ref } from 'vue'
import type { PageRequest, PageResult } from '@/types/api'

interface UseTableOptions<T, Q extends PageRequest> {
  fetchFn: (params: Q) => Promise<PageResult<T>>
  defaultParams?: Partial<Q>
  defaultPageSize?: number
  immediate?: boolean
}

export function useTable<T, Q extends PageRequest = PageRequest>(
  options: UseTableOptions<T, Q>
) {
  const { fetchFn, defaultParams = {}, defaultPageSize = 10, immediate = false } = options

  const loading = ref(false)
  const data: Ref<T[]> = ref([])
  const total = ref(0)

  const params = reactive<Q>({
    current: 1,
    pageSize: defaultPageSize,
    ...defaultParams,
  } as Q)

  async function fetchData() {
    loading.value = true
    try {
      const result = await fetchFn(params)
      data.value = result.records
      total.value = result.total
    } finally {
      loading.value = false
    }
  }

  function reset() {
    params.current = 1
    Object.assign(params, { ...defaultParams, current: 1, pageSize: defaultPageSize })
    fetchData()
  }

  function handlePageChange(page: number) {
    params.current = page
    fetchData()
  }

  function handleSizeChange(size: number) {
    params.pageSize = size
    params.current = 1
    fetchData()
  }

  if (immediate) {
    fetchData()
  }

  return {
    loading,
    data,
    total,
    params,
    fetchData,
    reset,
    handlePageChange,
    handleSizeChange,
  }
}
