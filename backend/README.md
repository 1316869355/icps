# ICPS 后端模块

学生信息管理系统后端API，基于Spring Boot构建。

## 项目概述

ICPS (Intelligent Campus Performance System) 后端模块提供了学生信息管理的RESTful API接口，支持学生、教师、课程等核心功能。

## 技术栈

- **框架**: Spring Boot 2.7.0
- **数据库**: MySQL 8.0
- **ORM**: Spring Data JPA + Hibernate
- **数据库迁移**: Flyway
- **构建工具**: Maven
- **Java版本**: 1.8

## 项目结构

```
backend/
├── src/main/java/com/icps/
│   ├── IcpsApplication.java          # 应用启动类
│   ├── controller/                   # 控制器层
│   │   ├── AuthController.java       # 认证控制器（已重构为使用Service层）
│   │   └── StudentController.java     # 学生管理控制器（已重构为使用Service层）
│   ├── entity/                       # 实体类
│   │   ├── Student.java              # 学生实体
│   │   ├── Teacher.java              # 教师实体
│   │   └── jpa/                      # JPA实体类
│   │       ├── CourseJpa.java
│   │       ├── StudentCourseJpa.java
│   │       ├── StudentJpa.java
│   │       ├── TeacherJpa.java
│   │       └── UserJpa.java
│   ├── repository/                   # 数据访问层
│   │   ├── StudentRepository.java    # 学生仓库
│   │   ├── TeacherRepository.java    # 教师仓库
│   │   └── jpa/                      # JPA仓库
│   │       ├── CourseJpaRepository.java
│   │       ├── StudentCourseJpaRepository.java
│   │       ├── StudentJpaRepository.java
│   │       ├── TeacherJpaRepository.java
│   │       └── UserJpaRepository.java
│   └── service/                      # 业务逻辑层
│       ├── AuthService.java          # 认证服务
│       └── StudentService.java       # 学生服务
├── src/main/resources/
│   ├── application.yml               # 应用配置
│   └── db/migration/                 # 数据库迁移脚本
│       ├── V1__create_base_tables.sql
│       └── V2__insert_test_data.sql
└── pom.xml                           # Maven配置
```

## 快速开始

### 环境要求

- Java 8
- MySQL 8.0+
- Maven 3.6+

### 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE icpsdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改数据库连接配置（`src/main/resources/application.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/icpsdb?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 运行应用

1. 克隆项目并进入backend目录：
```bash
cd backend
```

2. 使用Maven编译：
```bash
mvn clean compile
```

3. 运行应用：
```bash
mvn spring-boot:run
```

或者直接运行主类：
```bash
mvn exec:java -Dexec.mainClass="com.icps.IcpsApplication"
```

应用启动后，访问：http://localhost:8081/api

## API接口

### 认证相关

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出

### 学生管理

- `GET /api/students` - 获取学生列表
- `GET /api/students/{id}` - 获取学生详情
- `POST /api/students` - 创建学生
- `PUT /api/students/{id}` - 更新学生信息
- `DELETE /api/students/{id}` - 删除学生

### 教师管理

- 教师相关API（待完善）

### 课程管理

- 课程相关API（待完善）

## 数据库设计

### 核心表结构

- **students** - 学生信息表
- **teachers** - 教师信息表  
- **courses** - 课程信息表
- **student_courses** - 学生选课表
- **users** - 用户认证表

### 数据库迁移

项目使用Flyway进行数据库版本管理，迁移脚本位于`src/main/resources/db/migration/`目录：

- `V1__create_base_tables.sql` - 创建基础表结构
- `V2__insert_test_data.sql` - 插入测试数据

## 配置说明

### 应用配置

- 服务端口：8081
- 上下文路径：/api
- 数据库连接池配置
- JPA/Hibernate配置
- 日志级别配置

### 安全配置

- 使用Spring Security进行密码加密
- 认证和授权机制（待完善）

## 开发指南

### 添加新功能

1. 在`entity`包下创建实体类
2. 在`repository`包下创建数据访问接口
3. 在`service`包下实现业务逻辑
4. 在`controller`包下创建REST API

### 代码规范

- 遵循Spring Boot最佳实践
- 使用JPA注解进行实体映射
- 统一的异常处理机制
- RESTful API设计规范

## 构建和部署

### 构建JAR包

```bash
mvn clean package
```

### 运行JAR包

```bash
java -jar target/icps-backend-1.0.0.jar
```

### Docker部署（可选）

Docker部署配置待完善。

## 故障排除

### 常见问题

1. **数据库连接失败**：检查MySQL服务是否启动，用户名密码是否正确
2. **端口冲突**：修改`application.yml`中的端口配置
3. **Flyway迁移失败**：检查数据库版本和迁移脚本

### 日志查看

应用日志输出到控制台，可通过`logging.level`配置调整日志级别。

## 贡献指南

欢迎提交Issue和Pull Request来改进项目。

## 许可证

本项目采用MIT许可证。

## 联系方式

如有问题请联系项目维护者。