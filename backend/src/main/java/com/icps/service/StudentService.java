package com.icps.service;

import com.icps.entity.jpa.StudentJpa;
import com.icps.repository.jpa.StudentJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 学生服务
 */
@Service
public class StudentService {
    
    @Autowired
    private StudentJpaRepository studentRepository;
    
    /**
     * 获取所有学生列表
     */
    public List<Map<String, Object>> getAllStudents() {
        List<StudentJpa> students = studentRepository.findAll();
        return convertStudentsToMap(students);
    }
    
    /**
     * 分页查询学生列表
     */
    public Map<String, Object> getStudentsByPage(int pageNum, int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<StudentJpa> studentPage = studentRepository.findAll(pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", convertStudentsToMap(studentPage.getContent()));
        result.put("total", studentPage.getTotalElements());
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", studentPage.getTotalPages());
        
        return result;
    }
    
    /**
     * 根据条件查询学生
     */
    public List<Map<String, Object>> searchStudents(String name, String dept, String major, Integer sex) {
        Specification<StudentJpa> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(root.get("sname"), "%" + name + "%"));
            }
            if (dept != null && !dept.equals("0")) {
                predicates.add(cb.equal(root.get("stuDept"), dept));
            }
            if (major != null && !major.equals("0")) {
                predicates.add(cb.equal(root.get("stuMajor"), major));
            }
            if (sex != null && sex != 0) {
                predicates.add(cb.equal(root.get("ssex"), sex));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        List<StudentJpa> students = studentRepository.findAll(spec);
        return convertStudentsToMap(students);
    }
    
    /**
     * 根据ID获取学生详情
     */
    public Map<String, Object> getStudentById(String studentId) {
        Optional<StudentJpa> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isPresent()) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("student", convertStudentToMap(studentOpt.get()));
            return result;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "学生不存在");
            return result;
        }
    }
    
    /**
     * 更新学生信息
     */
    public Map<String, Object> updateStudent(String studentId, Map<String, Object> studentData) {
        Optional<StudentJpa> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isPresent()) {
            StudentJpa student = studentOpt.get();
            
            // 更新学生信息
            if (studentData.containsKey("sname")) {
                student.setSname((String) studentData.get("sname"));
            }
            if (studentData.containsKey("ssex")) {
                student.setSsex((Integer) studentData.get("ssex"));
            }
            if (studentData.containsKey("sage")) {
                student.setSage((Integer) studentData.get("sage"));
            }
            if (studentData.containsKey("stuAddress")) {
                student.setStuAddress((String) studentData.get("stuAddress"));
            }
            if (studentData.containsKey("shbt")) {
                student.setShbt((String) studentData.get("shbt"));
            }
            if (studentData.containsKey("sbloodType")) {
                student.setSbloodType((String) studentData.get("sbloodType"));
            }
            if (studentData.containsKey("sstartSign")) {
                student.setSstartSign((String) studentData.get("sstartSign"));
            }
            if (studentData.containsKey("sevaledType")) {
                student.setSevaledType((String) studentData.get("sevaledType"));
            }
            if (studentData.containsKey("stuDept")) {
                student.setStuDept((String) studentData.get("stuDept"));
            }
            if (studentData.containsKey("stuMajor")) {
                student.setStuMajor((String) studentData.get("stuMajor"));
            }
            if (studentData.containsKey("stuClazz")) {
                student.setStuClazz((String) studentData.get("stuClazz"));
            }
            if (studentData.containsKey("region")) {
                student.setRegion((String) studentData.get("region"));
            }
            
            studentRepository.save(student);
            
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("message", "学生信息更新成功");
            return result;
        } else {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "学生不存在");
            return result;
        }
    }
    
    /**
     * 获取学生统计信息
     */
    public Map<String, Object> getStudentStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总学生数
        long totalStudents = studentRepository.countStudents();
        statistics.put("totalStudents", totalStudents);
        
        // 按学院统计
        List<Object[]> deptStats = studentRepository.countByDepartment();
        Map<String, Long> deptCount = new HashMap<>();
        for (Object[] stat : deptStats) {
            deptCount.put((String) stat[0], (Long) stat[1]);
        }
        statistics.put("departmentStats", deptCount);
        
        return statistics;
    }
    
    /**
     * 转换学生列表为Map列表
     */
    private List<Map<String, Object>> convertStudentsToMap(List<StudentJpa> students) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (StudentJpa student : students) {
            result.add(convertStudentToMap(student));
        }
        return result;
    }
    
    /**
     * 转换单个学生实体为Map
     */
    private Map<String, Object> convertStudentToMap(StudentJpa student) {
        Map<String, Object> studentMap = new HashMap<>();
        studentMap.put("id", student.getStuCardNo());
        studentMap.put("studentId", student.getSno());
        studentMap.put("name", student.getSname());
        studentMap.put("gender", student.getSsex() == 1 ? "男" : "女");
        studentMap.put("age", student.getSage());
        studentMap.put("cardNo", student.getStuCardNo());
        studentMap.put("address", student.getStuAddress());
        studentMap.put("hobby", student.getShbt());
        studentMap.put("bloodType", student.getSbloodType());
        studentMap.put("zodiac", student.getSstartSign());
        studentMap.put("evaluation", student.getSevaledType());
        studentMap.put("department", student.getStuDept());
        studentMap.put("major", student.getStuMajor());
        studentMap.put("clazz", student.getStuClazz());
        studentMap.put("region", student.getRegion());
        studentMap.put("createdAt", student.getCreatedAt());
        return studentMap;
    }
}