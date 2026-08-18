import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { RouteLocationNormalizedLoaded } from 'vue-router'

export interface TagView extends Partial<RouteLocationNormalizedLoaded> {
  title?: string
}

export const useTagsViewStore = defineStore('tags-view', () => {
  const visitedViews = ref<TagView[]>([])
  const cachedViews = ref<string[]>([])
  const iframeViews = ref<TagView[]>([])

  function addView(view: TagView) {
    addVisitedView(view)
    addCachedView(view)
  }

  function addIframeView(view: TagView) {
    if (iframeViews.value.some(v => v.path === view.path)) return
    iframeViews.value.push({ ...view, title: view.meta?.title as string || 'no-name' })
  }

  function addVisitedView(view: TagView) {
    if (visitedViews.value.some(v => v.path === view.path)) return
    visitedViews.value.push({ ...view, title: view.meta?.title as string || 'no-name' })
  }

  function addCachedView(view: TagView) {
    if (cachedViews.value.includes(view.name as string)) return
    if (!view.meta?.noCache) {
      cachedViews.value.push(view.name as string)
    }
  }

  function delView(view: TagView) {
    delVisitedView(view)
    delCachedView(view)
  }

  function delVisitedView(view: TagView) {
    const idx = visitedViews.value.findIndex(v => v.path === view.path)
    if (idx > -1) visitedViews.value.splice(idx, 1)
    iframeViews.value = iframeViews.value.filter(v => v.path !== view.path)
  }

  function delIframeView(view: TagView) {
    iframeViews.value = iframeViews.value.filter(v => v.path !== view.path)
  }

  function delCachedView(view: TagView) {
    const idx = cachedViews.value.indexOf(view.name as string)
    if (idx > -1) cachedViews.value.splice(idx, 1)
  }

  function delOthersViews(view: TagView) {
    visitedViews.value = visitedViews.value.filter(
      v => v.meta?.affix || v.path === view.path
    )
    iframeViews.value = iframeViews.value.filter(v => v.path === view.path)
    const idx = cachedViews.value.indexOf(view.name as string)
    cachedViews.value = idx > -1 ? [cachedViews.value[idx]] : []
  }

  function delOthersVisitedViews(view: TagView) {
    visitedViews.value = visitedViews.value.filter(
      v => v.meta?.affix || v.path === view.path
    )
    iframeViews.value = iframeViews.value.filter(v => v.path === view.path)
  }

  function delOthersCachedViews(view: TagView) {
    const idx = cachedViews.value.indexOf(view.name as string)
    if (idx > -1) {
      cachedViews.value = [cachedViews.value[idx]]
    } else {
      cachedViews.value = []
    }
  }

  function delAllViews() {
    visitedViews.value = visitedViews.value.filter(v => v.meta?.affix)
    iframeViews.value = []
    cachedViews.value = []
  }

  function delAllVisitedViews() {
    const affixTags = visitedViews.value.filter(tag => tag.meta?.affix)
    visitedViews.value = affixTags
    iframeViews.value = []
  }

  function delAllCachedViews() {
    cachedViews.value = []
  }

  function delRightTags(view: TagView) {
    const index = visitedViews.value.findIndex(v => v.path === view.path)
    if (index === -1) return
    visitedViews.value = visitedViews.value.filter((item, idx) => {
      if (idx <= index || (item.meta && item.meta.affix)) return true
      const i = cachedViews.value.indexOf(item.name as string)
      if (i > -1) cachedViews.value.splice(i, 1)
      if (item.meta?.link) {
        const fi = iframeViews.value.findIndex(v => v.path === item.path)
        if (fi > -1) iframeViews.value.splice(fi, 1)
      }
      return false
    })
  }

  function delLeftTags(view: TagView) {
    const index = visitedViews.value.findIndex(v => v.path === view.path)
    if (index === -1) return
    visitedViews.value = visitedViews.value.filter((item, idx) => {
      if (idx >= index || (item.meta && item.meta.affix)) return true
      const i = cachedViews.value.indexOf(item.name as string)
      if (i > -1) cachedViews.value.splice(i, 1)
      if (item.meta?.link) {
        const fi = iframeViews.value.findIndex(v => v.path === item.path)
        if (fi > -1) iframeViews.value.splice(fi, 1)
      }
      return false
    })
  }

  function updateVisitedView(view: TagView) {
    const idx = visitedViews.value.findIndex(v => v.path === view.path)
    if (idx > -1) visitedViews.value[idx] = { ...view }
  }

  return {
    visitedViews, cachedViews, iframeViews,
    addView, addVisitedView, addCachedView, addIframeView,
    delView, delVisitedView, delCachedView, delIframeView,
    delOthersViews, delOthersVisitedViews, delOthersCachedViews,
    delAllViews, delAllVisitedViews, delAllCachedViews,
    delRightTags, delLeftTags,
    updateVisitedView
  }
})
