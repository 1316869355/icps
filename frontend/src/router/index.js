import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 页面组件
const Login = () => import('@/views/Login.vue')
const Student = () => import('@/views/Student.vue')
const StudentInfo = () => import('@/views/student/Info.vue')
const StudentGrades = () => import('@/views/student/Grades.vue')
const StudentCourses = () => import('@/views/student/Courses.vue')

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  // 学生路由
  {
    path: '/student',
    name: 'Student',
    component: Student,
    meta: { requiresAuth: true, role: 'student' },
    redirect: '/student/info',
    children: [
      {
        path: 'info',
        name: 'StudentInfo',
        component: StudentInfo,
        meta: { requiresAuth: true, role: 'student' }
      },
      {
        path: 'grades',
        name: 'StudentGrades',
        component: StudentGrades,
        meta: { requiresAuth: true, role: 'student' }
      },
      {
        path: 'courses',
        name: 'StudentCourses',
        component: StudentCourses,
        meta: { requiresAuth: true, role: 'student' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 检查是否需要认证
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login')
    return
  }
  
  // 检查角色权限
  if (to.meta.role && to.meta.role !== authStore.userRole) {
    // 根据当前用户角色重定向到对应的页面
    if (authStore.userRole === 'student') {
      next('/student')
    } else {
      next('/login')
    }
    return
  }
  
  // 如果已登录且访问登录页，根据角色重定向到对应页面
  if (to.path === '/login' && authStore.isLoggedIn) {
    if (authStore.userRole === 'student') {
      next('/student')
    } else if (authStore.userRole === 'teacher') {
      // 后续可添加教师页面
      next('/student')
    } else {
      next('/student')
    }
    return
  }
  
  next()
})

export default router