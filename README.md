# ICPS - 学生信息管理系统

## 项目概述

ICPS (Integrated Campus Personality System) 是一个现代化的学生信息管理系统，采用前后端分离架构，主要用于新生入学信息收集、个性评估和数据分析。系统采用B/S架构，使用Spring Boot提供RESTful API，前端使用Vue 3构建现代化界面。

## 功能特性

### 主要功能模块

1. **用户登录系统**
   - 支持教师和学生两种角色登录
   - 基于用户名和密码进行身份验证
   - JWT令牌认证机制
   - 角色权限管理

2. **学生信息管理**
   - 学生个人信息管理
   - 个人信息编辑和更新
   - 学籍信息维护

3. **成绩查询系统**
   - 学生成绩查询
   - 绩点计算和显示
   - 成绩统计分析

4. **课程信息管理**
   - 课程表查看
   - 选课功能
   - 课程信息展示

5. **个性化评估**
   - 基于多维度评估算法
   - 个性化报告生成
   - 发展建议提供

## 技术架构

### 后端技术
- **开发语言**: Java 8+
- **Web框架**: Spring Boot 2.7.0
- **数据访问**: MyBatis-Plus 3.5.3.1
- **安全框架**: Spring Security
- **数据库**: MySQL 8.0+
- **构建工具**: Maven 3.6+
- **数据迁移**: Flyway

### 前端技术
- **框架**: Vue 3.3.4
- **构建工具**: Vite 4.4.5
- **UI组件库**: Element Plus 2.3.8
- **状态管理**: Pinia 2.1.4
- **路由**: Vue Router 4.2.4
- **HTTP客户端**: Axios 1.4.0

### 数据库
- 使用MySQL数据库
- 支持数据迁移和版本控制
- 主要数据表：
  - `students` - 学生信息表
  - `teachers` - 教师信息表
  - `courses` - 课程信息表
  - `student_courses` - 学生选课表
  - `users` - 用户认证表

## 项目结构

```
icps/
├── backend/                # 后端Spring Boot应用
│   ├── src/main/java/     # Java源代码
│   │   └── com/icps/
│   │       ├── controller/ # 控制器层
│   │       ├── service/   # 业务逻辑层
│   │       ├── mapper/    # 数据访问层
│   │       ├── entity/    # 实体类
│   │       └── config/    # 配置类
│   ├── src/main/resources/ # 配置文件
│   │   ├── application.yml # 应用配置
│   │   └── db/migration/   # 数据库迁移脚本
│   └── pom.xml            # Maven配置
├── frontend/              # 前端Vue应用
│   ├── src/
│   │   ├── api/          # API接口
│   │   ├── components/   # 公共组件
│   │   ├── router/       # 路由配置
│   │   ├── stores/       # 状态管理
│   │   ├── styles/       # 样式文件
│   │   └── views/        # 页面组件
│   ├── public/           # 静态资源
│   ├── package.json      # 依赖配置
│   └── vite.config.js    # Vite配置
├── docs/                  # 项目文档
│   └── legacy/           # 历史文档（已迁移）
└── README.md             # 项目说明文档
```

## 核心模块说明

### 后端模块 (backend/)

#### 控制器层 (controller/)
- `AuthController.java` - 用户认证控制器
- `StudentController.java` - 学生管理控制器

#### 服务层 (service/)
- `AuthService.java` - 认证服务
- `StudentService.java` - 学生业务服务
- `mybatisplus/` - MyBatis-Plus服务实现

#### 数据访问层 (mapper/)
- `StudentMpMapper.java` - 学生数据访问
- `TeacherMpMapper.java` - 教师数据访问
- `UserMpMapper.java` - 用户数据访问
- `CourseMpMapper.java` - 课程数据访问

#### 实体类 (entity/)
- `Student.java` - 学生实体类
- `Teacher.java` - 教师实体类
- `mybatisplus/` - MyBatis-Plus实体类

