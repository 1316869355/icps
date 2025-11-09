package com.icps.controller;

import com.icps.entity.Student;
import com.icps.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentController {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStudents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Student> students = studentRepository.findPage(page, size);
            int total = studentRepository.count();
            
            response.put("success", true);
            response.put("data", students);
            response.put("currentPage", page);
            response.put("pageSize", size);
            response.put("total", total);
            response.put("totalPages", (int) Math.ceil((double) total / size));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取学生列表失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStudentById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Student student = studentRepository.findById(id);
            if (student != null) {
                response.put("success", true);
                response.put("data", student);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "学生不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取学生信息失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchStudents(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) Integer sex) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Student> students = studentRepository.findByCondition(name, dept, major, sex);
            response.put("success", true);
            response.put("data", students);
            response.put("total", students.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "搜索学生失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(
            @PathVariable String id, @RequestBody Student student) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Student existingStudent = studentRepository.findById(id);
            if (existingStudent == null) {
                response.put("success", false);
                response.put("message", "学生不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            // 设置更新后的学生信息
            student.setStuCardNum(id);
            int result = studentRepository.update(student);
            
            if (result > 0) {
                response.put("success", true);
                response.put("message", "学生信息更新成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "学生信息更新失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新学生信息失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteStudent(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Student existingStudent = studentRepository.findById(id);
            if (existingStudent == null) {
                response.put("success", false);
                response.put("message", "学生不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
            int result = studentRepository.delete(id);
            
            if (result > 0) {
                response.put("success", true);
                response.put("message", "学生删除成功");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "学生删除失败");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除学生失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getStudentCount() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            int count = studentRepository.count();
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取学生总数失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}