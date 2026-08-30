import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// 页面组件
const Login = () => import('@/views/Login.vue')
const Dashboard = () => import('@/views/Dashboard.vue')
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
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  // 学生路由 - 直接使用子路由，避免重复布局
  {
    path: '/student/info',
    name: 'StudentInfo',
    component: StudentInfo,
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/student/grades',
    name: 'StudentGrades',
    component: StudentGrades,
    meta: { requiresAuth: true, role: 'student' }
  },
  {
    path: '/student/courses',
    name: 'StudentCourses',
    component: StudentCourses,
    meta: { requiresAuth: true, role: 'student' }
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
    // 角色不匹配时统一回仪表盘（/student 不是已注册路由，直接跳转会失败）
    next('/dashboard')
    return
  }
  
  // 如果已登录且访问登录页，根据角色重定向到对应页面
  if (to.path === '/login' && authStore.isLoggedIn) {
    next('/dashboard')
    return
  }
  
  next()
})

export default router