# CODEBUDDY.md This file provides guidance to CodeBuddy when working with code in this repository.

## 常用命令

### 后端 (Spring Boot)
```bash
cd backend
mvn clean package                    # 构建JAR包到 target/icps-backend-1.0.0.jar
mvn compile                         # 仅编译
mvn test                            # 运行单元测试
java -jar target/icps-backend-1.0.0.jar  # 运行已构建的JAR
```

### 前端 (Vue 3 + Vite)
```bash
cd frontend
npm install                         # 安装依赖
npm run dev                         # 开发模式运行（端口3000）
npm run build                       # 生产构建到 dist/
npm run preview                     # 预览构建产物
```

## 技术栈概览

### 后端架构 (backend/)
- **框架**: Spring Boot 2.7.0 + MyBatis-Plus 3.5.3.1
- **入口**: `IcpsApplication.java` (端口8081, context-path: /api)
- **分层结构**: controller → service → mapper → entity
- **核心控制器**: `AuthController.java` (认证), `StudentController.java` (学生管理)
- **配置**: `application.yml` 中配置数据库连接(MySQL:3306/icpsdb)和MyBatis-Plus逻辑删除

### 前端架构 (frontend/)
- **框架**: Vue 3.3.4 + Element Plus 2.3.8 + Pinia 2.1.4
- **入口**: `main.js` 初始化Vue应用和路由
- **路由**: `router/index.js` 定义路由守卫，未登录重定向到/login
- **API封装**: `api/request.js` 配置axios实例，自动附加JWT token到请求头
- **代理配置**: Vite代理 `/api` 请求到 `http://localhost:8081`

### API设计
- 所有API前缀: `/api` (由后端context-path统一添加)
- 认证端点: `/api/auth/login`, `/api/auth/logout`, `/api/auth/user-info`
- 学生端点: `/api/students/*`
- 认证方式: Bearer Token (JWT)

### 数据模型
- MyBatis-Plus实体位于 `entity/mybatisplus/`，支持逻辑删除(deleted字段)
- 数据库迁移脚本位于 `src/main/resources/db/migration/` (Flyway，暂禁用)
- 主要表: students, teachers, courses, users

### 前端路由结构
```
/login        → Login.vue (公开)
/dashboard    → Dashboard.vue (需认证)
/student/info    → student/Info.vue
/student/grades  → student/Grades.vue  
/student/courses → student/Courses.vue
```
登录状态存储在Pinia store (`stores/auth.js`)和localStorage，路由守卫根据`isLoggedIn`状态拦截未认证访问。

## 关键配置

### 环境变量
- 前端: `VITE_API_BASE_URL` (默认 `/api`)
- 后端数据库: `spring.datasource.url` 中配置，账号密码均为 `root`

### CORS处理
前端通过Vite开发服务器代理解决跨域，生产环境需配置Nginx反向代理到后端8081端口。

## 开发注意事项
- App.vue 区分登录页和主应用布局：登录页直接渲染router-view，主应用包含侧边栏和顶部导航
- 后端实体类使用驼峰命名，数据库字段自动映射(下划线转驼峰)
- 前端JWT token有效期由后端控制，前端自动处理401响应并跳转登录
