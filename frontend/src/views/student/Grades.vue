<template>
  <div class="grades-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩查询</span>
          <el-button type="primary" @click="handleExport">
            导出成绩单
          </el-button>
        </div>
      </template>

      <!-- 查询条件 -->
      <el-form :model="queryParams" inline>
        <el-form-item label="学年学期">
          <el-select v-model="queryParams.term" placeholder="全部学期" clearable style="width: 220px">
            <el-option
              v-for="term in termOptions"
              :key="term"
              :label="term"
              :value="term"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="课程名称">
          <el-input
            v-model="queryParams.courseName"
            placeholder="请输入课程名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 成绩表格 -->
      <el-table :data="pagedList" style="width: 100%" v-loading="loading">
        <el-table-column prop="courseCode" label="课程代码" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="180" />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column label="学年学期" width="160">
          <template #default="{ row }">{{ row.academicYear }} {{ row.semester }}</template>
        </el-table-column>
        <el-table-column prop="teacherName" label="授课教师" width="120" />
        <el-table-column label="成绩" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getScoreType(row.grade)" effect="dark">
              {{ row.grade == null ? '未录入' : row.grade }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="绩点" width="100" align="center">
          <template #default="{ row }">{{ row.gradePoint == null ? '--' : row.gradePoint }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="text" @click="handleViewDetail(row)">详情</el-button>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="暂无成绩数据" />
        </template>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50]"
          :total="filteredList.length"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </el-card>

    <!-- 成绩详情对话框 -->
    <el-dialog v-model="detailVisible" title="成绩详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="课程名称">{{ currentDetail.courseName }}</el-descriptions-item>
        <el-descriptions-item label="课程代码">{{ currentDetail.courseCode }}</el-descriptions-item>
        <el-descriptions-item label="学分">{{ currentDetail.credit }}</el-descriptions-item>
        <el-descriptions-item label="学时">{{ currentDetail.hours }}</el-descriptions-item>
        <el-descriptions-item label="授课教师">{{ currentDetail.teacherName }}</el-descriptions-item>
        <el-descriptions-item label="学年学期">
          {{ currentDetail.academicYear }} {{ currentDetail.semester }}
        </el-descriptions-item>
        <el-descriptions-item label="成绩">
          {{ currentDetail.grade == null ? '未录入' : currentDetail.grade }}
        </el-descriptions-item>
        <el-descriptions-item label="绩点">
          {{ currentDetail.gradePoint == null ? '--' : currentDetail.gradePoint }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { gradeApi } from '@/api/course'

const authStore = useAuthStore()

const loading = ref(false)
const detailVisible = ref(false)
const gradesList = ref([])

const queryParams = reactive({
  current: 1,
  size: 10,
  term: '',
  courseName: ''
})

const currentDetail = ref({})

const studentKey = computed(() => {
  const info = authStore.userInfo
  if (!info) return null
  return info.userId ?? info.studentId ?? null
})

// 从成绩数据中提取学年学期选项
const termOptions = computed(() => {
  const set = new Set()
  gradesList.value.forEach(item => {
    const term = `${item.academicYear || ''} ${item.semester || ''}`.trim()
    if (term) set.add(term)
  })
  return Array.from(set).sort().reverse()
})

const filteredList = computed(() => {
  const keyword = (queryParams.courseName || '').trim()
  return gradesList.value.filter(item => {
    if (queryParams.term) {
      const term = `${item.academicYear || ''} ${item.semester || ''}`.trim()
      if (term !== queryParams.term) return false
    }
    if (keyword && !(item.courseName || '').includes(keyword)) return false
    return true
  })
})

const pagedList = computed(() => {
  const start = (queryParams.current - 1) * queryParams.size
  return filteredList.value.slice(start, start + queryParams.size)
})

const getScoreType = (score) => {
  if (score == null) return 'info'
  if (score >= 90) return 'success'
  if (score >= 80) return 'primary'
  if (score >= 70) return 'warning'
  if (score >= 60) return 'info'
  return 'danger'
}

const loadData = async () => {
  if (!studentKey.value) {
    ElMessage.warning('未获取到用户信息，请重新登录')
    return
  }
  loading.value = true
  try {
    const { data } = await gradeApi.getGrades(studentKey.value)
    if (data?.success) {
      gradesList.value = data.data || []
    } else {
      ElMessage.error(data?.message || '获取成绩失败')
    }
  } catch (error) {
    ElMessage.error('获取成绩失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.current = 1
}

const handleReset = () => {
  Object.assign(queryParams, {
    current: 1,
    size: 10,
    term: '',
    courseName: ''
  })
}

const handleViewDetail = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

const handleExport = () => {
  ElMessage.info('成绩单导出功能开发中，敬请期待')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.grades-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

:deep(.el-table) {
  margin-top: 20px;
}

:deep(.el-descriptions) {
  margin-top: 10px;
}
</style>
