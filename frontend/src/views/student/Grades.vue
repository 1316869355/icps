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
          <el-select v-model="queryParams.semester" placeholder="请选择学期" @change="handleSearch">
            <el-option label="全部学期" value="" />
            <el-option label="2023-2024学年第一学期" value="2023-1" />
            <el-option label="2023-2024学年第二学期" value="2023-2" />
            <el-option label="2022-2023学年第一学期" value="2022-1" />
            <el-option label="2022-2023学年第二学期" value="2022-2" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="课程名称">
          <el-input 
            v-model="queryParams.courseName" 
            placeholder="请输入课程名称"
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
      <el-table :data="gradesList" style="width: 100%" v-loading="loading">
        <el-table-column prop="courseCode" label="课程代码" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="180" />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column prop="semester" label="学期" width="150" />
        <el-table-column prop="score" label="成绩" width="100" align="center">
          <template #default="{ row }">
            <el-tag 
              :type="getScoreType(row.score)"
              effect="dark"
            >
              {{ row.score }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="gpa" label="绩点" width="100" align="center" />
        <el-table-column prop="examDate" label="考试时间" width="120" />
        <el-table-column label="操作" width="120" align="center">
          <template #default="{ row }">
            <el-button type="text" @click="handleViewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 成绩详情对话框 -->
    <el-dialog v-model="detailVisible" title="成绩详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="课程名称">{{ currentDetail.courseName }}</el-descriptions-item>
        <el-descriptions-item label="课程代码">{{ currentDetail.courseCode }}</el-descriptions-item>
        <el-descriptions-item label="学分">{{ currentDetail.credit }}</el-descriptions-item>
        <el-descriptions-item label="学期">{{ currentDetail.semester }}</el-descriptions-item>
        <el-descriptions-item label="考试成绩">{{ currentDetail.score }}</el-descriptions-item>
        <el-descriptions-item label="绩点">{{ currentDetail.gpa }}</el-descriptions-item>
        <el-descriptions-item label="考试时间">{{ currentDetail.examDate }}</el-descriptions-item>
        <el-descriptions-item label="平时成绩">{{ currentDetail.usualScore || '--' }}</el-descriptions-item>
        <el-descriptions-item label="期末成绩">{{ currentDetail.finalScore || '--' }}</el-descriptions-item>
        <el-descriptions-item label="教师评语" :span="2">
          {{ currentDetail.comment || '暂无评语' }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const detailVisible = ref(false)
const total = ref(0)

const queryParams = reactive({
  current: 1,
  size: 10,
  semester: '',
  courseName: ''
})

const currentDetail = ref({})
const gradesList = ref([
  {
    courseCode: 'CS101',
    courseName: '计算机基础',
    credit: 3,
    semester: '2023-2024学年第一学期',
    score: 85,
    gpa: 3.5,
    examDate: '2024-01-15',
    usualScore: 90,
    finalScore: 80,
    comment: '表现良好，积极参与课堂讨论'
  },
  {
    courseCode: 'MA102',
    courseName: '高等数学',
    credit: 4,
    semester: '2023-2024学年第一学期',
    score: 92,
    gpa: 4.0,
    examDate: '2024-01-20',
    usualScore: 95,
    finalScore: 90,
    comment: '数学基础扎实，解题思路清晰'
  },
  {
    courseCode: 'EN201',
    courseName: '大学英语',
    credit: 2,
    semester: '2023-2024学年第二学期',
    score: 78,
    gpa: 2.8,
    examDate: '2024-06-10',
    usualScore: 80,
    finalScore: 76,
    comment: '英语口语需要加强练习'
  }
])

const getScoreType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'primary'
  if (score >= 70) return 'warning'
  if (score >= 60) return 'info'
  return 'danger'
}

const handleSearch = () => {
  queryParams.current = 1
  loadData()
}

const handleReset = () => {
  Object.assign(queryParams, {
    current: 1,
    size: 10,
    semester: '',
    courseName: ''
  })
  loadData()
}

const handleSizeChange = (size) => {
  queryParams.size = size
  loadData()
}

const handleCurrentChange = (current) => {
  queryParams.current = current
  loadData()
}

const handleViewDetail = (row) => {
  currentDetail.value = row
  detailVisible.value = true
}

const handleExport = () => {
  ElMessage.success('成绩单导出成功')
}

const loadData = async () => {
  loading.value = true
  try {
    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 500))
    total.value = gradesList.value.length
  } finally {
    loading.value = false
  }
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