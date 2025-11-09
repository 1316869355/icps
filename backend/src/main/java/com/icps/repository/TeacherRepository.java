package com.icps.repository;

import com.icps.entity.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TeacherRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // 教师登录验证
    public boolean login(String cardNo, String userName) {
        String sql = "SELECT COUNT(*) FROM icps_teacher WHERE teacher_card_no = ? AND teacher_name LIKE ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, cardNo, "%" + userName + "%");
        return count != null && count > 0;
    }
    
    // 根据ID查找教师
    public Teacher findById(String teacherId) {
        String sql = "SELECT * FROM icps_teacher WHERE teacher_id = ?";
        List<Teacher> teachers = jdbcTemplate.query(sql, new Object[]{teacherId}, new TeacherRowMapper());
        return teachers.isEmpty() ? null : teachers.get(0);
    }
    
    // 根据身份证号查找教师
    public Teacher findByCardNo(String cardNo) {
        String sql = "SELECT * FROM icps_teacher WHERE teacher_card_no = ?";
        List<Teacher> teachers = jdbcTemplate.query(sql, new Object[]{cardNo}, new TeacherRowMapper());
        return teachers.isEmpty() ? null : teachers.get(0);
    }
    
    // 获取所有教师列表
    public List<Teacher> findAll() {
        String sql = "SELECT * FROM icps_teacher";
        return jdbcTemplate.query(sql, new TeacherRowMapper());
    }
    
    // 教师行映射器
    private static class TeacherRowMapper implements org.springframework.jdbc.core.RowMapper<Teacher> {
        @Override
        public Teacher mapRow(ResultSet rs, int rowNum) throws SQLException {
            Teacher teacher = new Teacher();
            teacher.setTeacherId(rs.getString("teacher_id"));
            teacher.setTeacherName(rs.getString("teacher_name"));
            teacher.setTeacherCardNo(rs.getString("teacher_card_no"));
            teacher.setDept(rs.getString("dept"));
            teacher.setTitle(rs.getString("title"));
            return teacher;
        }
    }
}