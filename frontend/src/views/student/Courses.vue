<template>
  <div class="courses-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程信息</span>
          <el-button type="primary" @click="handleImport">
            导入课程表
          </el-button>
        </div>
      </template>

      <!-- 学年学期筛选 -->
      <div class="filter-section">
        <el-radio-group v-model="activeSemester" @change="handleSemesterChange">
          <el-radio-button label="current">本学期</el-radio-button>
          <el-radio-button label="next">下学期</el-radio-button>
          <el-radio-button label="all">全部课程</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 课程列表 -->
      <div class="courses-list">
        <el-row :gutter="20">
          <el-col 
            :span="8" 
            v-for="course in filteredCourses" 
            :key="course.id"
            class="course-item"
          >
            <el-card shadow="hover" class="course-card">
              <div class="course-header">
                <h4>{{ course.name }}</h4>
                <el-tag 
                  :type="getCourseTypeTag(course.type)"
                  size="small"
                >
                  {{ course.type }}
                </el-tag>
              </div>
              
              <div class="course-info">
                <div class="info-item">
                  <el-icon><Collection /></el-icon>
                  <span>课程代码: {{ course.code }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Coin /></el-icon>
                  <span>学分: {{ course.credit }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Clock /></el-icon>
                  <span>学时: {{ course.hours }}</span>
                </div>
                <div class="info-item">
                  <el-icon><User /></el-icon>
                  <span>教师: {{ course.teacher }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Location /></el-icon>
                  <span>教室: {{ course.classroom }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Calendar /></el-icon>
                  <span>时间: {{ course.time }}</span>
                </div>
              </div>

              <div class="course-actions">
                <el-button type="primary" size="small" @click="handleViewDetail(course)">
                  课程详情
                </el-button>
                <el-button type="success" size="small" @click="handleSelectCourse(course)" v-if="course.status === '可选'">
                  选课
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalCourses"
          layout="total, prev, pager, next, jumper"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 课程详情对话框 -->
    <el-dialog v-model="detailVisible" :title="currentCourse.name" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="课程代码">{{ currentCourse.code }}</el-descriptions-item>
        <el-descriptions-item label="课程名称">{{ currentCourse.name }}</el-descriptions-item>
        <el-descriptions-item label="课程类型">{{ currentCourse.type }}</el-descriptions-item>
        <el-descriptions-item label="学分">{{ currentCourse.credit }}</el-descriptions-item>
        <el-descriptions-item label="学时">{{ currentCourse.hours }}</el-descriptions-item>
        <el-descriptions-item label="授课教师">{{ currentCourse.teacher }}</el-descriptions-item>
        <el-descriptions-item label="上课地点">{{ currentCourse.classroom }}</el-descriptions-item>
        <el-descriptions-item label="上课时间">{{ currentCourse.time }}</el-descriptions-item>
        <el-descriptions-item label="课程状态">{{ currentCourse.status }}</el-descriptions-item>
        <el-descriptions-item label="选课人数">{{ currentCourse.enrolled }}/{{ currentCourse.capacity }}</el-descriptions-item>
      </el-descriptions>
      
      <div class="course-description">
        <h4>课程简介</h4>
        <p>{{ currentCourse.description || '暂无课程简介' }}</p>
      </div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleSelectCourse(currentCourse)" v-if="currentCourse.status === '可选'">
          立即选课
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Collection, Coin, Clock, User, Location, Calendar 
} from '@element-plus/icons-vue'
import { courseApi } from '../../api/course'
import { useAuthStore } from '../../stores/auth'

const authStore = useAuthStore()
const activeSemester = ref('current')
const currentPage = ref(1)
const pageSize = ref(9)
const detailVisible = ref(false)
const loading = ref(false)

const currentCourse = ref({})
const coursesData = ref([])
const selectedCourses = ref([])

// 获取当前学年学期
const getCurrentSemester = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1
  // 春季学期: 2-7月, 秋季学期: 8-1月
  const semester = month >= 2 && month <= 7 ? 2 : 1
  return `${year-1}-${year}-${semester}`
}

// 获取学生已选课程
const loadSelectedCourses = async () => {
  try {
    const response = await courseApi.getSelectedCourses(authStore.user.stuCardNo)
    selectedCourses.value = response.data || []
  } catch (error) {
    console.error('获取已选课程失败:', error)
    ElMessage.error('获取已选课程失败')
  }
}

// 获取课程列表
const loadCourses = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    if (activeSemester.value === 'current') {
      params.semester = getCurrentSemester()
    } else if (activeSemester.value === 'next') {
      // 下个学期
      const current = getCurrentSemester()
      const [yearFrom, yearTo, semester] = current.split('-')
      params.semester = semester === '1' ? `${yearTo}-${parseInt(yearTo)+1}-2` : `${parseInt(yearTo)+1}-${parseInt(yearTo)+2}-1`
    }
    
    const response = await courseApi.getCourses(params)
    coursesData.value = response.data.content || []
    
    // 标记已选课程状态
    coursesData.value.forEach(course => {
      const isSelected = selectedCourses.value.some(sc => sc.courseId === course.courseId)
      course.status = isSelected ? '已选' : '可选'
    })
  } catch (error) {
    console.error('获取课程列表失败:', error)
    ElMessage.error('获取课程列表失败')
    
    // 降级使用模拟数据
    coursesData.value = getMockData()
  } finally {
    loading.value = false
  }
}

