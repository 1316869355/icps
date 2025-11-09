// 用户相关类型定义

export const UserRole = {
  STUDENT: 'student',
  TEACHER: 'teacher'
}

// 用户信息接口
export interface UserInfo {
  id: number
  username: string
  name: string
  role: string
  avatar?: string
  email?: string
  phone?: string
}

// 登录表单接口
export interface LoginForm {
  username: string
  password: string
  role: string
}

// 登录响应接口
export interface LoginResponse {
  success: boolean
  message?: string
  token?: string
  role?: string
  user?: UserInfo
}

// 学生信息接口
export interface StudentInfo {
  id: number
  studentId: string
  name: string
  gender: string
  birthDate: string
  className: string
  major: string
  phone: string
  email: string
  address: string
  admissionDate: string
  status: string
}

// 成绩信息接口
export interface GradeInfo {
  id: number
  courseId: number
  courseName: string
  credit: number
  score: number
  gradePoint: number
  semester: string
  academicYear: string
  teacher: string
  examType: string
}

// 课程信息接口
export interface CourseInfo {
  id: number
  courseCode: string
  courseName: string
  credit: number
  hours: number
  teacher: string
  classroom: string
  semester: string
  academicYear: string
  schedule: string
  status: string
  capacity: number
  enrolled: number
}

// API响应通用接口
export interface ApiResponse<T = any> {
  success: boolean
  message?: string
  data?: T
  code?: number
}

// 分页参数接口
export interface PaginationParams {
  page: number
  size: number
  total?: number
}

// 分页响应接口
export interface PaginationResponse<T> {
  list: T[]
  pagination: PaginationParams
}