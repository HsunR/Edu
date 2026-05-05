import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getCourseDetail, getCourseList, getTeachingCourses } from '@/api/course/course'
import { getCategoryTree } from '@/api/course/chapter'
import type { CourseDetailVO, CategoryVO, CourseQueryRequest, CourseVO } from '@/api/course/types'
import type { PageResult } from '@/types/api'
import { useUserStore } from '@/stores/user'

export const useCourseStore = defineStore('course', () => {
  const currentCourse = ref<CourseDetailVO | null>(null)
  const categoryTree = ref<CategoryVO[]>([])
  const courseList = ref<CourseVO[]>([])
  const courseTotal = ref(0)
  const teachingCourses = ref<CourseVO[]>([])
  const teachingTotal = ref(0)

  const isCourseOwner = computed(() => {
    const userStore = useUserStore()
    return currentCourse.value?.teacherId === userStore.userInfo?.userId
  })

  async function fetchCourseDetail(courseId: string) {
    currentCourse.value = await getCourseDetail(courseId)
  }

  async function fetchCategoryTree() {
    if (categoryTree.value.length > 0) return
    categoryTree.value = await getCategoryTree()
  }

  async function fetchCourseList(params: CourseQueryRequest) {
    const result: PageResult<CourseVO> = await getCourseList(params)
    courseList.value = result.records
    courseTotal.value = result.total
  }

  async function fetchTeachingCourses(params: CourseQueryRequest) {
    const result: PageResult<CourseVO> = await getTeachingCourses(params)
    teachingCourses.value = result.records
    teachingTotal.value = result.total
  }

  function clearCurrentCourse() {
    currentCourse.value = null
  }

  return {
    currentCourse,
    categoryTree,
    courseList,
    courseTotal,
    teachingCourses,
    teachingTotal,
    isCourseOwner,
    fetchCourseDetail,
    fetchCategoryTree,
    fetchCourseList,
    fetchTeachingCourses,
    clearCurrentCourse
  }
})
