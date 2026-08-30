<template>
  <el-dialog
    :model-value="modelValue"
    :title="detail ? `${detail.name} - 学生详情` : '学生详情'"
    width="880px"
    @update:model-value="(v) => emit('update:modelValue', v)"
  >
    <div v-loading="loading">
      <el-descriptions :column="3" border size="small" v-if="detail">
        <el-descriptions-item label="学号">{{ detail.studentId }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detail.gender }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ detail.age }}</el-descriptions-item>
        <el-descriptions-item label="学院">{{ detail.department }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ detail.major }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ detail.clazz }}</el-descriptions-item>
        <el-descriptions-item label="地区">{{ detail.region }}</el-descriptions-item>
        <el-descriptions-item label="评估结果">{{ detail.evaluation || '--' }}</el-descriptions-item>
        <el-descriptions-item label="兴趣爱好" :span="3">{{ detail.hobby || '--' }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="3">{{ detail.address || '--' }}</el-descriptions-item>
      </el-descriptions>

      <div class="section-title">课程与成绩</div>

      <el-table :data="courses" size="small" border>
        <el-table-column prop="courseCode" label="课程代码" width="110" />
        <el-table-column prop="courseName" label="课程名称" min-width="160" />
        <el-table-column prop="credit" label="学分" width="70" align="center" />
        <el-table-column label="学年学期" width="150">
          <template #default="{ row }">{{ row.academicYear }} {{ row.semester }}</template>
        </el-table-column>
        <el-table-column label="成绩" width="150">
          <template #default="{ row }">
            <el-input-number
              v-model="row.__grade"
              :min="0"
              :max="100"
              :precision="1"
              size="small"
              controls-position="right"
            />
          </template>
        </el-table-column>
        <el-table-column label="绩点" width="80" align="center">
          <template #default="{ row }">{{ row.gradePoint == null ? '--' : row.gradePoint }}</template>
        </el-table-column>
        <el-table-column label="操作" width="90" align="center">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :loading="savingId === row.courseId"
              @click="saveGrade(row)"
            >
              保存
            </el-button>
          </template>
        </el-table-column>

        <template #empty>
          <el-empty description="该学生暂无选课记录" />
        </template>
      </el-table>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { teacherApi } from '@/api/auth'

const props = defineProps({
  modelValue: Boolean,
  studentId: {
    type: [String, Number],
    default: null
  }
})

const emit = defineEmits(['update:modelValue', 'saved'])

const loading = ref(false)
const savingId = ref(null)
const detail = ref(null)
const courses = ref([])

const loadDetail = async () => {
  if (!props.studentId) {
    return
  }
  loading.value = true
  try {
    const { data } = await teacherApi.getStudentDetail(props.studentId)
    if (data?.success) {
      detail.value = data.data || null
      courses.value = ((data.data && data.data.courses) || []).map(item => ({
        ...item,
        __grade: item.grade == null ? null : Number(item.grade)
      }))
    } else {
      ElMessage.error(data?.message || '获取学生详情失败')
    }
  } catch (error) {
    ElMessage.error('获取学生详情失败')
  } finally {
    loading.value = false
  }
}

const saveGrade = async (row) => {
  if (row.__grade == null) {
    ElMessage.warning('请先填写成绩')
    return
  }
  savingId.value = row.courseId
  try {
    const { data } = await teacherApi.updateStudentGrade(props.studentId, row.courseId, { grade: row.__grade })
    if (data?.success) {
      ElMessage.success('成绩保存成功')
      emit('saved')
      await loadDetail()
    } else {
      ElMessage.error(data?.message || '成绩保存失败')
    }
  } catch (error) {
    ElMessage.error('成绩保存失败')
  } finally {
    savingId.value = null
  }
}

watch(
  () => [props.modelValue, props.studentId],
  ([visible]) => {
    if (visible) {
      loadDetail()
    }
  },
  { immediate: true }
)
</script>

<style scoped>
.section-title {
  margin: 20px 0 10px;
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}
</style>
