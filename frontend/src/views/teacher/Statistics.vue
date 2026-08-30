<template>
  <div class="teacher-statistics">
    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>数据统计</span>
          <el-button type="primary" :loading="loading" @click="loadStatistics">刷新</el-button>
        </div>
      </template>

      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon primary"><el-icon><User /></el-icon></div>
              <div class="stat-info">
                <p class="stat-label">学生总数</p>
                <h3 class="stat-value">{{ toNumber(overview.students) }}</h3>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon success"><el-icon><Reading /></el-icon></div>
              <div class="stat-info">
                <p class="stat-label">课程总数</p>
                <h3 class="stat-value">{{ toNumber(overview.courses) }}</h3>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon warning"><el-icon><Tickets /></el-icon></div>
              <div class="stat-info">
                <p class="stat-label">选课总人次</p>
                <h3 class="stat-value">{{ toNumber(overview.selections) }}</h3>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card" shadow="hover">
            <div class="stat-content">
              <div class="stat-icon info"><el-icon><DataLine /></el-icon></div>
              <div class="stat-info">
                <p class="stat-label">平均绩点</p>
                <h3 class="stat-value">{{ formatScore(overview.avgGradePoint) }}</h3>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="section-row">
        <el-col :span="12">
          <div class="section-title">学院学生分布</div>
          <div v-if="departmentRows.length" class="progress-list">
            <div v-for="row in departmentRows" :key="row.name" class="progress-item">
              <span class="progress-label">{{ row.name }}</span>
              <el-progress
                class="progress-bar"
                :percentage="row.percent"
                :stroke-width="14"
                :text-inside="true"
              />
              <span class="progress-value">{{ row.value }} 人</span>
            </div>
          </div>
          <el-empty v-else description="暂无学院分布数据" />
        </el-col>

        <el-col :span="12">
          <div class="section-title">学期课程分布</div>
          <el-table v-if="semesterRows.length" :data="semesterRows" size="small" border>
            <el-table-column prop="name" label="学期" min-width="140" />
            <el-table-column prop="value" label="课程数" width="90" align="center" />
            <el-table-column label="占比" min-width="180">
              <template #default="{ row }">
                <el-progress :percentage="row.percent" :stroke-width="12" />
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无学期课程数据" />
        </el-col>
      </el-row>

      <div class="section-title">数据明细</div>
      <el-table :data="detailRows" size="small" border>
        <el-table-column prop="name" label="指标" min-width="180" />
        <el-table-column prop="value" label="数值" width="140" align="center" />
        <el-table-column prop="remark" label="说明" min-width="220" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Reading, Tickets, DataLine } from '@element-plus/icons-vue'
import { teacherApi } from '@/api/auth'

const loading = ref(false)
const raw = reactive({
  student: {},
  course: {},
  selection: {},
  grade: {}
})

const toNumber = (value) => (value == null ? '--' : value)

const formatScore = (value) => {
  if (value == null) return '--'
  return Number(value).toFixed(2)
}

const toPercent = (value, total) => {
  if (!value || !total) return 0
  return Math.round((Number(value) / Number(total)) * 100)
}

const toCountMap = (stats) => {
  if (!stats || typeof stats !== 'object') return {}
  return stats
}

const overview = computed(() => ({
  students: raw.student.totalStudents,
  courses: raw.course.totalCourses,
  selections: raw.grade.record_count != null ? raw.grade.record_count : raw.selection.totalRecords,
  avgGradePoint: raw.grade.avg_grade_point
}))

const departmentRows = computed(() => {
  const stats = toCountMap(raw.student.departmentStats)
  const total = raw.student.totalStudents || 0
  return Object.keys(stats)
    .filter(name => name && name !== 'null')
    .map(name => ({
      name,
      value: stats[name],
      percent: toPercent(stats[name], total)
    }))
    .sort((a, b) => b.value - a.value)
})

const semesterRows = computed(() => {
  const stats = toCountMap(raw.course.semesterStats)
  const total = raw.course.totalCourses || 0
  return Object.keys(stats)
    .filter(name => name && name !== 'null')
    .map(name => ({
      name,
      value: stats[name],
      percent: toPercent(stats[name], total)
    }))
    .sort((a, b) => String(a.name).localeCompare(String(b.name)))
})

const detailRows = computed(() => [
  { name: '学生总数', value: toNumber(raw.student.totalStudents), remark: '逻辑删除后的在册学生数' },
  { name: '学院数量', value: departmentRows.value.length, remark: '有在读学生的学院数' },
  { name: '课程总数', value: toNumber(raw.course.totalCourses), remark: '逻辑删除后的在开课程数' },
  { name: '开设学期数', value: semesterRows.value.length, remark: '有课程安排的学期数' },
  { name: '选课总人次', value: toNumber(overview.value.selections), remark: '全部有效选课记录数' },
  { name: '已录入成绩人次', value: toNumber(raw.grade.graded_count), remark: '已有分数值的选课记录数' },
  { name: '平均成绩', value: formatScore(raw.grade.avg_grade), remark: '已录入成绩的平均分' },
  { name: '平均绩点', value: formatScore(raw.grade.avg_grade_point), remark: '已录入记录的平均绩点' }
])

const loadStatistics = async () => {
  loading.value = true
  try {
    const { data } = await teacherApi.getStatistics()
    if (data?.success) {
      raw.student = data.data?.student || {}
      raw.course = data.data?.course || {}
      raw.selection = data.data?.selection || {}
      raw.grade = data.data?.grade || {}
    } else {
      ElMessage.error(data?.message || '获取统计信息失败')
    }
  } catch (error) {
    ElMessage.error('获取统计信息失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-card {
  margin-bottom: 4px;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 6px 0;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 14px;
  font-size: 24px;
  color: #fff;
}

.stat-icon.primary {
  background: linear-gradient(135deg, #409eff 0%, #337ecc 100%);
}

.stat-icon.success {
  background: linear-gradient(135deg, #67c23a 0%, #529b2e 100%);
}

.stat-icon.warning {
  background: linear-gradient(135deg, #e6a23c 0%, #b88230 100%);
}

.stat-icon.info {
  background: linear-gradient(135deg, #909399 0%, #73767a 100%);
}

.stat-label {
  margin: 0 0 4px;
  color: #606266;
  font-size: 14px;
}

.stat-value {
  margin: 0;
  color: #303133;
  font-size: 22px;
  font-weight: 600;
}

.section-row {
  margin-top: 24px;
}

.section-title {
  margin: 18px 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.progress-list {
  padding-right: 8px;
}

.progress-item {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
}

.progress-label {
  width: 90px;
  color: #606266;
  font-size: 14px;
}

.progress-bar {
  flex: 1;
}

.progress-value {
  width: 66px;
  text-align: right;
  color: #303133;
  font-size: 14px;
}
</style>
