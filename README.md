# ICPS - 学生信息管理系统

## 项目概述

ICPS (Integrated Campus Personality System) 是一个基于Java Web的学生信息管理系统，主要用于新生入学信息收集、个性评估和数据分析。系统采用B/S架构，使用Spring JDBC进行数据访问，前端使用HTML/CSS/JavaScript技术栈。

## 功能特性

### 主要功能模块

1. **用户登录系统**
   - 支持教师和学生两种角色登录
   - 基于身份证号和用户名进行身份验证
   - 角色权限管理

2. **学生信息收集**
   - 新生个人信息调查问卷
   - 包含基本信息、个人特征、兴趣爱好等
   - 支持个性化评估分析

3. **数据分析与评估**
   - 基于放松方式、血型、星座的个性化评估算法
   - 决策树分析模型
   - 多种评估结果分类

4. **信息查询与管理**
   - 学生信息检索功能
   - 分页显示和搜索功能
   - 教师端信息管理

## 技术架构

### 后端技术
- **开发语言**: Java
- **Web框架**: Servlet/JSP
- **数据访问**: Spring JDBC
- **构建工具**: Eclipse IDE
- **依赖管理**: Maven (通过pom.xml)

### 前端技术
- **HTML5**: 页面结构
- **CSS3**: 样式设计
- **JavaScript**: 交互逻辑
- **jQuery**: DOM操作和AJAX
- **doT.js**: 模板引擎

### 数据库
- 使用MySQL数据库
- 主要数据表：
  - `icps_stu` - 学生信息表
  - `icps_code` - 系统代码表

## 项目结构

```
icps/
├── src/                    # Java源代码
│   ├── com/icps/
│   │   ├── bean/           # 实体类
│   │   ├── dao/           # 数据访问层
│   │   ├── filter/        # 过滤器
│   │   ├── servlet/       # 控制器
│   │   └── util/          # 工具类
├── test/                   # 测试代码
├── WebContent/             # Web资源文件
│   ├── design/            # 设计文档
│   ├── js/                # JavaScript文件
│   ├── style/             # CSS样式文件
│   ├── META-INF/          # 部署描述符
│   └── WEB-INF/           # Web应用配置
├── .classpath             # Eclipse类路径
├── .project               # Eclipse项目文件
└── README.md              # 项目说明文档
```

## 核心模块说明

### 实体类 (bean包)
- `Student.java` - 学生实体类
- `Teacher.java` - 教师实体类
- `SysCode.java` - 系统代码实体类
- `Hobit.java` - 兴趣爱好实体类

### 数据访问层 (dao包)
- `StudentDao.java` - 学生数据操作
- `TeacherDao.java` - 教师数据操作
- `CodeDao.java` - 系统代码操作
- `MyDataSource.java` - 数据源配置
- `Page.java` - 分页处理

### 控制器 (servlet包)
- `loginServlet.java` - 登录处理
- `searchServlet.java` - 搜索功能
- `uploadInfoServlet.java` - 信息上传
- `CodeServlet.java` - 代码管理
- `CountStu.java` - 学生统计

### 工具类 (util包)
- `Evalued.java` - 个性化评估算法
- `SystemCodeEnum.java` - 系统枚举定义

## 安装与部署

### 环境要求
- JDK 1.8+
- Tomcat 8.0+
- MySQL 5.7+
- Eclipse IDE (可选)

### 部署步骤

1. **数据库配置**
   ```sql
   -- 创建数据库
   CREATE DATABASE icps;
   
   -- 导入数据表结构
   -- (参考design文件夹中的数据库设计文档)
   ```

2. **数据源配置**
   - 修改`src/com/icps/dao/MyDataSource.java`中的数据库连接信息

3. **项目部署**
   - 将项目导入Eclipse IDE
   - 配置Tomcat服务器
   - 部署到Web容器

4. **启动应用**
   - 启动Tomcat服务器
   - 访问应用首页: `http://localhost:8080/icps`

## 使用说明

### 学生用户
1. 使用身份证号和用户名登录
2. 填写个人信息调查问卷
3. 提交后查看个性化评估结果

### 教师用户
1. 使用教师账号登录
2. 查看学生信息列表
3. 使用搜索功能查找特定学生
4. 查看学生的评估结果

## 评估算法

系统采用基于决策树的个性化评估模型：

### 评估维度
1. **放松方式** (0401-0404)
   - 运动健身
   - 休闲娱乐
   - 逛街聚会
   - 网购影视

2. **血型** (0501-0504)
   - A型
   - B型
   - O型
   - AB型

3. **星座** (0601-0612)
   - 12个星座类型

### 评估结果
- 0701: 积极向上型
- 0702: 社交活跃型
- 0703: 内向思考型
- 0704: 灵活适应型

## 开发规范

### 代码规范
- 遵循Java编码规范
- 使用有意义的类名和方法名
- 添加必要的注释说明

### 数据库规范
- 表名使用前缀`icps_`
- 字段名使用小写蛇形命名法
- 主键使用自增ID

## 维护与支持

### 问题反馈
- 在`WebContent/design/说明.txt`中记录问题
- 联系项目维护团队

### 版本更新
- 记录在项目根目录的变更日志中
- 定期进行代码审查和优化

## 许可证

本项目仅用于教育和研究目的，未经许可不得用于商业用途。

## 联系方式

如有问题或建议，请联系项目开发团队。

---

*最后更新: 2025年11月*