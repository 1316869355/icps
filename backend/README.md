# ICPS 后端模块

学生信息管理系统后端API，基于Spring Boot + MyBatis-Plus构建。

## 项目概述

ICPS (Intelligent Campus Performance System) 后端模块提供了学生信息管理的RESTful API接口，支持学生、教师、课程等核心功能。项目采用分层架构设计，支持多种ORM框架。

## 技术栈

- **框架**: Spring Boot 2.7.0
- **数据库**: MySQL 8.0
- **主ORM**: MyBatis-Plus 3.5.3.1
- **数据库迁移**: Flyway
- **构建工具**: Maven
- **Java版本**: 1.8

## 项目结构

```
backend/
├── src/main/java/com/icps/
│   ├── IcpsApplication.java          # 应用启动类
│   ├── controller/                   # 控制器层
│   │   ├── AuthController.java       # 认证控制器
│   │   └── StudentController.java     # 学生管理控制器
│   ├── config/                       # 配置类
│   │   ├── MybatisPlusConfig.java    # MyBatis-Plus配置
│   │   └── MyMetaObjectHandler.java  # 元数据处理器
│   ├── entity/                       # 实体类
│   │   ├── Student.java              # 学生实体
│   │   ├── Teacher.java              # 教师实体
│   │   └── mybatisplus/              # MyBatis-Plus实体类
│   │       ├── CourseMp.java
│   │       ├── StudentCourseMp.java
│   │       ├── StudentMp.java
│   │       ├── TeacherMp.java
│   │       └── UserMp.java
│   ├── mapper/                       # MyBatis-Plus Mapper接口
│   │   ├── CourseMpMapper.java
│   │   ├── StudentCourseMpMapper.java
│   │   ├── StudentMpMapper.java
│   │   ├── TeacherMpMapper.java
│   │   └── UserMpMapper.java
│   └── service/                      # 业务逻辑层
│       ├── mybatisplus/              # MyBatis-Plus服务层
│       │   ├── CourseMpService.java
│       │   ├── StudentCourseMpService.java
│       │   ├── StudentMpService.java
│       │   ├── TeacherMpService.java
│       │   └── UserMpService.java
│       ├── AuthService.java          # 认证服务
│       └── StudentService.java       # 学生服务(主要业务)
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

#### 方式一：使用已编译的JAR包（推荐）

1. 确保项目已编译（已有JAR包在target目录）：
```bash
# 如果没有JAR包，先编译
mvn clean package -DskipTests
```

2. 直接运行JAR包：
```bash
java -jar target/icps-backend-1.0.0.jar
```

#### 方式二：使用Maven运行

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

#### 成功启动标识

应用启动成功后，会看到类似以下输出：
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v2.7.0)

...
2025-12-27 22:xx:xx.xxx  INFO 12345 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8081 (http) with context path '/api'
2025-12-27 22:xx:xx.xxx  INFO 12345 --- [           main] com.icps.IcpsApplication                 : Started IcpsApplication in 3.456 seconds (process running for 5.123)
```

#### 访问地址

应用启动后，访问：
- **API根路径**: http://localhost:8081/api
- **健康检查**: http://localhost:8081/api/actuator/health
- **API文档**: http://localhost:8081/api/actuator

#### 快速启动命令（Windows PowerShell）

```powershell
# 切换到后端目录
cd "d:\workspace\codebuddy\icps\backend"

# 直接运行已编译的JAR包
java -jar target/icps-backend-1.0.0.jar
```

#### 环境要求确认

- Java 8+ （当前系统: Java 1.8.0_92）
- MySQL 8.0+ （需确保服务运行）
- Maven 3.6+ （当前系统: Maven 3.6.3）

### MyBatis-Plus特性

项目已集成MyBatis-Plus，提供以下功能：
- 自动CRUD操作
- 分页插件
- 逻辑删除
- 自动填充
- 条件构造器
- 主键生成策略

### MyBatis-Plus特性

项目已集成MyBatis-Plus，提供以下功能：
- 自动CRUD操作
- 分页插件
- 逻辑删除
- 自动填充
- 条件构造器
- 主键生成策略

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
- MyBatis-Plus配置（主ORM）
- 日志级别配置

### MyBatis-Plus配置

```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0
```

### 安全配置

- 使用Spring Security进行密码加密
- 认证和授权机制（待完善）

## 开发指南

### 添加新功能（MyBatis-Plus方式）

1. 在`entity/mybatisplus`包下创建实体类，继承`BaseMapper`
2. 在`mapper`包下创建Mapper接口，继承`BaseMapper<T>`
3. 在`service/mybatisplus`包下创建Service类，实现业务逻辑
4. 在`controller`包下创建REST API

### 代码规范

- 遵循Spring Boot最佳实践
- 使用MyBatis-Plus注解进行实体映射（主推）
- 统一的异常处理机制
- RESTful API设计规范
- 优先使用MyBatis-Plus进行新功能开发

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