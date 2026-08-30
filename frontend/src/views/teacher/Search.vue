<template>
  <div class="teacher-search">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生搜索</span>
          <el-button :disabled="!hasSearched" @click="handleClear">清空结果</el-button>
        </div>
      </template>

      <div class="search-box">
        <el-input
          v-model="keyword"
          placeholder="输入学号、姓名、班级或专业关键词"
          size="large"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button type="primary" :loading="loading" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>

      <el-alert
        v-if="hasSearched"
        class="hit-bar"
        :title="`共命中 ${total} 条记录`"
        type="info"
        :closable="false"
        show-icon
      />

      <div class="result-area">
        <StudentTable
          v-if="hasSearched"
          :rows="students"
          :loading="loading"
          @view="handleView"
          @grade="handleGrade"
        />
        <el-empty v-else description="输入关键词后开始搜索学生" />
      </div>

      <div class="pagination" v-if="hasSearched && total > query.size">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="handleSearch"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <StudentDetailDialog v-model="detailVisible" :student-id="currentId" @saved="handleSearch" />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { teacherApi } from '@/api/auth'
import StudentTable from './components/StudentTable.vue'
import StudentDetailDialog from './components/StudentDetailDialog.vue'

const loading = ref(false)
const keyword = ref('')
const students = ref([])
const total = ref(0)
const hasSearched = ref(false)

const detailVisible = ref(false)
const currentId = ref(null)

const query = reactive({ page: 1, size: 10 })

const handleSearch = async () => {
  const kw = (keyword.value || '').trim()
  if (!kw) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  loading.value = true
  hasSearched.value = true
  try {
    const { data } = await teacherApi.searchStudents(kw, { page: query.page, size: query.size })
    if (data?.success) {
      students.value = data.data || []
      total.value = data.total || 0
      if (total.value === 0) {
        ElMessage.info('未找到匹配的学生')
      }
    } else {
      ElMessage.error(data?.message || '搜索失败')
    }
  } catch (error) {
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

const handleSizeChange = () => {
  query.page = 1
  handleSearch()
}

const handleClear = () => {
  keyword.value = ''
  students.value = []
  total.value = 0
  hasSearched.value = false
  query.page = 1
}

const handleView = (row) => {
  currentId.value = row.userId
  detailVisible.value = true
}

const handleGrade = (row) => {
  currentId.value = row.userId
  detailVisible.value = true
}
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-box {
  max-width: 620px;
  margin: 0 auto;
}

.hit-bar {
  margin-top: 18px;
}

.result-area {
  margin-top: 16px;
}

.pagination {
  margin-top: 18px;
  text-align: right;
}
</style>
