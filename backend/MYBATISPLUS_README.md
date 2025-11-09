# MyBatisPlus 集成说明

## 概述

本项目已成功集成了 MyBatisPlus，与原有的 JPA 实现共存。MyBatisPlus 提供了更简洁的 CRUD 操作和强大的查询功能。

## 新增文件结构

```
backend/src/main/java/com/icps/
├── entity/mybatisplus/     # MyBatisPlus 实体类
│   ├── StudentMp.java      # 学生实体类
│   └── TeacherMp.java      # 教师实体类
├── mapper/                 # Mapper 接口
│   └── StudentMpMapper.java # 学生 Mapper
├── service/mybatisplus/    # MyBatisPlus 服务层
│   └── StudentMpService.java
├── controller/             # 控制器
│   └── StudentMpController.java
└── config/                 # 配置类
    ├── MybatisPlusConfig.java
    └── MyMetaObjectHandler.java
```

## 依赖配置

已在 `pom.xml` 中添加了以下依赖：

```xml
<!-- MyBatis Plus Starter -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-boot-starter</artifactId>
    <version>3.5.3.1</version>
</dependency>

<!-- MyBatis Plus Generator -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-generator</artifactId>
    <version>3.5.3.1</version>
</dependency>
```

## 配置文件

在 `application.yml` 中添加了 MyBatisPlus 配置：

```yaml
# MyBatisPlus配置
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

## 使用方法

### 1. 实体类（Entity）

使用 MyBatisPlus 注解定义实体类：

```java
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("icps_stu")
public class StudentMp {
    @TableId(value = "stu_card_no", type = IdType.INPUT)
    private String stuCardNo;
    
    @TableField("sname")
    private String sname;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
```

### 2. Mapper 接口

继承 `BaseMapper` 并添加自定义方法：

```java
@Mapper
public interface StudentMpMapper extends BaseMapper<StudentMp> {
    @Select("SELECT * FROM icps_stu WHERE sno = #{sno}")
    StudentMp selectBySno(@Param("sno") String sno);
}
```

### 3. Service 层

使用 MyBatisPlus 提供的 CRUD 方法：

```java
@Service
public class StudentMpService {
    @Autowired
    private StudentMpMapper studentMpMapper;
    
    // 查询所有
    public List<StudentMp> getAllStudents() {
        return studentMpMapper.selectList(null);
    }
    
    // 条件查询
    public List<StudentMp> searchStudents(String name, String dept) {
        LambdaQueryWrapper<StudentMp> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StudentMp::getSname, name)
               .eq(StudentMp::getStuDept, dept);
        return studentMpMapper.selectList(wrapper);
    }
    
    // 分页查询
    public IPage<StudentMp> getStudentsByPage(int pageNum, int pageSize) {
        Page<StudentMp> page = new Page<>(pageNum, pageSize);
        return studentMpMapper.selectPage(page, null);
    }
}
```

### 4. Controller 层

提供 REST API 接口：

```java
@RestController
@RequestMapping("/api/mp")
public class StudentMpController {
    @Autowired
    private StudentMpService studentMpService;
    
    @GetMapping("/students")
    public List<StudentMp> getAllStudents() {
        return studentMpService.getAllStudents();
    }
}
```

## API 端点

### MyBatisPlus 版本 API

- `GET /api/mp/students` - 获取所有学生
- `GET /api/mp/students/page?pageNum=1&pageSize=10` - 分页查询
- `GET /api/mp/students/search?name=张三&dept=计算机学院` - 条件查询
- `GET /api/mp/students/{id}` - 根据ID查询
- `POST /api/mp/students` - 添加学生
- `PUT /api/mp/students/{id}` - 更新学生
- `DELETE /api/mp/students/{id}` - 删除学生
- `GET /api/mp/students/statistics` - 统计信息

### 原有 JPA 版本 API

原有的 JPA API 保持不变：

- `GET /api/students` - 获取所有学生（JPA）
- `GET /api/students/page` - 分页查询（JPA）
- `GET /api/students/search` - 条件查询（JPA）

## 主要特性

### 1. 自动填充

创建时间和更新时间自动填充：

```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
    
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
```

### 2. 分页插件

自动配置了分页插件：

```java
@Bean
public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
    interceptor.addInnerInterceptor(paginationInterceptor);
    return interceptor;
}
```

### 3. 条件构造器

使用 LambdaQueryWrapper 进行类型安全的查询：

```java
LambdaQueryWrapper<StudentMp> wrapper = new LambdaQueryWrapper<>();
wrapper.like(StudentMp::getSname, "张")
       .eq(StudentMp::getSsex, 1)
       .orderByDesc(StudentMp::getCreatedAt);
List<StudentMp> students = studentMpMapper.selectList(wrapper);
```

## 优势对比

### MyBatisPlus vs JPA

| 特性 | MyBatisPlus | JPA |
|------|-------------|-----|
| 代码简洁性 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |
| 灵活度 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| 学习曲线 | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| 性能优化 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| 复杂查询 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ |

## 迁移建议

1. **逐步迁移**：可以先在新功能中使用 MyBatisPlus，逐步替换现有 JPA 代码
2. **并行使用**：MyBatisPlus 和 JPA 可以共存，根据场景选择合适的技术
3. **团队培训**：确保团队成员熟悉 MyBatisPlus 的使用方法

## 注意事项

1. MyBatisPlus 默认开启下划线转驼峰命名
2. 实体类需要使用 MyBatisPlus 注解
3. Mapper 接口需要添加 `@Mapper` 注解
4. 分页查询需要配置分页插件
5. 自动填充功能需要配置 MetaObjectHandler

## 总结

MyBatisPlus 的集成使得数据层编码更加简洁高效，特别是对于复杂的查询场景。建议在实际项目中根据具体需求选择合适的持久层技术。