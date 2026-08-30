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
  // 学生列表（分页 + 姓名/学院/专业/性别筛选）
  getStudents: (params) => request.get('/teacher/students', { params }),

  // 关键词搜索学生（学号 / 姓名 / 班级 / 专业）
  searchStudents: (keyword, params = {}) => request.get('/teacher/students/search', {
    params: { keyword, ...params }
  }),

  // 学生详情（档案 + 课程成绩明细）
  getStudentDetail: (studentId) => request.get(`/teacher/students/${studentId}`),

  // 录入 / 更新成绩
  updateStudentGrade: (studentId, courseId, data) =>
    request.put(`/teacher/students/${studentId}/courses/${courseId}/grade`, data),

  // 课程学生名单（含成绩）
  getCourseStudents: (courseId) => request.get(`/teacher/courses/${courseId}/students`),

  // 综合统计（学生 / 课程 / 选课 / 成绩概览）
  getStatistics: () => request.get('/teacher/statistics')
}