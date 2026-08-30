<template>
  <el-table
    :data="rows"
    v-loading="loading"
    stripe
    border
    style="width: 100%"
    @row-click="(row) => emit('view', row)"
  >
    <el-table-column prop="studentId" label="学号" width="120" />
    <el-table-column prop="name" label="姓名" width="100" />
    <el-table-column prop="gender" label="性别" width="70" align="center" />
    <el-table-column prop="age" label="年龄" width="70" align="center" />
    <el-table-column prop="department" label="学院" min-width="140" />
    <el-table-column prop="major" label="专业" min-width="170" />
    <el-table-column prop="clazz" label="班级" width="120" />
    <el-table-column prop="region" label="地区" width="100" />
    <el-table-column label="操作" width="150" align="center" fixed="right">
      <template #default="{ row }">
        <el-button type="text" @click.stop="emit('view', row)">查看详情</el-button>
        <el-button type="text" @click.stop="emit('grade', row)">录入成绩</el-button>
      </template>
    </el-table-column>

    <template #empty>
      <el-empty description="暂无学生数据" />
    </template>
  </el-table>
</template>

<script setup>
defineProps({
  rows: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['view', 'grade'])
</script>
