<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon primary">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <h3>个人信息</h3>
              <p>查看和编辑个人信息</p>
              <el-button type="primary" @click="goToPage('/student/info')">查看详情</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon success">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <h3>成绩查询</h3>
              <p>查看学习成绩和绩点</p>
              <el-button type="success" @click="goToPage('/student/grades')">查看详情</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon warning">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <h3>课程信息</h3>
              <p>查看课程表和选课</p>
              <el-button type="warning" @click="goToPage('/student/courses')">查看详情</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon info">
              <el-icon><DataBoard /></el-icon>
            </div>
            <div class="stat-info">
              <h3>个性评估</h3>
              <p>查看个性评估结果</p>
              <el-button type="info" @click="handleEvaluation">查看详情</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>欢迎使用 ICPS 系统</span>
            </div>
          </template>
          <div class="welcome-content">
            <p>ICPS (Integrated Campus Personality System) 是一个综合性的学生信息管理系统，为您提供：</p>
            <ul>
              <li>📚 个人信息管理</li>
              <li>📊 成绩查询与分析</li>
              <li>🎓 课程信息与选课</li>
              <li>🧠 个性化评估分析</li>
            </ul>
            <p>请使用左侧菜单或上方卡片快速访问各个功能模块。</p>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>系统公告</span>
            </div>
          </template>
          <div class="announcement-content">
            <el-empty description="暂无系统公告" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Document, Reading, DataBoard } from '@element-plus/icons-vue'

const router = useRouter()

const goToPage = (path) => {
  router.push(path)
}

const handleEvaluation = () => {
  ElMessage.info('个性评估功能正在开发中，敬请期待！')
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px 0;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.stat-icon.primary {
  background: linear-gradient(135deg, #409EFF 0%, #337ecc 100%);
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

.stat-info {
  flex: 1;
}

.stat-info h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #303133;
}

.stat-info p {
  margin: 0 0 10px 0;
  color: #606266;
  font-size: 14px;
}

.card-header {
  font-weight: 600;
  color: #303133;
}

.welcome-content, .announcement-content {
  color: #606266;
  line-height: 1.6;
}

.welcome-content ul {
  margin: 15px 0;
  padding-left: 20px;
}

.welcome-content li {
  margin-bottom: 8px;
}
</style>