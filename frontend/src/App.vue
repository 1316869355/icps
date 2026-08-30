<template>
  <div id="app">
    <!-- 登录页面布局 -->
    <template v-if="$route.path === '/login'">
      <router-view />
    </template>
    
    <!-- 主应用布局 -->
    <template v-else>
      <el-container class="layout-container" v-if="isLoggedIn">
        <!-- 顶部导航栏 -->
        <el-header class="header">
          <div class="header-content">
            <div class="logo">
              <h2>ICPS - 学生信息管理系统</h2>
            </div>
            <div class="user-info">
              <el-dropdown>
                <span class="el-dropdown-link">
                  {{ userInfo?.name || '用户' }}
                  <el-icon class="el-icon--right">
                    <arrow-down />
                  </el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </div>
          </div>
        </el-header>

        <el-container>
          <!-- 侧边栏菜单 -->
          <el-aside width="200px">
            <el-menu
              :default-active="$route.path"
              class="sidebar-menu"
              router
              background-color="#304156"
              text-color="#bfcbd9"
              active-text-color="#409EFF"
            >
              <el-menu-item index="/dashboard">
                <el-icon><data-board /></el-icon>
                <span>仪表盘</span>
              </el-menu-item>
              
              <el-sub-menu index="student" v-if="userRole === 'student'">
                <template #title>
                  <el-icon><user /></el-icon>
                  <span>学生功能</span>
                </template>
                <el-menu-item index="/student/info">个人信息</el-menu-item>
                <el-menu-item index="/student/grades">成绩查询</el-menu-item>
                <el-menu-item index="/student/courses">课程信息</el-menu-item>
              </el-sub-menu>

              <el-sub-menu index="teacher" v-if="userRole === 'teacher' || userRole === 'admin'">
                <template #title>
                  <el-icon><user-filled /></el-icon>
                  <span>教师功能</span>
                </template>
                <el-menu-item index="/teacher/students">学生管理</el-menu-item>
                <el-menu-item index="/teacher/search">学生搜索</el-menu-item>
                <el-menu-item index="/teacher/statistics">数据统计</el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-aside>

          <!-- 主要内容区域 -->
          <el-main class="main-content">
            <router-view />
          </el-main>
        </el-container>
      </el-container>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const isLoggedIn = computed(() => authStore.isLoggedIn)
const userRole = computed(() => authStore.userRole)
const userInfo = computed(() => authStore.userInfo)

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    authStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    // 用户取消操作
  }
}

onMounted(() => {
  // 检查登录状态
  if (!isLoggedIn.value && route.path !== '/login') {
    router.push('/login')
  }
  
  // 如果已登录但访问根路径，跳转到仪表盘
  if (isLoggedIn.value && route.path === '/') {
    router.push('/dashboard')
  }
})
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  
  .header {
    background-color: #409EFF;
    color: white;
    padding: 0;
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      height: 100%;
      padding: 0 20px;
      
      .logo h2 {
        margin: 0;
        font-size: 20px;
      }
      
      .user-info {
        .el-dropdown-link {
          color: white;
          cursor: pointer;
        }
      }
    }
  }
  
  .sidebar-menu {
    height: 100%;
    border-right: none;
  }
  
  .main-content {
    background-color: #f5f7fa;
    padding: 20px;
    overflow-y: auto;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .layout-container {
    .el-aside {
      width: 100% !important;
    }
  }
}
</style>