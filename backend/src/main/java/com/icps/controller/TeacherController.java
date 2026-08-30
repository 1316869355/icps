package com.icps.controller;

import com.icps.entity.TeacherMp;
import com.icps.service.TeacherMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师接口
 */
@RestController
@RequestMapping("/teachers")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TeacherController {

    @Autowired
    private TeacherMpService teacherMpService;

    /**
     * 教师列表（支持分页与条件查询）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getTeachers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {

        Map<String, Object> response = new HashMap<>();
        try {
            boolean hasCondition = notBlank(name) || notBlank(dept) || notBlank(title) || status != null;

            if (hasCondition) {
                List<Map<String, Object>> teachers = teacherMpService.searchTeachers(name, dept, title, status);
                response.put("success", true);
                response.put("data", teachers);
                response.put("total", teachers.size());
                return ResponseEntity.ok(response);
            }

            Map<String, Object> pageResult = teacherMpService.getTeachersByPage(page, size);
            response.put("success", true);
            response.put("data", pageResult.get("list"));
            response.put("total", pageResult.get("total"));
            response.put("page", page);
            response.put("size", size);
            response.put("totalPages", pageResult.get("totalPages"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取教师列表失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 教师详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getTeacherById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = teacherMpService.getTeacherById(id);
            if (Boolean.TRUE.equals(result.get("success"))) {
                response.put("success", true);
                response.put("data", result.get("teacher"));
                return ResponseEntity.ok(response);
            }
            response.put("success", false);
            response.put("message", result.get("message"));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取教师详情失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 新增教师
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addTeacher(@RequestBody TeacherMp teacher) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = teacherMpService.addTeacher(teacher);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "新增教师失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新教师
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTeacher(@PathVariable Long id,
                                                             @RequestBody Map<String, Object> teacherData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = teacherMpService.updateTeacher(id, teacherData);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新教师失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除教师（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTeacher(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = teacherMpService.deleteTeacher(id);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除教师失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 教师统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> statistics() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("data", teacherMpService.getTeacherStatistics());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取教师统计失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean notBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
