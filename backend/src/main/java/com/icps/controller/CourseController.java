package com.icps.controller;

import com.icps.entity.CourseMp;
import com.icps.service.CourseMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程接口
 */
@RestController
@RequestMapping("/courses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    @Autowired
    private CourseMpService courseMpService;

    /**
     * 课程列表（支持分页与条件查询）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCourses(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String teacherName,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Integer status) {

        Map<String, Object> response = new HashMap<>();
        try {
            boolean hasCondition = notBlank(name) || notBlank(teacherName) || notBlank(semester) || status != null;

            if (hasCondition) {
                List<Map<String, Object>> courses = courseMpService.searchCourses(name, teacherName, semester, status);
                response.put("success", true);
                response.put("data", courses);
                response.put("total", courses.size());
                response.put("content", courses);
                response.put("page", page);
                response.put("size", size);
                return ResponseEntity.ok(response);
            }

            Map<String, Object> pageResult = courseMpService.getCoursesByPage(page, size);
            response.put("success", true);
            response.put("data", pageResult.get("list"));
            response.put("content", pageResult.get("list"));
            response.put("total", pageResult.get("total"));
            response.put("page", page);
            response.put("size", size);
            response.put("totalPages", pageResult.get("totalPages"));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取课程列表失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 课程详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourseById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = courseMpService.getCourseById(id);
            if (Boolean.TRUE.equals(result.get("success"))) {
                response.put("success", true);
                response.put("data", result.get("course"));
                return ResponseEntity.ok(response);
            }
            response.put("success", false);
            response.put("message", result.get("message"));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取课程详情失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 新增课程
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> addCourse(@RequestBody CourseMp course) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = courseMpService.addCourse(course);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "新增课程失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新课程
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCourse(@PathVariable Long id,
                                                            @RequestBody Map<String, Object> courseData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = courseMpService.updateCourse(id, courseData);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新课程失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 删除课程（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCourse(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = courseMpService.deleteCourse(id);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "删除课程失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 课程统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> statistics() {
        Map<String, Object> response = new HashMap<>();
        try {
            response.put("success", true);
            response.put("data", courseMpService.getCourseStatistics());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取课程统计失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 按选课记录重算课程已选人数
     */
    @PostMapping("/sync-enrolled")
    public ResponseEntity<Map<String, Object>> syncEnrolled() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = courseMpService.syncEnrolled();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "同步已选人数失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private boolean notBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
