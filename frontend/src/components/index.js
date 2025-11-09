// 全局组件注册

import { ElButton, ElInput, ElForm, ElTable, ElMessage } from 'element-plus'

// 全局组件配置
export const globalComponents = {
  // Element Plus 组件
  ElButton,
  ElInput,
  ElForm,
  ElTable,
  // 可以继续添加更多组件
}

// 全局插件配置
export const globalPlugins = {
  ElMessage,
}

// 安装函数
export const installComponents = (app) => {
  // 注册全局组件
  Object.entries(globalComponents).forEach(([name, component]) => {
    app.component(name, component)
  })
  
  // 注册全局插件
  Object.entries(globalPlugins).forEach(([name, plugin]) => {
    app.config.globalProperties[`$${name}`] = plugin
  })
}

// 默认导出安装函数
export default installComponents