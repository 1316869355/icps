-- V3 迁移：补齐 deleted 逻辑删除列、为 icps_stu.user_id 增加 UNIQUE 索引
--
-- 背景：实际库现状已满足"删除外键 / 删除 icps_user 冗余关联列 / user_id 已与 icps_user 对齐"等目标
--       （icps_teacher 物理主键已是 user_id；icps_user 已无 stu_card_no/teacher_id；全库无 FOREIGN KEY）。
--       故本脚本仅做增量补齐，且全部采用"若不存在才执行"的存储过程，保证可重放（重复执行安全）。
-- 适用：可经 mysql CLI 执行，或经本仓库 run_v3.py 执行（自动处理 DELIMITER）。

DELIMITER //

DROP PROCEDURE IF EXISTS add_col_if_missing //
CREATE PROCEDURE add_col_if_missing(
  IN p_tbl VARCHAR(64),
  IN p_col VARCHAR(64),
  IN p_def VARCHAR(255)
)
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = DATABASE() AND table_name = p_tbl AND column_name = p_col
  ) THEN
    SET @sql = CONCAT('ALTER TABLE `', p_tbl, '` ADD COLUMN `', p_col, '` ', p_def);
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END //

DROP PROCEDURE IF EXISTS add_unique_if_missing //
CREATE PROCEDURE add_unique_if_missing(
  IN p_tbl VARCHAR(64),
  IN p_idx VARCHAR(64),
  IN p_cols VARCHAR(255)
)
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = p_tbl AND index_name = p_idx
  ) THEN
    SET @sql = CONCAT('ALTER TABLE `', p_tbl, '` ADD UNIQUE KEY `', p_idx, '` (', p_cols, ')');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
  END IF;
END //

DELIMITER ;

-- 1) 全表补充 deleted 逻辑删除列（匹配 application.yml 的 logic-delete 配置）
CALL add_col_if_missing('icps_stu',            'deleted', 'TINYINT(1) NOT NULL DEFAULT 0');
CALL add_col_if_missing('icps_teacher',        'deleted', 'TINYINT(1) NOT NULL DEFAULT 0');
CALL add_col_if_missing('icps_course',         'deleted', 'TINYINT(1) NOT NULL DEFAULT 0');
CALL add_col_if_missing('icps_student_course', 'deleted', 'TINYINT(1) NOT NULL DEFAULT 0');
CALL add_col_if_missing('icps_user',           'deleted', 'TINYINT(1) NOT NULL DEFAULT 0');

-- 2) icps_stu.user_id 增加 UNIQUE 索引（与 icps_user.user_id 对齐，供实体 @TableId 使用）
CALL add_unique_if_missing('icps_stu', 'uk_stu_user_id', '`user_id`');

-- 3) 清理临时存储过程
DROP PROCEDURE IF EXISTS add_col_if_missing;
DROP PROCEDURE IF EXISTS add_unique_if_missing;
