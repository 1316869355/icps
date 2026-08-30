import request from './request'

// 课程相关API
export const courseApi = {
  // 获取课程列表（分页，page 从 1 开始）
  getCourses(params) {
    return request.get('/courses', { params })
  },

  // 根据ID获取课程详情
  getCourseById(id) {
    return request.get(`/courses/${id}`)
  },

  // 获取学生已选课程（studentId 支持用户ID / 学号 / 身份证号）
  getSelectedCourses(studentId) {
    return request.get(`/students/${studentId}/courses`)
  },

  // 获取可选课程
  getAvailableCourses(params) {
    return request.get('/courses', { params })
  },

  // 选课
  selectCourse(studentId, courseId) {
    return request.post(`/students/${studentId}/courses/${courseId}/select`)
  },

  // 退课
  dropCourse(studentId, courseId) {
    return request.delete(`/students/${studentId}/courses/${courseId}/drop`)
  },

  // 创建课程（管理员）
  createCourse(data) {
    return request.post('/courses', data)
  },

  // 更新课程信息（管理员）
  updateCourse(id, data) {
    return request.put(`/courses/${id}`, data)
  },

  // 删除课程（管理员）
  deleteCourse(id) {
    return request.delete(`/courses/${id}`)
  }
}

// 成绩相关API
export const gradeApi = {
  // 获取学生成绩
  getGrades(studentId, params) {
    return request.get(`/students/${studentId}/grades`, { params })
  },

  // 获取课程成绩
  getCourseGrades(courseId, params) {
    return request.get(`/courses/${courseId}/grades`, { params })
  },

  // 录入成绩（教师）
  inputGrade(data) {
    return request.post('/grades', data)
  },

  // 更新成绩（教师）
  updateGrade(id, data) {
    return request.put(`/grades/${id}`, data)
  }
}
