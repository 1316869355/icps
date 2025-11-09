import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/api/auth.js'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const isLoggedIn = ref(false)
  const userRole = ref('')
  const userInfo = ref(null)
  const token = ref('')

  // 登录
  const login = async (loginData) => {
    try {
      const response = await authApi.login(loginData)
      
      if (response.data.success) {
        isLoggedIn.value = true
        userRole.value = response.data.role
        userInfo.value = response.data.user
        
        // 保存到本地存储
        localStorage.setItem('auth_token', response.data.token || 'default_token')
        localStorage.setItem('user_role', userRole.value)
        localStorage.setItem('user_info', JSON.stringify(userInfo.value))
        
        return { success: true, data: response.data }
      } else {
        return { success: false, message: response.data.message }
      }
    } catch (error) {
      console.error('Login error:', error)
      return { 
        success: false, 
        message: error.response?.data?.message || '登录失败，请检查网络连接' 
      }
    }
  }

  // 退出登录
  const logout = () => {
    isLoggedIn.value = false
    userRole.value = ''
    userInfo.value = null
    token.value = ''
    
    // 清除本地存储
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user_role')
    localStorage.removeItem('user_info')
    
    // 调用退出登录API
    authApi.logout()
  }

  // 检查登录状态
  const checkAuthStatus = () => {
    const storedToken = localStorage.getItem('auth_token')
    const storedRole = localStorage.getItem('user_role')
    const storedUserInfo = localStorage.getItem('user_info')
    
    if (storedToken && storedRole && storedUserInfo) {
      isLoggedIn.value = true
      userRole.value = storedRole
      userInfo.value = JSON.parse(storedUserInfo)
      token.value = storedToken
    }
  }

  // 更新用户信息
  const updateUserInfo = (info) => {
    userInfo.value = { ...userInfo.value, ...info }
    localStorage.setItem('user_info', JSON.stringify(userInfo.value))
  }

  // 初始化时检查登录状态
  checkAuthStatus()

  return {
    isLoggedIn,
    userRole,
    userInfo,
    token,
    login,
    logout,
    checkAuthStatus,
    updateUserInfo
  }
})