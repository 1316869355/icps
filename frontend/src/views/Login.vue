<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 class="login-title">ICPS 学生信息管理系统</h2>
      
      <el-form 
        :model="loginForm" 
        :rules="loginRules" 
        ref="loginFormRef"
        class="login-form"
        @keyup.enter="handleKeyup"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            prefix-icon="User"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        
        <el-form-item prop="role">
          <el-select 
            v-model="loginForm.role" 
            placeholder="请选择身份"
            size="large"
            style="width: 100%"
          >
            <el-option label="学生" value="student" />
            <el-option label="教师" value="teacher" />
          </el-select>
        </el-form-item>
        
        <el-form-item>
          <el-button 
            type="primary" 
            size="large" 
            @click="handleLogin"
            :loading="loading"
            style="width: 100%"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores/auth'
import { UserRole } from '../types'

const router = useRouter()
const authStore = useAuthStore()

const loginFormRef = ref()
const loading = ref(false)

// 使用更现代的响应式写法
const loginForm = reactive({
  username: '',
  password: '',
  role: UserRole.STUDENT
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择身份', trigger: 'change' }
  ]
}

// 计算属性：表单是否有效
const isFormValid = computed(() => {
  return loginForm.username && loginForm.password && loginForm.role
})

// 处理登录逻辑
const handleLogin = async () => {
  try {
    // 表单验证
    await loginFormRef.value.validate()
    
    loading.value = true
    
    // 调用认证存储的登录方法
    const result = await authStore.login(loginForm)
    
    if (result.success) {
      ElMessage.success('登录成功')
      
      // 登录成功后跳转到仪表盘
      router.push('/dashboard')
    } else {
      ElMessage.error(result.message || '登录失败')
    }
  } catch (error) {
    console.error('Login error:', error)
    
    // 更详细的错误处理
    if (error.errors) {
      // 表单验证错误
      ElMessage.warning('请检查表单输入')
    } else {
      // 网络或其他错误
      ElMessage.error(error.message || '登录失败，请检查网络连接')
    }
  } finally {
    loading.value = false
  }
}

// 键盘事件支持
const handleKeyup = (event) => {
  if (event.key === 'Enter' && isFormValid.value) {
    handleLogin()
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 40px 30px;
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-weight: 600;
}

.login-form {
  margin-top: 20px;
}

:deep(.el-input__inner) {
  border-radius: 8px;
}

:deep(.el-button) {
  border-radius: 8px;
}
</style>