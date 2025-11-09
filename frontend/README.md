# ICPS 前端项目

学生信息管理系统前端项目，基于 Vue 3 + Element Plus 构建。

## 技术栈

- **框架**: Vue 3.3.4
- **构建工具**: Vite 4.4.5
- **UI组件库**: Element Plus 2.3.8
- **路由**: Vue Router 4.2.4
- **状态管理**: Pinia 2.1.4
- **HTTP客户端**: Axios 1.4.0

## 项目结构

```
frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口
│   │   └── request.js     # Axios配置
│   ├── assets/            # 静态资源
│   ├── components/        # 公共组件
│   ├── router/            # 路由配置
│   │   └── index.js
│   ├── stores/            # 状态管理
│   │   └── auth.js       # 认证状态
│   ├── styles/            # 样式文件
│   │   └── index.css     # 全局样式
│   ├── views/             # 页面组件
│   │   ├── Login.vue     # 登录页面
│   │   ├── Student.vue   # 学生主页面
│   │   └── student/      # 学生子页面
│   │       ├── Info.vue    # 个人信息
│   │       ├── Grades.vue  # 成绩查询
│   │       └── Courses.vue # 课程信息
│   ├── App.vue            # 根组件
│   └── main.js            # 入口文件
├── index.html             # HTML模板
├── package.json           # 依赖配置
├── vite.config.js         # Vite配置
└── README.md              # 项目说明
```

## 功能特性

### 学生功能
- ✅ 用户登录/登出
- ✅ 个人信息管理
- ✅ 成绩查询
- ✅ 课程信息查看
- ✅ 课程选课功能

### 设计特点
- 🎨 响应式设计，支持移动端
- 🚀 现代化UI，基于Element Plus
- 📱 单页面应用(SPA)
- 🔒 路由守卫，权限控制
- 🎯 组件化开发

## 安装与运行

### 环境要求
- Node.js >= 16.0.0
- npm >= 8.0.0

### 安装依赖
```bash
cd frontend
npm install
```

### 开发模式
```bash
npm run dev
```
项目将在 http://localhost:3000 启动

### 生产构建
```bash
npm run build
```
构建产物位于 `dist/` 目录

### 预览构建结果
```bash
npm run preview
```

## 配置说明

### 环境变量
项目支持环境变量配置，可在根目录创建 `.env` 文件：

```env
# API基础地址
VITE_API_BASE_URL=http://localhost:8081/api

# 应用标题
VITE_APP_TITLE=ICPS学生信息管理系统
```

### Vite配置
Vite配置文件位于 `vite.config.js`，主要配置包括：

- **服务器配置**: 开发服务器设置
- **代理配置**: API请求代理
- **构建配置**: 生产构建优化
- **插件配置**: Vue、Element Plus插件

## 开发指南

### 添加新页面
1. 在 `src/views/` 目录创建页面组件
2. 在 `src/router/index.js` 中添加路由配置
3. 如有需要，在对应模块目录创建子页面

### API接口开发
1. 在 `src/api/` 目录创建API模块
2. 使用 `request.js` 中的Axios实例
3. 在页面组件中调用API方法

### 状态管理
- 使用Pinia进行状态管理
- 在 `src/stores/` 目录创建store
- 在组件中使用 `useStore()` 获取状态

### 样式开发
- 全局样式位于 `src/styles/index.css`
- 组件样式使用 `<style scoped>`
- 支持CSS变量和Element Plus主题

## 部署说明

### 静态部署
构建后可直接部署到任何静态服务器：

```bash
npm run build
# 将 dist/ 目录内容上传到服务器
```

### Docker部署
项目支持Docker容器化部署：

```dockerfile
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html/
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

## 浏览器支持

- Chrome >= 88
- Firefox >= 78
- Safari >= 14
- Edge >= 88

## 相关链接

- [Vue 3 文档](https://v3.vuejs.org/)
- [Element Plus 文档](https://element-plus.org/)
- [Vite 文档](https://vitejs.dev/)

## 许可证

MIT License