// 模拟数据
const getMockData = () => {
  return [
    {
      courseId: 1,
      courseCode: 'CS101',
      courseName: '计算机基础',
      credit: 3,
      hours: 48,
      teacherName: '李教授',
      classroom: '教学楼A101',
      schedule: '周一 9:00-11:00',
      enrolled: 45,
      capacity: 50,
      semester: '2023-2024-1',
      description: '本课程主要介绍计算机基础知识，包括计算机组成、操作系统、网络基础等内容。'
    },
    {
      courseId: 2,
      courseCode: 'MA102',
      courseName: '高等数学',
      credit: 4,
      hours: 64,
      teacherName: '王教授',
      classroom: '教学楼B201',
      schedule: '周二 14:00-16:00',
      enrolled: 48,
      capacity: 50,
      semester: '2023-2024-1',
      description: '高等数学是理工科学生的基础课程，涵盖微积分、线性代数等内容。'
    }
  ]
}

const filteredCourses = computed(() => {
  let filtered = coursesData.value
  
  // 按学期筛选
  if (activeSemester.value === 'current') {
    filtered = filtered.filter(course => course.semester === '2023-2')
  } else if (activeSemester.value === 'next') {
    filtered = filtered.filter(course => course.semester === '2024-1')
  }
  
  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  return filtered.slice(start, start + pageSize.value)
})

const totalCourses = computed(() => {
  if (activeSemester.value === 'current') {
    return coursesData.value.filter(course => course.semester === '2023-2').length
  } else if (activeSemester.value === 'next') {
    return coursesData.value.filter(course => course.semester === '2024-1').length
  }
  return coursesData.value.length
})

const getCourseTypeTag = (type) => {
  const typeMap = {
    '必修': 'primary',
    '选修': 'success',
    '限选': 'warning'
  }
  return typeMap[type] || 'info'
}

const handleSemesterChange = () => {
  currentPage.value = 1
}

const handlePageChange = (page) => {
  currentPage.value = page
}

const handleViewDetail = (course) => {
  currentCourse.value = course
  detailVisible.value = true
}

const handleSelectCourse = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要选择《${course.name}》课程吗？`,
      '选课确认',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    // 模拟选课操作
    course.status = '已选'
    course.enrolled += 1
    
    ElMessage.success('选课成功！')
  } catch (error) {
    // 用户取消操作
  }
}

const handleImport = () => {
  ElMessage.info('导入课程表功能开发中...')
}

onMounted(() => {
  // 初始化数据
  console.log('课程信息页面加载完成')
})
</script>

<style scoped>
.courses-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-section {
  margin-bottom: 20px;
  text-align: center;
}

.courses-list {
  margin: 20px 0;
}

.course-item {
  margin-bottom: 20px;
}

.course-card {
  height: 280px;
  display: flex;
  flex-direction: column;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  
  h4 {
    margin: 0;
    color: #333;
    font-size: 16px;
  }
}

.course-info {
  flex: 1;
  
  .info-item {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
    font-size: 14px;
    color: #666;
    
    .el-icon {
      margin-right: 8px;
      color: #409EFF;
    }
  }
}

.course-actions {
  display: flex;
  justify-content: space-between;
  margin-top: auto;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}

.course-description {
  margin-top: 20px;
  
  h4 {
    margin-bottom: 10px;
    color: #333;
  }
  
  p {
    line-height: 1.6;
    color: #666;
  }
}
</style>