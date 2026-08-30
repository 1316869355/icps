<template>
  <div class="courses-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>课程信息</span>
          <el-button type="primary" @click="loadData">刷新</el-button>
        </div>
      </template>

      <!-- 筛选区 -->
      <div class="filter-section">
        <el-radio-group v-model="activeTab" @change="handleTabChange">
          <el-radio-button label="all">全部课程</el-radio-button>
          <el-radio-button label="selected">我已选课程</el-radio-button>
        </el-radio-group>

        <el-select
          v-model="activeTerm"
          placeholder="全部学年学期"
          clearable
          style="width: 200px; margin-left: 12px"
          @change="handlePageChange(1)"
        >
          <el-option v-for="term in termOptions" :key="term" :label="term" :value="term" />
        </el-select>
      </div>

      <!-- 课程列表 -->
      <div class="courses-list" v-loading="loading">
        <el-row :gutter="20">
          <el-col
            :span="8"
            v-for="course in pagedCourses"
            :key="course.courseId"
            class="course-item"
          >
            <el-card shadow="hover" class="course-card">
              <div class="course-header">
                <h4>{{ course.name }}</h4>
                <el-tag :type="course.selected ? 'success' : 'primary'" size="small">
                  {{ course.selected ? '已选' : '可选' }}
                </el-tag>
              </div>

              <div class="course-info">
                <div class="info-item">
                  <el-icon><Collection /></el-icon>
                  <span>课程代码: {{ course.code }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Coin /></el-icon>
                  <span>学分: {{ course.credit }} / 学时: {{ course.hours }}</span>
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
                  <el-icon><Clock /></el-icon>
                  <span>时间: {{ course.time }}</span>
                </div>
                <div class="info-item">
                  <el-icon><Calendar /></el-icon>
                  <span>学期: {{ course.academicYear }} {{ course.semester }}</span>
                </div>
              </div>

              <div class="course-actions">
                <el-button size="small" @click="handleViewDetail(course)">课程详情</el-button>
                <el-button
                  v-if="course.selected"
                  type="danger"
                  size="small"
                  @click="handleDropCourse(course)"
                >
                  退课
                </el-button>
                <el-button
                  v-else
                  type="success"
                  size="small"
                  :disabled="course.enrolled >= course.capacity"
                  @click="handleSelectCourse(course)"
                >
                  {{ course.enrolled >= course.capacity ? '已满' : '选课' }}
                </el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-empty v-if="!loading && pagedCourses.length === 0" description="暂无课程数据" />
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="filteredCourses.length"
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
        <el-descriptions-item label="学分">{{ currentCourse.credit }}</el-descriptions-item>
        <el-descriptions-item label="学时">{{ currentCourse.hours }}</el-descriptions-item>
        <el-descriptions-item label="授课教师">{{ currentCourse.teacher }}</el-descriptions-item>
        <el-descriptions-item label="上课地点">{{ currentCourse.classroom }}</el-descriptions-item>
        <el-descriptions-item label="上课时间">{{ currentCourse.time }}</el-descriptions-item>
        <el-descriptions-item label="学年学期">
          {{ currentCourse.academicYear }} {{ currentCourse.semester }}
        </el-descriptions-item>
        <el-descriptions-item label="选课人数">
          {{ currentCourse.enrolled }}/{{ currentCourse.capacity }}
        </el-descriptions-item>
        <el-descriptions-item label="选课状态">
          {{ currentCourse.selected ? '已选' : '未选' }}
        </el-descriptions-item>
        <el-descriptions-item label="成绩">
          {{ currentCourse.grade == null ? '未录入' : currentCourse.grade }}
        </el-descriptions-item>
        <el-descriptions-item label="绩点">
          {{ currentCourse.gradePoint == null ? '--' : currentCourse.gradePoint }}
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="success" v-if="!currentCourse.selected" @click="handleSelectCourse(currentCourse)">
          立即选课
        </el-button>
        <el-button type="danger" v-else @click="handleDropCourse(currentCourse)">退课</el-button>
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
import { courseApi } from '@/api/course'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

const activeTab = ref('all')
const activeTerm = ref('')
const currentPage = ref(1)
const pageSize = ref(9)
const detailVisible = ref(false)
const loading = ref(false)

const currentCourse = ref({})
const allCourses = ref([])
const selectedCourses = ref([])

const studentKey = computed(() => {
  const info = authStore.userInfo
  if (!info) return null
  return info.userId ?? info.studentId ?? null
})

// 统一的课程视图模型
const courses = computed(() => {
  const selectedIds = new Set(selectedCourses.value.map(item => Number(item.courseId)))

  if (activeTab.value === 'selected') {
    return selectedCourses.value.map(item => ({
      courseId: item.courseId,
      code: item.courseCode,
      name: item.courseName,
      credit: item.credit,
      hours: item.hours,
      teacher: item.teacherName,
      classroom: item.classroom,
      time: item.schedule,
      semester: item.semester,
      academicYear: item.academicYear,
      enrolled: item.enrolled,
      capacity: item.capacity,
      grade: item.grade,
      gradePoint: item.gradePoint,
      selected: true
    }))
  }

  return allCourses.value.map(item => ({
    courseId: item.id,
    code: item.code,
    name: item.name,
    credit: item.credit,
    hours: item.hours,
    teacher: item.teacherName,
    classroom: item.classroom,
    time: item.schedule,
    semester: item.semester,
    academicYear: item.academicYear,
    enrolled: item.enrolled,
    capacity: item.capacity,
    grade: selectedCourses.value.find(sc => Number(sc.courseId) === Number(item.id))?.grade,
    gradePoint: selectedCourses.value.find(sc => Number(sc.courseId) === Number(item.id))?.gradePoint,
    selected: selectedIds.has(Number(item.id))
  }))
})

const termOptions = computed(() => {
  const set = new Set()
  courses.value.forEach(course => {
    const term = `${course.academicYear || ''} ${course.semester || ''}`.trim()
    if (term) set.add(term)
  })
  return Array.from(set).sort().reverse()
})

const filteredCourses = computed(() => {
  if (!activeTerm.value) return courses.value
  return courses.value.filter(course => {
    const term = `${course.academicYear || ''} ${course.semester || ''}`.trim()
    return term === activeTerm.value
  })
})

const pagedCourses = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredCourses.value.slice(start, start + pageSize.value)
})

