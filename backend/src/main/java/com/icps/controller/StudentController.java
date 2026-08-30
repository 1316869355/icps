package com.icps.controller;

import com.icps.entity.StudentMp;
import com.icps.security.SecurityUtils;
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
     * 学生详情：支持用户ID、学号、身份证号。
     *
     * <p>教师与管理员可查看任意学生；学生仅可查看本人档案，越权返回 403。</p>
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getStudentById(@PathVariable String id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            StudentMp target = studentService.resolveStudent(id);
            if (target == null) {
                return SecurityUtils.notFound("学生不存在");
            }
            if (!SecurityUtils.canAccessStudent(target)) {
                return SecurityUtils.forbidden("无权访问其他学生的档案");
            }

            response.put("success", true);
            response.put("data", studentService.convertStudentToMap(target));
            return ResponseEntity.ok(response);
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
    
    /**
     * 更新学生信息。
     *
     * <p>教师与管理员可修改任意学生的全部字段；学生只能修改本人档案，
     * 且仅 {@code stuAddress / shbt / sblood / start_sign / region} 五个字段生效，
     * 越权返回 403。</p>
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateStudent(
            @PathVariable String id, @RequestBody Map<String, Object> studentData) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            StudentMp target = studentService.resolveStudent(id);
            if (target == null) {
                return SecurityUtils.notFound("学生不存在");
            }
            if (!SecurityUtils.canAccessStudent(target)) {
                return SecurityUtils.forbidden("无权修改其他学生的档案");
            }

            Map<String, Object> payload = SecurityUtils.isStudent()
                    ? SecurityUtils.filterStudentSelfUpdate(studentData)
                    : studentData;

            Map<String, Object> updateResult = studentService.updateStudent(id, payload);
            
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
