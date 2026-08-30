<template>
  <div class="teacher-students">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
          <el-button type="primary" :loading="loading" @click="loadStudents">刷新</el-button>
        </div>
      </template>

      <el-form :model="query" inline class="filter-form" @submit.prevent>
        <el-form-item label="姓名">
          <el-input
            v-model="query.name"
            placeholder="请输入姓名"
            clearable
            style="width: 160px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="学院">
          <el-select v-model="query.dept" placeholder="全部学院" clearable style="width: 170px">
            <el-option v-for="dept in deptOptions" :key="dept" :label="dept" :value="dept" />
          </el-select>
        </el-form-item>
        <el-form-item label="专业">
          <el-input
            v-model="query.major"
            placeholder="请输入专业"
            clearable
            style="width: 190px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="query.sex" placeholder="全部" clearable style="width: 110px">
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <StudentTable :rows="students" :loading="loading" @view="handleView" @grade="handleGrade" />

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="loadStudents"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <StudentDetailDialog v-model="detailVisible" :student-id="currentId" @saved="loadStudents" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { teacherApi } from '@/api/auth'
import StudentTable from './components/StudentTable.vue'
import StudentDetailDialog from './components/StudentDetailDialog.vue'

const loading = ref(false)
const students = ref([])
const total = ref(0)
const deptOptions = ref([])

const detailVisible = ref(false)
const currentId = ref(null)

const query = reactive({
  page: 1,
  size: 10,
  name: '',
  dept: '',
  major: '',
  sex: null
})

const loadStudents = async () => {
  loading.value = true
  try {
    const params = { page: query.page, size: query.size }
    if (query.name && query.name.trim()) params.name = query.name.trim()
    if (query.dept) params.dept = query.dept
    if (query.major && query.major.trim()) params.major = query.major.trim()
    if (query.sex) params.sex = query.sex

    const { data } = await teacherApi.getStudents(params)
    if (data?.success) {
      students.value = data.data || []
      total.value = data.total || 0
    } else {
      ElMessage.error(data?.message || '获取学生列表失败')
    }
  } catch (error) {
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

const loadDeptOptions = async () => {
  try {
    const { data } = await teacherApi.getStatistics()
    const stats = (data?.data?.student?.departmentStats) || {}
    deptOptions.value = Object.keys(stats).filter(Boolean)
  } catch (error) {
    deptOptions.value = []
  }
}

const handleSearch = () => {
  query.page = 1
  loadStudents()
}

const handleReset = () => {
  Object.assign(query, { page: 1, size: 10, name: '', dept: '', major: '', sex: null })
  loadStudents()
}

const handleSizeChange = () => {
  query.page = 1
  loadStudents()
}

const handleView = (row) => {
  currentId.value = row.userId
  detailVisible.value = true
}

const handleGrade = (row) => {
  currentId.value = row.userId
  detailVisible.value = true
}

onMounted(() => {
  loadDeptOptions()
  loadStudents()
})
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-form {
  margin-bottom: 4px;
}

.pagination {
  margin-top: 18px;
  text-align: right;
}
</style>
