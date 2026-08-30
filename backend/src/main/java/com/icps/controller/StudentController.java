package com.icps.controller;

import com.icps.entity.StudentMp;
import com.icps.service.StudentMpService;
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
    private StudentMpService studentService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStudents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> pageResult = studentService.getStudentsByPage(page, size);
            
            response.put("success", true);
            response.put("data", pageResult.get("list"));
            response.put("currentPage", page);
            response.put("page", page);
            response.put("pageSize", size);
            response.put("total", pageResult.get("total"));
            response.put("totalPages", pageResult.get("totalPages"));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取学生列表失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 学生详情：支持用户ID、学号、身份证号
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStudentById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> studentResult = studentService.getStudentById(id);
            
            if (Boolean.TRUE.equals(studentResult.get("success"))) {
                response.put("success", true);
                response.put("data", studentResult.get("student"));
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", studentResult.get("message"));
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
            List<Map<String, Object>> students = studentService.searchStudents(name, dept, major, sex);
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

    /**
     * 新增学生
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addStudent(@RequestBody StudentMp student) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = studentService.addStudent(student);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "新增学生失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(
            @PathVariable String id, @RequestBody Map<String, Object> studentData) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> updateResult = studentService.updateStudent(id, studentData);
            
            if (Boolean.TRUE.equals(updateResult.get("success"))) {
                response.put("success", true);
                response.put("message", updateResult.get("message"));
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", updateResult.get("message"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
            Map<String, Object> deleteResult = studentService.deleteStudent(id);
            
            if (Boolean.TRUE.equals(deleteResult.get("success"))) {
                response.put("success", true);
                response.put("message", deleteResult.get("message"));
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", deleteResult.get("message"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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
            Map<String, Object> statistics = studentService.getStudentStatistics();
            response.put("success", true);
            response.put("count", statistics.get("totalStudents"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取学生总数失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 学生统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStudentStatistics() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("data", studentService.getStudentStatistics());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取学生统计失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
