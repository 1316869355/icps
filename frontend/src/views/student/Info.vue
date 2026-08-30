<template>
  <div class="student-info">
    <el-card class="info-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" @click="handleEdit" v-if="!isEditing">
            编辑信息
          </el-button>
          <el-button-group v-else>
            <el-button type="success" @click="handleSave" :loading="saving">保存</el-button>
            <el-button @click="handleCancel">取消</el-button>
          </el-button-group>
        </div>
      </template>

      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentId">
              <el-input v-model="formData.studentId" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" :disabled="isFieldLocked('name')" />
              <div v-if="isStudent && isEditing" class="lock-tip">仅管理员可修改</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="formData.gender" :disabled="isFieldLocked('gender')">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
              <div v-if="isStudent && isEditing" class="lock-tip">仅管理员可修改</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="formData.age" :disabled="isFieldLocked('age')" :min="16" :max="60" />
              <div v-if="isStudent && isEditing" class="lock-tip">仅管理员可修改</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学院" prop="department">
              <el-input v-model="formData.department" :disabled="isFieldLocked('department')" />
              <div v-if="isStudent && isEditing" class="lock-tip">仅管理员可修改</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="formData.major" :disabled="isFieldLocked('major')" />
              <div v-if="isStudent && isEditing" class="lock-tip">仅管理员可修改</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级" prop="clazz">
              <el-input v-model="formData.clazz" :disabled="isFieldLocked('clazz')" />
              <div v-if="isStudent && isEditing" class="lock-tip">仅管理员可修改</div>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="地区" prop="region">
              <el-input v-model="formData.region" :disabled="!isEditing" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="兴趣爱好" prop="hobby">
              <el-input v-model="formData.hobby" :disabled="!isEditing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="血型" prop="bloodType">
              <el-input v-model="formData.bloodType" :disabled="!isEditing" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="星座" prop="zodiac">
              <el-input v-model="formData.zodiac" :disabled="!isEditing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="评估结果" prop="evaluation">
              <el-input v-model="formData.evaluation" disabled />
              <div v-if="isStudent && isEditing" class="lock-tip">仅管理员可修改</div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="地址" prop="address">
          <el-input
            v-model="formData.address"
            type="textarea"
            :rows="3"
            :disabled="!isEditing"
          />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { studentApi } from '@/api/auth'

const authStore = useAuthStore()

const formRef = ref()
const isEditing = ref(false)
const loading = ref(false)
const saving = ref(false)
const originalData = ref({})

const formData = reactive({
  studentId: '',
  name: '',
  gender: '男',
  age: null,
  department: '',
  major: '',
  clazz: '',
  region: '',
  hobby: '',
  bloodType: '',
  zodiac: '',
  evaluation: '',
  address: ''
})

const formRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }]
}

// 当前登录用户的 userId（登录时后端返回）
const studentKey = computed(() => {
  const info = authStore.userInfo
  if (!info) return null
  return info.userId ?? info.studentId ?? null
})

// 当前登录角色是否为学生
const isStudent = computed(() => authStore.userRole === 'student')

// 学生在编辑态下被锁定的字段（后端白名单只允许学生修改 5 个字段，
// 其余 7 个字段后端会静默丢弃，前端须禁用编辑以避免误导）
const STUDENT_LOCKED_FIELDS = ['name', 'gender', 'age', 'department', 'major', 'clazz', 'evaluation']

// 字段是否被锁定：学生角色 + 编辑态时，锁定白名单外的字段
const isFieldLocked = (field) => {
  if (!isEditing.value) return true
  if (isStudent.value && STUDENT_LOCKED_FIELDS.includes(field)) return true
  return false
}

const loadStudent = async () => {
  if (!studentKey.value) {
    ElMessage.warning('未获取到用户信息，请重新登录')
    return
  }
  loading.value = true
  try {
    const { data } = await studentApi.getStudentInfo(studentKey.value)
    if (!data?.success) {
      ElMessage.error(data?.message || '获取学生信息失败')
      return
    }
    const student = data.data || {}
    Object.assign(formData, {
      studentId: student.studentId || '',
      name: student.name || '',
      gender: student.gender || '男',
      age: student.age ?? null,
      department: student.department || '',
      major: student.major || '',
      clazz: student.clazz || '',
      region: student.region || '',
      hobby: student.hobby || '',
      bloodType: student.bloodType || '',
      zodiac: student.zodiac || '',
      evaluation: student.evaluation || '',
      address: student.address || ''
    })
    originalData.value = { ...formData }
  } catch (error) {
    ElMessage.error('获取学生信息失败')
  } finally {
    loading.value = false
  }
}

const handleEdit = () => {
  isEditing.value = true
  originalData.value = { ...formData }
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
  } catch (error) {
    ElMessage.error('请完善表单信息')
    return
  }

  saving.value = true
  try {
    // 前端驼峰字段 -> 后端实体字段
    const payload = {
      sname: formData.name,
      ssex: formData.gender === '男' ? 1 : 2,
      sage: formData.age,
      stuDept: formData.department,
      stuMajor: formData.major,
      stuClazz: formData.clazz,
      region: formData.region,
      shbt: formData.hobby,
      bloodType: formData.bloodType,
      zodiac: formData.zodiac,
      evaluation: formData.evaluation,
      stuAddress: formData.address
    }

    const { data } = await studentApi.updateStudentInfo(studentKey.value, payload)
    if (data?.success) {
      ElMessage.success('信息保存成功')
      isEditing.value = false
      authStore.updateUserInfo({
        name: formData.name,
        gender: formData.gender,
        age: formData.age,
        department: formData.department,
        major: formData.major,
        clazz: formData.clazz,
        address: formData.address
      })
    } else {
      ElMessage.error(data?.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

const handleCancel = () => {
  Object.assign(formData, originalData.value)
  isEditing.value = false
}

onMounted(() => {
  loadStudent()
})
</script>

<style scoped>
.student-info {
  padding: 0;
}

.info-card {
  max-width: 800px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.el-form-item__label) {
  font-weight: 500;
}

:deep(.el-input) {
  width: 100%;
}

:deep(.el-textarea) {
  width: 100%;
}

.lock-tip {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
  margin-top: 4px;
}
</style>
