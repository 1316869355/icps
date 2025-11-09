<template>
  <div class="student-info">
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" @click="handleEdit" v-if="!isEditing">
            编辑信息
          </el-button>
          <el-button-group v-else>
            <el-button type="success" @click="handleSave">保存</el-button>
            <el-button @click="handleCancel">取消</el-button>
          </el-button-group>
        </div>
      </template>

      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentId">
              <el-input v-model="formData.studentId" :disabled="!isEditing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="formData.name" :disabled="!isEditing" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="formData.gender" :disabled="!isEditing">
                <el-radio label="男">男</el-radio>
                <el-radio label="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年龄" prop="age">
              <el-input-number v-model="formData.age" :disabled="!isEditing" :min="16" :max="30" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="formData.major" :disabled="!isEditing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input v-model="formData.className" :disabled="!isEditing" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="联系方式" prop="phone">
          <el-input v-model="formData.phone" :disabled="!isEditing" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email">
          <el-input v-model="formData.email" :disabled="!isEditing" />
        </el-form-item>

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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const formRef = ref()
const isEditing = ref(false)
const originalData = ref({})

const formData = reactive({
  studentId: '2023001001',
  name: '张三',
  gender: '男',
  age: 20,
  major: '计算机科学与技术',
  className: '计算机2001班',
  phone: '13800138000',
  email: 'zhangsan@example.com',
  address: '北京市海淀区中关村大街1号'
})

const formRules = {
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系方式', trigger: 'blur' }]
}

const handleEdit = () => {
  isEditing.value = true
  originalData.value = { ...formData }
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
    
    // 模拟保存操作
    ElMessage.success('信息保存成功')
    isEditing.value = false
  } catch (error) {
    ElMessage.error('请完善表单信息')
  }
}

const handleCancel = () => {
  Object.assign(formData, originalData.value)
  isEditing.value = false
}

onMounted(() => {
  // 模拟从后端获取数据
  console.log('加载学生信息')
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
</style>