#### 配置类 (config/)
- `MybatisPlusConfig.java` - MyBatis-Plus配置
- `MyMetaObjectHandler.java` - 元数据处理器

### 前端模块 (frontend/)

#### 页面组件 (views/)
- `Login.vue` - 登录页面
- `Dashboard.vue` - 仪表盘页面
- `student/` - 学生功能页面
  - `Info.vue` - 个人信息
  - `Grades.vue` - 成绩查询
  - `Courses.vue` - 课程信息

#### 核心功能
- `api/` - API接口封装
- `router/` - 路由配置
- `stores/` - Pinia状态管理
- `components/` - 公共组件

## 快速开始

### 环境要求
- Java 8+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 启动步骤

1. **数据库配置**
   ```sql
   -- 创建数据库
   CREATE DATABASE icpsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

2. **启动后端**
   ```bash
   cd backend
   java -jar target/icps-backend-1.0.0.jar
   ```
   - 后端访问地址: http://localhost:8081/api
   - API文档: http://localhost:8081/api/actuator

3. **启动前端**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   - 前端访问地址: http://localhost:3000
   - 登录后自动跳转到仪表盘

4. **访问应用**
   - 打开浏览器访问 http://localhost:3000
   - 使用测试账号登录系统

## 使用说明

### 学生用户
1. 选择"学生"身份
2. 输入用户名和密码登录
3. 查看个人信息、成绩、课程等信息
4. 可以编辑个人信息和查看学习情况

### 教师用户
1. 选择"教师"身份
2. 输入用户名和密码登录
3. 查看学生信息列表
4. 使用搜索功能查找特定学生
5. 管理学生成绩和课程信息

### 个性化评估
系统支持基于多维度数据的个性化评估：
- 个人基本信息分析
- 学习行为模式识别
- 发展潜力评估
- 个性化建议生成

## 开发指南

### 后端开发
- 遵循Spring Boot最佳实践
- 使用MyBatis-Plus进行数据访问
- 实现RESTful API规范
- 添加适当的单元测试

### 前端开发
- 使用Vue 3 Composition API
- 遵循Element Plus设计规范
- 组件化开发
- 响应式设计适配

### 数据库设计
- 表名使用小写字母和下划线
- 字段名使用驼峰命名法
- 主键使用自增ID
- 支持逻辑删除和软删除

### Git工作流
- 功能开发使用feature分支
- 提交信息遵循约定式提交规范
- 定期合并到dev分支
- 通过PR进行代码审查

## API文档

### 认证相关
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/user-info` - 获取用户信息

### 学生管理
- `GET /api/students` - 获取学生列表
- `GET /api/students/{id}` - 获取学生详情
- `PUT /api/students/{id}` - 更新学生信息

### 成绩管理
- `GET /api/students/{id}/grades` - 获取学生成绩

### 课程管理
- `GET /api/students/{id}/courses` - 获取学生课程

## 部署说明

### 开发环境
- 后端端口: 8081
- 前端端口: 3000
- 数据库: MySQL 8.0

### 生产环境
- 前端构建: `npm run build`
- 后端打包: `mvn clean package`
- 使用Nginx作为反向代理
- 支持Docker容器化部署

## 故障排除

### 常见问题
1. **端口冲突**: 检查端口是否被占用
2. **数据库连接失败**: 确认数据库服务和配置
3. **跨域问题**: 检查前端代理配置
4. **依赖安装失败**: 清理缓存重新安装

### 日志查看
- 后端日志: 控制台输出
- 前端日志: 浏览器开发者工具
- 数据库日志: MySQL错误日志

## 贡献指南

1. Fork项目到个人仓库
2. 创建功能分支进行开发
3. 提交代码并创建PR
4. 通过代码审查后合并
5. 更新相关文档

## 许可证

本项目采用MIT许可证，详见LICENSE文件。

## 联系方式

- 项目仓库: https://github.com/1316869355/icps
- 问题反馈: 通过GitHub Issues提交
- 开发团队: ICPS Team

---

*最后更新: 2025年12月 - 重构为前后端分离架构*