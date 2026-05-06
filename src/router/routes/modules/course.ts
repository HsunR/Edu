import type { RouteRecordRaw } from 'vue-router'
import { UserType } from '@/types/enums'

export const courseRoutes: RouteRecordRaw[] = [
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/home',
    name: 'CourseLayout',
    meta: { title: '课程', icon: 'education' },
    children: [
      {
        path: '/home',
        component: () => import('@/views/home/index.vue'),
        name: 'Index',
        meta: { title: '首页', icon: 'dashboard', affix: true },
      },
      {
        path: '/index',
        component: () => import('@/views/course/Browse/index.vue'),
        name: 'CourseBrowse',
        meta: { title: '课程浏览', icon: 'dashboard' },
      },
      {
        path: '/course/learning',
        component: () => import('@/views/course/LearningCourses/index.vue'),
        name: 'LearningCourse',
        meta: { title: '我学的课', icon: 'education', roles: [UserType.Student, UserType.Teacher, UserType.Admin] },
      },
      {
        path: '/course/learning/:id',
        component: () => import('@/views/course/LearningCourses/LearningIndex.vue'),
        name: 'LearningCourseDetails',
        meta: { title: '学习课程详情', hidden: true, roles: [UserType.Student, UserType.Teacher, UserType.Admin] },
        children: [
          {
            path: 'ai-assistant',
            component: () => import('@/views/course/LearningCourses/AITeachingAssistant/AITeachingAssistantIndex.vue'),
            name: 'LearningAIAssistant',
            meta: { title: 'AI助教', hidden: true, activeMenu: '/course/learning', roles: [UserType.Student, UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'chapters',
            component: () => import('@/views/course/LearningCourses/ChapterStudy/ChapterStudyIndex.vue'),
            name: 'LearningChapters',
            meta: { title: '章节学习', hidden: true, activeMenu: '/course/learning', roles: [UserType.Student, UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'homework',
            component: () => import('@/views/course/LearningCourses/CourseWork/CourseWorkIndex.vue'),
            name: 'LearningHomework',
            meta: { title: '课程作业', hidden: true, activeMenu: '/course/learning', roles: [UserType.Student, UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'exams',
            component: () => import('@/views/course/LearningCourses/CourseExams/CourseExamsIndex.vue'),
            name: 'LearningExams',
            meta: { title: '课程考试', hidden: true, activeMenu: '/course/learning', roles: [UserType.Student, UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'materials',
            component: () => import('@/views/course/LearningCourses/CourseMaterials/CourseMaterialsIndex.vue'),
            name: 'LearningMaterials',
            meta: { title: '课程资料', hidden: true, activeMenu: '/course/learning', roles: [UserType.Student, UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'knowledge-map',
            component: () => import('@/views/course/LearningCourses/CurriculumMap/CurriculumMapIndex.vue'),
            name: 'LearningKnowledgeMap',
            meta: { title: '知识图谱', hidden: true, activeMenu: '/course/learning', roles: [UserType.Student, UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'error-set',
            component: () => import('@/views/course/LearningCourses/ErrorSet/ErrorSetIndex.vue'),
            name: 'LearningErrorSet',
            meta: { title: '错题集', hidden: true, activeMenu: '/course/learning', roles: [UserType.Student, UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'exam-answer/:examId',
            component: () => import('@/views/course/LearningCourses/ExamAnswer/index.vue'),
            name: 'ExamAnswer',
            meta: { title: '答题', hidden: true, activeMenu: '/course/learning', roles: [UserType.Student, UserType.Teacher, UserType.Admin] },
          },
        ],
      },
      {
        path: '/course/teaching',
        component: () => import('@/views/course/TeachingCourses/index.vue'),
        name: 'TeachingCourse',
        meta: { title: '我教的课', icon: 'education', roles: [UserType.Teacher, UserType.Admin] },
      },
      {
        path: '/course/teaching/:id',
        component: () => import('@/views/course/TeachingCourses/TeachingIndex.vue'),
        name: 'TeachingCourseDetails',
        meta: { title: '教学课程详情', hidden: true, roles: [UserType.Teacher, UserType.Admin] },
        children: [
          {
            path: 'classes',
            component: () => import('@/views/course/TeachingCourses/ClassManagement/ClassManagementIndex.vue'),
            name: 'TeachingClasses',
            meta: { title: '班级管理', hidden: true, activeMenu: '/course/teaching', roles: [UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'chapters',
            component: () => import('@/views/course/TeachingCourses/ChapterStudy/ChapterStudyIndex.vue'),
            name: 'TeachingChapters',
            meta: { title: '章节管理', hidden: true, activeMenu: '/course/teaching', roles: [UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'homework',
            component: () => import('@/views/course/TeachingCourses/CourseWork/CourseWorkIndex.vue'),
            name: 'TeachingHomework',
            meta: { title: '课程作业', hidden: true, activeMenu: '/course/teaching', roles: [UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'exams',
            component: () => import('@/views/course/TeachingCourses/CourseExams/CourseExamsIndex.vue'),
            name: 'TeachingExams',
            meta: { title: '课程考试', hidden: true, activeMenu: '/course/teaching', roles: [UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'materials',
            component: () => import('@/views/course/TeachingCourses/CourseMaterials/CourseMaterialsIndex.vue'),
            name: 'TeachingMaterials',
            meta: { title: '课程资料', hidden: true, activeMenu: '/course/teaching', roles: [UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'knowledge-points',
            component: () => import('@/views/course/TeachingCourses/KnowledgePoints/KnowledgePointsIndex.vue'),
            name: 'TeachingKnowledgePoints',
            meta: { title: '知识点', hidden: true, activeMenu: '/course/teaching', roles: [UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'question-bank',
            component: () => import('@/views/course/TeachingCourses/QuestionBankManagement/QuestionBankManagementIndex.vue'),
            name: 'TeachingQuestionBank',
            meta: { title: '题库管理', hidden: true, activeMenu: '/course/teaching', roles: [UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'papers',
            component: () => import('@/views/course/TeachingCourses/PaperManagement/index.vue'),
            name: 'TeachingPapers',
            meta: { title: '试卷管理', hidden: true, activeMenu: '/course/teaching', roles: [UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'exam-stats/:examId',
            component: () => import('@/views/course/TeachingCourses/ExamStats/index.vue'),
            name: 'TeachingExamStats',
            meta: { title: '考试统计', hidden: true, activeMenu: '/course/teaching', roles: [UserType.Teacher, UserType.Admin] },
          },
          {
            path: 'settings',
            component: () => import('@/views/course/TeachingCourses/SettingManagement/SettingManagementIndex.vue'),
            name: 'TeachingSettings',
            meta: { title: '课程设置', hidden: true, activeMenu: '/course/teaching', roles: [UserType.Teacher, UserType.Admin] },
          },
        ],
      },
      {
        path: '/course/detail/:courseId',
        component: () => import('@/views/course/Detail/index.vue'),
        name: 'CourseDetail',
        meta: { title: '课程详情', hidden: true, activeMenu: '/index' },
      },
      {
        path: '/course/create-question',
        component: () => import('@/views/course/TeachingCourses/QuestionBankManagement/createQuestion.vue'),
        name: 'CreateQuestion',
        meta: { title: '创建题目', hidden: true, roles: [UserType.Teacher, UserType.Admin], activeMenu: '/course/teaching' },
      },
      {
        path: '/course/create-exam/:id',
        component: () => import('@/views/course/TeachingCourses/CourseExams/createExam.vue'),
        name: 'CreateExam',
        meta: { title: '新建作业/考试', hidden: true, roles: [UserType.Teacher, UserType.Admin], activeMenu: '/course/teaching' },
      },
      {
        path: '/course/job-library',
        component: () => import('@/views/course/TeachingCourses/CourseWork/JobLibrary.vue'),
        name: 'JobLibrary',
        meta: { title: '作业库', hidden: true, roles: [UserType.Teacher, UserType.Admin], activeMenu: '/course/teaching' },
      },
      {
        path: '/course/homework-detail/:type/:id',
        component: () => import('@/views/course/LearningCourses/CourseExams/homeworkExamDetail.vue'),
        name: 'HomeworkDetail',
        meta: { title: '作业/考试详情', hidden: true, activeMenu: '/course/learning' },
      },
    ],
  },
]
