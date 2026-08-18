export function parseTime(time: string | number | Date | null, pattern?: string): string | null {
  if (!time) return null
  const date = new Date(time)
  if (isNaN(date.getTime())) return null

  const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
  const formatObj: Record<string, number> = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay(),
  }

  return format.replace(/{([ymdhisa])+}/g, (result, key) => {
    const value = formatObj[key]
    return key === 'a'
      ? ['日', '一', '二', '三', '四', '五', '六'][value]
      : String(value).padStart(2, '0')
  })
}

export function handleTree<T extends Record<string, unknown>>(
  data: T[],
  idKey = 'id',
  parentIdKey = 'parentId',
  childrenKey = 'children'
): T[] {
  const map = new Map<unknown, T & { [K in typeof childrenKey]?: T[] }>()
  const tree: (T & { [K in typeof childrenKey]?: T[] })[] = []

  data.forEach(item => map.set(item[idKey], { ...item, [childrenKey]: [] }))

  map.forEach(item => {
    const parentId = item[parentIdKey]
    if (parentId && map.has(parentId)) {
      map.get(parentId)![childrenKey]!.push(item)
    } else {
      tree.push(item)
    }
  })

  return tree as T[]
}

export function addDateRange(params: Record<string, unknown>, dateRange: [string, string] | null, propName?: string) {
  const search = { ...params }
  if (dateRange && dateRange.length === 2) {
    const beginTime = propName ? `${propName}BeginTime` : 'beginTime'
    const endTime = propName ? `${propName}EndTime` : 'endTime'
    search[beginTime] = dateRange[0]
    search[endTime] = dateRange[1]
  }
  return search
}

export function resetForm(formRef: { resetFields: () => void } | null) {
  formRef?.resetFields()
}

export function getNormalPath(path: string): string {
  return path.replace(/\/\//g, '/')
}

export function selectDictLabel(datas: Array<{ value: string | number; label: string }>, value: string | number): string {
  const item = datas.find(d => d.value === value)
  return item ? item.label : String(value)
}

export function selectDictLabels(datas: Array<{ value: string | number; label: string }>, value: string | number, separator = ','): string {
  const values = Array.isArray(value) ? value : [value]
  const labels = values.map(v => {
    const item = datas.find(d => d.value === v)
    return item ? item.label : String(v)
  })
  return labels.join(separator)
}

export function tansParams(params: Record<string, unknown>): string {
  let result = ''
  for (const propName of Object.keys(params)) {
    const value = params[propName]
    const part = encodeURIComponent(propName) + '='
    if (value !== null && value !== '' && typeof value !== 'undefined') {
      if (typeof value === 'object') {
        for (const key of Object.keys(value as Record<string, unknown>)) {
          const val = (value as Record<string, unknown>)[key]
          if (val !== null && val !== '' && typeof val !== 'undefined') {
            const paramsKey = propName + '[' + key + ']'
            const subPart = encodeURIComponent(paramsKey) + '='
            result += subPart + encodeURIComponent(val as string) + '&'
          }
        }
      } else {
        result += part + encodeURIComponent(value as string) + '&'
      }
    }
  }
  return result
}

export function blobValidate(data: Blob): boolean {
  return data.type !== 'application/json'
}