const loadSelectedCourses = async () => {
  if (!studentKey.value) return
  try {
    const { data } = await courseApi.getSelectedCourses(studentKey.value)
    selectedCourses.value = data?.success ? (data.data || []) : []
  } catch (error) {
    selectedCourses.value = []
  }
}

const loadCourses = async () => {
  loading.value = true
  try {
    // 课程量级较小，一次性取回后在前端做筛选与分页
    const { data } = await courseApi.getCourses({ page: 1, size: 100 })
    allCourses.value = data?.success ? (data.data || []) : []
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const loadData = async () => {
  await loadSelectedCourses()
  if (activeTab.value === 'all') {
    await loadCourses()
  }
}

const handleTabChange = () => {
  currentPage.value = 1
  activeTerm.value = ''
  loadData()
}

const handlePageChange = (page) => {
  currentPage.value = page || 1
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
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
  } catch (error) {
    return
  }

  try {
    const { data } = await courseApi.selectCourse(studentKey.value, course.courseId)
    if (data?.success) {
      ElMessage.success('选课成功')
      detailVisible.value = false
      await loadData()
    } else {
      ElMessage.error(data?.message || '选课失败')
    }
  } catch (error) {
    ElMessage.error('选课失败')
  }
}

const handleDropCourse = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定要退选《${course.name}》课程吗？`,
      '退课确认',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
  } catch (error) {
    return
  }

  try {
    const { data } = await courseApi.dropCourse(studentKey.value, course.courseId)
    if (data?.success) {
      ElMessage.success('退课成功')
      detailVisible.value = false
      await loadData()
    } else {
      ElMessage.error(data?.message || '退课失败')
    }
  } catch (error) {
    ElMessage.error('退课失败')
  }
}

onMounted(() => {
  loadData()
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
  display: flex;
  align-items: center;
}

.courses-list {
  margin: 20px 0;
}

.course-item {
  margin-bottom: 20px;
}

.course-card {
  height: 300px;
  display: flex;
  flex-direction: column;
}

.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;

  :is(h4) {
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
      color: #409eff;
    }
  }
}

.course-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: auto;
}

.pagination {
  margin-top: 20px;
  text-align: center;
}
</style>
