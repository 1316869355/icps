-- 创建学生表
CREATE TABLE IF NOT EXISTS icps_stu (
    stu_card_no VARCHAR(50) PRIMARY KEY,
    user_id BIGINT NOT NULL DEFAULT 0,
    sno VARCHAR(20) NOT NULL UNIQUE,
    sname VARCHAR(50) NOT NULL,
    ssex INT DEFAULT NULL,
    sage INT DEFAULT NULL,
    stu_address VARCHAR(200) DEFAULT NULL,
    shbt VARCHAR(100) DEFAULT NULL,
    sblood VARCHAR(10) DEFAULT NULL,
    start_sign VARCHAR(10) DEFAULT NULL,
    evaluated_type VARCHAR(20) DEFAULT NULL,
    stu_dept VARCHAR(50) DEFAULT NULL,
    stu_major VARCHAR(50) DEFAULT NULL,
    stu_clazz VARCHAR(50) DEFAULT NULL,
    region VARCHAR(50) DEFAULT NULL,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_stu_user_id (user_id),
    INDEX idx_sno (sno),
    INDEX idx_sname (sname),
    INDEX idx_dept (stu_dept)
);

-- 创建教师表
CREATE TABLE IF NOT EXISTS icps_teacher (
    teacher_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    teacher_card_no VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL DEFAULT 0,
    teacher_name VARCHAR(50) NOT NULL,
    dept VARCHAR(50) DEFAULT NULL,
    title VARCHAR(50) DEFAULT NULL,
    email VARCHAR(100) DEFAULT NULL,
    phone VARCHAR(20) DEFAULT NULL,
    status INT DEFAULT 1,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_teacher_user_id (user_id),
    INDEX idx_card_no (teacher_card_no),
    INDEX idx_name (teacher_name),
    INDEX idx_dept (dept)
);

-- 创建课程表
CREATE TABLE IF NOT EXISTS icps_course (
    course_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    credit INT DEFAULT NULL,
    hours INT DEFAULT NULL,
    teacher_id BIGINT DEFAULT NULL,
    teacher_name VARCHAR(50) DEFAULT NULL,
    classroom VARCHAR(50) DEFAULT NULL,
    semester VARCHAR(20) DEFAULT NULL,
    academic_year VARCHAR(20) DEFAULT NULL,
    schedule VARCHAR(100) DEFAULT NULL,
    status INT DEFAULT 1,
    capacity INT DEFAULT NULL,
    enrolled INT DEFAULT 0,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_course_code (course_code),
    INDEX idx_course_name (course_name),
    INDEX idx_teacher (teacher_id),
    INDEX idx_semester (semester)
);

-- 创建学生选课表（多对多关系）
CREATE TABLE IF NOT EXISTS icps_student_course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    stu_card_no VARCHAR(50) NOT NULL,
    course_id BIGINT NOT NULL,
    grade DECIMAL(4,1) DEFAULT NULL,
    grade_point DECIMAL(3,2) DEFAULT NULL,
    academic_year VARCHAR(20) DEFAULT NULL,
    semester VARCHAR(20) DEFAULT NULL,
    status INT DEFAULT 1, -- 1: 已选课 2: 已完成 0: 退课
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_stu_course (stu_card_no, course_id, academic_year, semester),
    INDEX idx_stu_card_no (stu_card_no),
    INDEX idx_course_id (course_id)
);

-- 创建用户认证表
CREATE TABLE IF NOT EXISTS icps_user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'student', -- student, teacher, admin
    status INT DEFAULT 1,
    deleted TINYINT(1) NOT NULL DEFAULT 0,
    last_login DATETIME DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
);
