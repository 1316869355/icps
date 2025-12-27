// 用户相关类型定义

// 角色常量
export const UserRole = {
  STUDENT: 'student',
  TEACHER: 'teacher'
}

// 用户信息数据结构
/*
{
  id: number,
  username: string,
  name: string,
  role: string,
  avatar?: string,
  email?: string,
  phone?: string
}
*/

// 登录表单数据结构
/*
{
  username: string,
  password: string,
  role: string
}
*/

// 登录响应数据结构
/*
{
  success: boolean,
  message?: string,
  token?: string,
  role?: string,
  user?: UserInfo
}
*/

// 学生信息数据结构
/*
{
  id: number,
  studentId: string,
  name: string,
  gender: string,
  birthDate: string,
  className: string,
  major: string,
  phone: string,
  email: string,
  address: string,
  admissionDate: string,
  status: string
}
*/

// 成绩信息数据结构
/*
{
  id: number,
  courseId: number,
  courseName: string,
  credit: number,
  score: number,
  gradePoint: number,
  semester: string,
  academicYear: string,
  teacher: string,
  examType: string
}
*/

// 课程信息数据结构
/*
{
  id: number,
  courseCode: string,
  courseName: string,
  credit: number,
  hours: number,
  teacher: string,
  classroom: string,
  semester: string,
  academicYear: string,
  schedule: string,
  status: string,
  capacity: number,
  enrolled: number
}
*/

// API响应通用数据结构
/*
{
  success: boolean,
  message?: string,
  data?: any,
  code?: number
}
*/

// 分页参数数据结构
/*
{
  page: number,
  size: number,
  total?: number
}
*/

// 分页响应数据结构
/*
{
  list: Array<any>,
  pagination: PaginationParams
}
*/