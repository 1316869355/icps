package com.icps.repository;

import com.icps.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class StudentRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    // 学生登录验证
    public boolean login(String cardNo, String userName) {
        String sql = "SELECT COUNT(*) FROM icps_stu WHERE stu_card_no = ? AND sname LIKE ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, cardNo, "%" + userName + "%");
        return count != null && count > 0;
    }
    
    // 根据ID查找学生
    public Student findById(String id) {
        String sql = "SELECT * FROM icps_stu WHERE stu_card_no = ?";
        List<Student> students = jdbcTemplate.query(sql, new Object[]{id}, new StudentRowMapper());
        return students.isEmpty() ? null : students.get(0);
    }
    
    // 获取所有学生列表
    public List<Student> findAll() {
        String sql = "SELECT * FROM icps_stu";
        return jdbcTemplate.query(sql, new StudentRowMapper());
    }
    
    // 分页查询学生列表
    public List<Student> findPage(int pageNum, int pageSize) {
        String sql = "SELECT * FROM icps_stu LIMIT ? OFFSET ?";
        int offset = (pageNum - 1) * pageSize;
        return jdbcTemplate.query(sql, new Object[]{pageSize, offset}, new StudentRowMapper());
    }
    
    // 条件查询学生
    public List<Student> findByCondition(String name, String dept, String major, Integer sex) {
        StringBuilder sql = new StringBuilder("SELECT * FROM icps_stu WHERE 1=1");
        
        if (name != null && !name.trim().isEmpty()) {
            sql.append(" AND sname LIKE ?");
        }
        if (dept != null && !dept.equals("0")) {
            sql.append(" AND stu_dept = ?");
        }
        if (major != null && !major.equals("0")) {
            sql.append(" AND stu_major = ?");
        }
        if (sex != null && sex != 0) {
            sql.append(" AND ssex = ?");
        }
        
        return jdbcTemplate.query(sql.toString(), new StudentRowMapper());
    }
    
    // 更新学生信息
    public int update(Student student) {
        String sql = "UPDATE icps_stu SET sname=?, ssex=?, sage=?, stu_address=?, " +
                "shbt=?, sblood=?, start_sign=?, evaluated_type=?, " +
                "stu_dept=?, stu_major=?, stu_clazz=?, region=? WHERE stu_card_no=?";
        
        return jdbcTemplate.update(sql, 
                student.getSname(), student.getSsex(), student.getSage(), student.getStuAddress(),
                student.getShbt(), student.getSbloodType(), student.getSstartSign(), student.getSevaledType(),
                student.getStuDept(), student.getStuMajor(), student.getStuClazz(), student.getRegion(),
                student.getStuCardNum());
    }
    
    // 删除学生
    public int delete(String cardNo) {
        String sql = "DELETE FROM icps_stu WHERE stu_card_no = ?";
        return jdbcTemplate.update(sql, cardNo);
    }
    
    // 获取学生总数
    public int count() {
        String sql = "SELECT COUNT(*) FROM icps_stu";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }
    
    // 学生行映射器
    private static class StudentRowMapper implements org.springframework.jdbc.core.RowMapper<Student> {
        @Override
        public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
            Student student = new Student();
            student.setSno(rs.getString("sno"));
            student.setSname(rs.getString("sname"));
            student.setSsex(rs.getInt("ssex"));
            student.setSage(rs.getInt("sage"));
            student.setStuCardNum(rs.getString("stu_card_no"));
            student.setStuAddress(rs.getString("stu_address"));
            student.setShbt(rs.getString("shbt"));
            student.setSbloodType(rs.getString("sblood"));
            student.setSstartSign(rs.getString("start_sign"));
            student.setSevaledType(rs.getString("evaluated_type"));
            student.setStuDept(rs.getString("stu_dept"));
            student.setStuMajor(rs.getString("stu_major"));
            student.setStuClazz(rs.getString("stu_clazz"));
            student.setRegion(rs.getString("region"));
            return student;
        }
    }
}