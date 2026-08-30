-- H2 测试种子数据
-- 所有账号密码均为 123456，使用与生产库一致的 BCrypt 哈希

-- 用户：含 admin / teacher / student，以及一个被停用( status=0 )的账号
INSERT INTO icps_user (user_id, username, password, role, status, deleted) VALUES
(1,    'admin',      '$2a$12$R8B5WyTCyZNLgIqi9F6s0OOAlcAHevErXN9V/FDGclmpLwm.7q9PG', 'admin',   1, 0),
(4,    'teacher1',   '$2a$12$R8B5WyTCyZNLgIqi9F6s0OOAlcAHevErXN9V/FDGclmpLwm.7q9PG', 'teacher', 1, 0),
(1011, '2023001001', '$2a$12$R8B5WyTCyZNLgIqi9F6s0OOAlcAHevErXN9V/FDGclmpLwm.7q9PG', 'student', 1, 0),
(1012, '2023001002', '$2a$12$R8B5WyTCyZNLgIqi9F6s0OOAlcAHevErXN9V/FDGclmpLwm.7q9PG', 'student', 1, 0),
(1013, 'disabled01', '$2a$12$R8B5WyTCyZNLgIqi9F6s0OOAlcAHevErXN9V/FDGclmpLwm.7q9PG', 'student', 0, 0);

-- 学生：CARD004 为逻辑删除数据，用于验证 deleted = 0 过滤
INSERT INTO icps_stu (user_id, stu_card_no, sno, sname, ssex, sage, stu_address, shbt, sblood, start_sign, evaluated_type, stu_dept, stu_major, stu_clazz, region, deleted) VALUES
(1011, 'CARD001', '2023001001', '张三',   1, 20, '北京市海淀区', '篮球', 'A型', '白羊座', '积极型', '计算机学院', '计算机科学与技术', '计科2101', '北京', 0),
(1012, 'CARD002', '2023001002', '李四',   2, 21, '上海市浦东新区', '阅读', 'B型', '金牛座', '稳重型', '软件学院',   '软件工程',         '软件2102', '上海', 0),
(1014, 'CARD004', '2023001004', '已删除', 1, 22, '广州市天河区', '游泳', 'O型', '双子座', '未评估', '计算机学院', '计算机科学与技术', '计科2101', '广东', 1);

-- 教师
INSERT INTO icps_teacher (user_id, teacher_card_no, teacher_name, dept, title, email, phone, status, deleted) VALUES
(4, 'T001', '王老师', '计算机学院', '教授', 'wang@example.com', '13800138001', 1, 0);

-- 课程：CS301 为停开课程(status=0)，用于验证选课时的状态校验
INSERT INTO icps_course (course_id, course_code, course_name, credit, hours, teacher_id, teacher_name, classroom, semester, academic_year, schedule, status, capacity, enrolled, deleted) VALUES
(1, 'CS101', '计算机基础', 3, 48, 4, '王老师', '教学楼A101', '1', '2023-2024', '周一 9:00-11:00', 1, 60, 1, 0),
(2, 'CS201', '数据结构',   4, 64, 4, '王老师', '教学楼B201', '1', '2023-2024', '周二 14:00-16:00', 1, 50, 0, 0),
(3, 'CS301', '操作系统',   3, 48, 4, '王老师', '教学楼C301', '2', '2023-2024', '周三 9:00-11:00', 0, 30, 0, 0);

-- 选课记录：id=4 为逻辑删除数据
--   活跃记录数：CS101 = 2（CARD001、CARD002）、CS201 = 1（CARD001）
--   而 icps_course.enrolled 种子值为 CS101=1、CS201=0，用于验证 syncEnrolled 的重算
INSERT INTO icps_student_course (id, stu_card_no, course_id, grade, grade_point, academic_year, semester, status, deleted) VALUES
(1, 'CARD001', 1, 88.0, 3.70, '2023-2024', '1', 1, 0),
(2, 'CARD001', 2, NULL, NULL, '2023-2024', '1', 1, 0),
(3, 'CARD002', 1, 92.0, 4.00, '2023-2024', '1', 1, 0),
(4, 'CARD002', 2, 70.0, 2.00, '2023-2024', '1', 1, 1);
