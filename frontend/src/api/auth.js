import request from './request'

// 认证相关API
export const authApi = {
  // 用户登录
  login: (data) => request.post('/auth/login', data),
  
  // 用户退出
  logout: () => request.post('/auth/logout'),
  
  // 获取用户信息
  getUserInfo: () => request.get('/auth/user-info'),
  
  // 刷新token
  refreshToken: () => request.post('/auth/refresh-token')
}

// 学生相关API
export const studentApi = {
  // 获取学生信息
  getStudentInfo: (studentId) => request.get(`/students/${studentId}`),
  
  // 更新学生信息
  updateStudentInfo: (studentId, data) => request.put(`/students/${studentId}`, data),
  
  // 获取学生成绩
  getStudentGrades: (studentId) => request.get(`/students/${studentId}/grades`),
  
  // 获取学生课程
  getStudentCourses: (studentId) => request.get(`/students/${studentId}/courses`)
}

// 教师相关API
export const teacherApi = {
  // 获取学生列表
  getStudents: (params) => request.get('/teacher/students', { params }),
  
  // 搜索学生
  searchStudents: (keyword) => request.get('/teacher/students/search', { 
    params: { keyword } 
  }),
  
  // 获取学生详情
  getStudentDetail: (studentId) => request.get(`/teacher/students/${studentId}`),
  
  // 更新学生成绩
  updateStudentGrade: (studentId, courseId, data) => 
    request.put(`/teacher/students/${studentId}/courses/${courseId}/grade`, data)
}