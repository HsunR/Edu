import { createWebHistory, createRouter } from "vue-router";
/* Layout */
import Layout from "@/layout";

/**
 * Note: 路由配置项
 *
 * hidden: true                     // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true                 // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                  // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                  // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                  // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect             // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'               // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * query: '{"id": 1, "name": "ry"}' // 访问路由的默认传递参数
 * roles: ['admin', 'common']       // 访问路由的角色权限
 * permissions: ['a:a:a', 'b:b:b']  // 访问路由的菜单权限
 * meta : {
    noCache: true                   // 如果设置为true，则不会被 <keep-alive> 缓存(默认 false)
    title: 'title'                  // 设置该路由在侧边栏和面包屑中展示的名字
    icon: 'svg-name'                // 设置该路由的图标，对应路径src/assets/icons/svg
    breadcrumb: false               // 如果设置为false，则不会在breadcrumb面包屑中显示
    activeMenu: '/system/user'      // 当路由设置了该属性，则会高亮相对应的侧边栏。
  }
 */

// 公共路由
export const constantRoutes = [
  {
    path: "/redirect",
    component: Layout,
    hidden: true,
    children: [
      {
        path: "/redirect/:path(.*)",
        component: () => import("@/views/redirect/index.vue"),
      },
    ],
  },
  {
    path: "/login",
    component: () => import("@/views/login/loginNew.vue"),
    hidden: true,
  },
  {
    path: "/:pathMatch(.*)*",
    component: () => import("@/views/error/404"),
    hidden: true,
  },
  {
    path: "/401",
    component: () => import("@/views/error/401"),
    hidden: true,
  },
  {
    path: "",
    component: Layout,
    redirect: "/index",
    children: [
      {
        path: "/index",
        component: () => import("@/views/home/index.vue"),
        name: "Index",
        meta: { title: "首页", icon: "dashboard", affix: true },
      },
    ],
  },
  {
    path: "/user",
    component: Layout,
    children: [
      {
        path: "profile",
        component: () => import("@/views/system/user/profile/index"),
        name: "Profile",
        meta: { title: "个人中心", icon: "user" },
      },
    ],
  },
  {
    path: "/message",
    component: Layout,
    children: [
      {
        path: "",
        component: () => import("@/views/message/index"),
        name: "Message",
        meta: { title: "消息", icon: "message" },
      },
    ],
  },
  {
    path: "/",
    component: Layout,
    redirect: "/course/LearningCourses",
    meta: { title: "课程", icon: "education" },
    children: [
      {
        path: "/course/LearningCourses",
        component: () => import("@/views/course/LearningCourses/index"),
        name: "LearningCourse",
        meta: { title: "我学的课", icon: "education" },
      },
      // 我学的课
      {
        path: "/course/LearningCourses/CourseDetails/:id",
        component: () => import("@/views/course/LearningCourses/LearingIndex"),
        name: "LearningCourseDetails",
        hidden: true,
        meta: { title: "课程详情" },
        children: [
          {
            path: "AITeachingAssistantLearning",
            component: () =>
              import(
                "@/views/course/LearningCourses/AITeachingAssistant/AITeachingAssistantIndex"
              ),
            name: "AITeachingAssistantLearning",
            hidden: true,
            meta: {
              title: "AI助教",
              activeMenu: "/AITeachingAssistantLearning",
            },
          },
          {
            path: "ChapterStudyLearning",
            component: () =>
              import(
                "@/views/course/LearningCourses/ChapterStudy/ChapterStudyIndex"
              ),
            name: "ChapterStudyLearning/:id",
            hidden: true,
            meta: {
              title: "章节学习",
              activeMenu: "/ChapterStudyLearning",
            },
          },
          {
            path: "CourseWorkLearning",
            component: () =>
              import(
                "@/views/course/LearningCourses/CourseWork/CourseWorkIndex"
              ),
            name: "CourseWorkLearning/:id",
            hidden: true,
            meta: {
              title: "课程作业",
              activeMenu: "/CourseWorkLearning",
            },
          },
          {
            path: "CourseExamsLearning",
            component: () =>
              import(
                "@/views/course/LearningCourses/CourseExams/CourseExamsIndex"
              ),
            name: "CourseExamsLearning",
            hidden: true,
            meta: { title: "课程考试" },
          },
          {
            path: "CourseMaterialsLearning",
            component: () =>
              import(
                "@/views/course/LearningCourses/CourseMaterials/CourseMaterialsIndex"
              ),
            name: "CourseMaterialsLearning",
            hidden: true,
            meta: { title: "课程资料" },
          },
          {
            path: "CurriculumMapLearning",
            component: () =>
              import(
                "@/views/course/LearningCourses/CurriculumMap/CurriculumMapIndex"
              ),
            name: "CurriculumMapLearning",
            hidden: true,
            meta: { title: "知识图谱" },
          },
          {
            path: "ErrorSetLearning",
            component: () =>
              import("@/views/course/LearningCourses/ErrorSet/ErrorSetIndex"),
            name: "ErrorSetLearning",
            hidden: true,
            meta: { title: "错题集" },
          },
        ],
      },

      {
        path: "/course/TeachingCourses",
        component: () => import("@/views/course/TeachingCourses/index"),
        name: "TeachingCourse",
        meta: { title: "我教的课", icon: "education" },
      },
      // 我教的课
      {
        path: "/course/TeachingCourses/CourseDetails/:id",
        component: () => import("@/views/course/TeachingCourses/TeachingIndex"),
        name: "TeachingCourseDetails",
        hidden: true,
        meta: { title: "课程详情" },
        children: [
          {
            path: "ClassManagementTeaching",
            component: () =>
              import(
                "@/views/course/TeachingCourses/ClassManagement/ClassManagementIndex"
              ),
            name: "ClassManagementTeaching",
            hidden: true,
            meta: { title: "课程管理" },
          },
          {
            path: "ChapterStudyTeaching",
            component: () =>
              import(
                "@/views/course/TeachingCourses/ChapterStudy/ChapterStudyIndex"
              ),
            name: "ChapterStudyTeaching",
            hidden: true,
            meta: { title: "章节学习" },
          },
          {
            path: "CourseWorkTeaching",
            component: () =>
              import(
                "@/views/course/TeachingCourses/CourseWork/CourseWorkIndex"
              ),
            name: "CourseWorkTeaching",
            hidden: true,
            meta: { title: "课程作业" },
          },
          {
            path: "CourseExamsTeaching",
            component: () =>
              import(
                "@/views/course/TeachingCourses/CourseExams/CourseExamsIndex"
              ),
            name: "CourseExamsTeaching",
            hidden: true,
            meta: { title: "课程考试" },
          },
          {
            path: "CourseMaterialsTeaching",
            component: () =>
              import(
                "@/views/course/TeachingCourses/CourseMaterials/CourseMaterialsIndex"
              ),
            name: "CourseMaterialsTeaching",
            hidden: true,
            meta: { title: "课程资料" },
          },
          {
            path: "KnowledgePointsTeaching",
            component: () =>
              import(
                "@/views/course/TeachingCourses/KnowledgePoints/KnowledgePointsIndex"
              ),
            name: "KnowledgePointsTeaching",
            hidden: true,
            meta: { title: "知识点" },
          },
          {
            path: "QuestionBankManagementTeaching",
            component: () =>
              import(
                "@/views/course/TeachingCourses/QuestionBankManagement/QuestionBankManagementIndex"
              ),
            name: "QuestionBankManagementTeaching",
            hidden: true,
            meta: { title: "题库管理" },
          },
          {
            path: "SettingManagementTeaching",
            component: () =>
                import(
                    "@/views/course/TeachingCourses/SettingManagement/SettingManagementIndex.vue"
                    ),
            name: "SettingManagementTeaching",
            hidden: true,
            meta: { title: "管理" },
          },
        ],
      },
      // 创建题目
      {
        path: "/course/createQuestion",
        component: () =>
          import(
            "@/views/course/TeachingCourses/QuestionBankManagement/CreateQuestion"
          ),
        name: "CreateQuestion",
        hidden: true,
        meta: { title: "创建题目" },
      },
      // 新建作业（1）/考试（2）
      {
        path: "/course/createExam/:id",
        component: () =>
          import("@/views/course/TeachingCourses/CourseExams/createExam"),
        name: "createExam",
        hidden: true,
        meta: { title: "新建作业/考试" },
      },
      // 作业库
      {
        path: "/course/jobLibrary",
        component: () =>
          import("@/views/course/TeachingCourses/CourseWork/JobLibrary"),
        name: "JobLibrary",
        hidden: true,
        meta: { title: "作业库" },
      },
      // 进入作业 / 考试详情页 
      {
        path: "/course/homeworkWorkDetail/:type/:id",
        component: () =>
          import(
            "@/views/course/LearningCourses/CourseExams/homeworkExamDetail"
          ),
        name: "homeworkWorkDetail",
        hidden: true,
        meta: { title: "作业/考试详情" },
      },
    ],
  },

  {
    path: "/setting",
    component: Layout,
    children: [
      {
        path: "",
        component: () => import("@/views/setting/index"),
        name: "Setting",
        meta: { title: "设置", icon: "system" },
      },
    ],
  },
];

// 动态路由，基于用户权限动态去加载
export const dynamicRoutes = [
  {
    path: "/system/user-auth",
    component: Layout,
    hidden: true,
    permissions: ["system:user:edit"],
    children: [
      {
        path: "role/:userId(\\d+)",
        component: () => import("@/views/system/user/authRole"),
        name: "AuthRole",
        meta: { title: "分配角色", activeMenu: "/system/user" },
      },
    ],
  },
  {
    path: "/system/role-auth",
    component: Layout,
    hidden: true,
    permissions: ["system:role:edit"],
    children: [
      {
        path: "user/:roleId(\\d+)",
        component: () => import("@/views/system/role/authUser"),
        name: "AuthUser",
        meta: { title: "分配用户", activeMenu: "/system/role" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    }
    return { top: 0 };
  },
});

export default router;
