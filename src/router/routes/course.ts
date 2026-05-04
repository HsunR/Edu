import type { RouteRecordRaw } from 'vue-router'
import Layout from '@/layout/index.vue'

export const courseRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: Layout,
    redirect: '/course/LearningCourses',
    meta: { title: '课程', icon: 'education' },
    children: [
      {
        path: '/course/LearningCourses',
        component: () => import('@/views/course/LearningCourses/index.vue'),
        name: 'LearningCourse',
        meta: { title: '我学的课', icon: 'education' },
      },
      {
        path: '/course/LearningCourses/CourseDetails/:id',
        component: () => import('@/views/course/LearningCourses/LearingIndex.vue'),
        name: 'LearningCourseDetails',
        meta: { title: '课程详情', hidden: true },
        children: [
          {
            path: 'AITeachingAssistantLearning',
            component: () => import('@/views/course/LearningCourses/AITeachingAssistant/AITeachingAssistantIndex.vue'),
            name: 'AITeachingAssistantLearning',
            meta: { title: 'AI助教', hidden: true, activeMenu: '/AITeachingAssistantLearning' },
          },
          {
            path: 'ChapterStudyLearning',
            component: () => import('@/views/course/LearningCourses/ChapterStudy/ChapterStudyIndex.vue'),
            name: 'ChapterStudyLearning',
            meta: { title: '章节学习', hidden: true, activeMenu: '/ChapterStudyLearning' },
          },
          {
            path: 'CourseWorkLearning',
            component: () => import('@/views/course/LearningCourses/CourseWork/CourseWorkIndex.vue'),
            name: 'CourseWorkLearning',
            meta: { title: '课程作业', hidden: true, activeMenu: '/CourseWorkLearning' },
          },
          {
            path: 'CourseExamsLearning',
            component: () => import('@/views/course/LearningCourses/CourseExams/CourseExamsIndex.vue'),
            name: 'CourseExamsLearning',
            meta: { title: '课程考试', hidden: true },
          },
          {
            path: 'CourseMaterialsLearning',
            component: () => import('@/views/course/LearningCourses/CourseMaterials/CourseMaterialsIndex.vue'),
            name: 'CourseMaterialsLearning',
            meta: { title: '课程资料', hidden: true },
          },
          {
            path: 'CurriculumMapLearning',
            component: () => import('@/views/course/LearningCourses/CurriculumMap/CurriculumMapIndex.vue'),
            name: 'CurriculumMapLearning',
            meta: { title: '知识图谱', hidden: true },
          },
          {
            path: 'ErrorSetLearning',
            component: () => import('@/views/course/LearningCourses/ErrorSet/ErrorSetIndex.vue'),
            name: 'ErrorSetLearning',
            meta: { title: '错题集', hidden: true },
          },
          {
            path: 'ExamAnswer/:examId',
            component: () => import('@/views/course/LearningCourses/ExamAnswer/index.vue'),
            name: 'ExamAnswerLearning',
            meta: { title: '答题', hidden: true },
          },
        ],
      },
      {
        path: '/course/TeachingCourses',
        component: () => import('@/views/course/TeachingCourses/index.vue'),
        name: 'TeachingCourse',
        meta: { title: '我教的课', icon: 'education' },
      },
      {
        path: '/course/TeachingCourses/CourseDetails/:id',
        component: () => import('@/views/course/TeachingCourses/TeachingIndex.vue'),
        name: 'TeachingCourseDetails',
        meta: { title: '课程详情', hidden: true },
        children: [
          {
            path: 'ClassManagementTeaching',
            component: () => import('@/views/course/TeachingCourses/ClassManagement/ClassManagementIndex.vue'),
            name: 'ClassManagementTeaching',
            meta: { title: '班级管理', hidden: true },
          },
          {
            path: 'ChapterStudyTeaching',
            component: () => import('@/views/course/TeachingCourses/ChapterStudy/ChapterStudyIndex.vue'),
            name: 'ChapterStudyTeaching',
            meta: { title: '章节管理', hidden: true },
          },
          {
            path: 'CourseWorkTeaching',
            component: () => import('@/views/course/TeachingCourses/CourseWork/CourseWorkIndex.vue'),
            name: 'CourseWorkTeaching',
            meta: { title: '课程作业', hidden: true },
          },
          {
            path: 'CourseExamsTeaching',
            component: () => import('@/views/course/TeachingCourses/CourseExams/CourseExamsIndex.vue'),
            name: 'CourseExamsTeaching',
            meta: { title: '课程考试', hidden: true },
          },
          {
            path: 'CourseMaterialsTeaching',
            component: () => import('@/views/course/TeachingCourses/CourseMaterials/CourseMaterialsIndex.vue'),
            name: 'CourseMaterialsTeaching',
            meta: { title: '课程资料', hidden: true },
          },
          {
            path: 'KnowledgePointsTeaching',
            component: () => import('@/views/course/TeachingCourses/KnowledgePoints/KnowledgePointsIndex.vue'),
            name: 'KnowledgePointsTeaching',
            meta: { title: '知识点', hidden: true },
          },
          {
            path: 'QuestionBankManagementTeaching',
            component: () => import('@/views/course/TeachingCourses/QuestionBankManagement/QuestionBankManagementIndex.vue'),
            name: 'QuestionBankManagementTeaching',
            meta: { title: '题库管理', hidden: true },
          },
          {
            path: 'PaperManagementTeaching',
            component: () => import('@/views/course/TeachingCourses/PaperManagement/index.vue'),
            name: 'PaperManagementTeaching',
            meta: { title: '试卷管理', hidden: true },
          },
          {
            path: 'ExamStats/:examId',
            component: () => import('@/views/course/TeachingCourses/ExamStats/index.vue'),
            name: 'ExamStatsTeaching',
            meta: { title: '考试统计', hidden: true },
          },
          {
            path: 'SettingManagementTeaching',
            component: () => import('@/views/course/TeachingCourses/SettingManagement/SettingManagementIndex.vue'),
            name: 'SettingManagementTeaching',
            meta: { title: '课程设置', hidden: true },
          },
        ],
      },
      {
        path: '/course/detail/:courseId',
        component: () => import('@/views/course/Detail/index.vue'),
        name: 'CourseDetail',
        meta: { title: '课程详情', hidden: true },
      },
      {
        path: '/course/createQuestion',
        component: () => import('@/views/course/TeachingCourses/QuestionBankManagement/createQuestion.vue'),
        name: 'CreateQuestion',
        meta: { title: '创建题目', hidden: true },
      },
      {
        path: '/course/createExam/:id',
        component: () => import('@/views/course/TeachingCourses/CourseExams/createExam.vue'),
        name: 'CreateExam',
        meta: { title: '新建作业/考试', hidden: true },
      },
      {
        path: '/course/jobLibrary',
        component: () => import('@/views/course/TeachingCourses/CourseWork/JobLibrary.vue'),
        name: 'JobLibrary',
        meta: { title: '作业库', hidden: true },
      },
      {
        path: '/course/homeworkWorkDetail/:type/:id',
        component: () => import('@/views/course/LearningCourses/CourseExams/homeworkExamDetail.vue'),
        name: 'HomeworkWorkDetail',
        meta: { title: '作业/考试详情', hidden: true },
      },
    ],
  },
  {
    path: '/resource',
    component: Layout,
    meta: { title: '资源管理', icon: 'education' },
    children: [
      {
        path: '',
        component: () => import('@/views/resource/index.vue'),
        name: 'ResourceManagement',
        meta: { title: '我的资源', icon: 'education' },
      },
    ],
  },
  {
    path: '/setting',
    component: Layout,
    children: [
      {
        path: '',
        component: () => import('@/views/setting/index.vue'),
        name: 'Setting',
        meta: { title: '设置', icon: 'system' },
      },
    ],
  },
]
