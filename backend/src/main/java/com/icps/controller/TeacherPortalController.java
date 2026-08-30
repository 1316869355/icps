package com.icps.controller;

import com.icps.entity.CourseMp;
import com.icps.entity.StudentCourseMp;
import com.icps.entity.StudentMp;
import com.icps.security.SecurityUtils;
import com.icps.service.CourseMpService;
import com.icps.service.StudentCourseMpService;
import com.icps.service.StudentMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 教师端业务接口。
 *
 * <p>{@code {studentId}} 支持三种写法：用户ID（纯数字）、学号、身份证号。
 * 全部端点仅教师与管理员可访问，见 {@code SecurityConfig} 的角色配置。</p>
 */
@RestController
@RequestMapping("/teacher")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TeacherPortalController {

    @Autowired
    private StudentMpService studentMpService;

    @Autowired
    private StudentCourseMpService studentCourseMpService;

    @Autowired
    private CourseMpService courseMpService;

    /**
     * 学生列表：支持分页与姓名/学院/专业/性别组合筛选
     */
    @GetMapping("/students")
    public ResponseEntity<Map<String, Object>> listStudents(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) String major,
            @RequestParam(required = false) Integer sex) {

        Map<String, Object> response = new HashMap<>();
        try {
            boolean hasCondition = notBlank(name) || notBlank(dept) || notBlank(major) || sex != null;

            if (hasCondition) {
                List<Map<String, Object>> all = studentMpService.searchStudents(name, dept, major, sex);
                response.putAll(paginate(all, page, size));
                response.put("success", true);
                return ResponseEntity.ok(response);
            }

            Map<String, Object> pageResult = studentMpService.getStudentsByPage(page, size);
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
            response.put("message", "获取学生列表失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 关键词模糊搜索（学号 / 姓名 / 班级 / 专业）
     */
    @GetMapping("/students/search")
    public ResponseEntity<Map<String, Object>> searchStudents(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        Map<String, Object> response = new HashMap<>();
        try {
            if (!notBlank(keyword)) {
                response.put("success", true);
                response.put("data", new java.util.ArrayList<>());
                response.put("total", 0);
                response.put("page", page);
                response.put("size", size);
                response.put("totalPages", 0);
                return ResponseEntity.ok(response);
            }

            List<Map<String, Object>> all = studentMpService.searchStudentsByKeyword(keyword.trim());
            response.putAll(paginate(all, page, size));
            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "搜索学生失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 学生详情：档案 + 全部课程与成绩明细
     */
    @GetMapping("/students/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentDetail(@PathVariable String studentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            StudentMp student = studentMpService.resolveStudent(studentId);
            if (student == null) {
                response.put("success", false);
                response.put("message", "学生不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Map<String, Object> detail = new HashMap<>(studentMpService.convertStudentToMap(student));
            detail.put("courses", studentCourseMpService.getGradeDetails(student.getStuCardNo()));

            response.put("success", true);
            response.put("data", detail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取学生详情失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 录入 / 更新成绩。学生未选此课程（含已退课）时返回 404。
     */
    @PutMapping("/students/{studentId}/courses/{courseId}/grade")
    public ResponseEntity<Map<String, Object>> inputGrade(@PathVariable String studentId,
                                                          @PathVariable Long courseId,
                                                          @RequestBody(required = false) Map<String, Object> gradeData) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 先做课程归属校验：课程不存在→404，归属不符→403
            CourseMp course = courseMpService.getCourseEntityById(courseId);
            if (course == null) {
                response.put("success", false);
                response.put("message", "课程不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            if (!SecurityUtils.canAccessCourse(course)) {
                return SecurityUtils.forbidden("无权为其他教师的课程录入成绩");
            }

            StudentMp student = studentMpService.resolveStudent(studentId);
            if (student == null) {
                response.put("success", false);
                response.put("message", "学生不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            StudentCourseMp record = studentCourseMpService.findByStuAndCourse(student.getStuCardNo(), courseId);
            if (record == null) {
                response.put("success", false);
                response.put("message", "该学生未选择此课程，无法录入成绩");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            Map<String, Object> body = gradeData == null ? new HashMap<>() : gradeData;
            if (!body.containsKey("grade") && !body.containsKey("gradePoint")) {
                response.put("success", false);
                response.put("message", "请提供 grade 或 gradePoint");
                return ResponseEntity.badRequest().body(response);
            }

            // updateGrade 是全量覆盖，未提供成绩时保留原值
            Double grade = body.containsKey("grade")
                    ? toDouble(body.get("grade"))
                    : record.getGrade();
            Double gradePoint = body.containsKey("gradePoint")
                    ? toDouble(body.get("gradePoint"))
                    : defaultGradePoint(grade);

            Map<String, Object> result = studentCourseMpService.updateGrade(record.getId(), grade, gradePoint);
            result.put("id", record.getId());
            result.put("courseId", courseId);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "录入成绩失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 课程学生名单（含成绩），用于教师按课程录入成绩
     */
    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<Map<String, Object>> getCourseStudents(@PathVariable Long courseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            CourseMp course = courseMpService.getCourseEntityById(courseId);
            if (course == null) {
                response.put("success", false);
                response.put("message", "课程不存在");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            if (!SecurityUtils.canAccessCourse(course)) {
                return SecurityUtils.forbidden("无权查看其他教师的课程学生名单");
            }

            List<Map<String, Object>> students = studentCourseMpService.getCourseGradeDetails(courseId);
            response.put("success", true);
            response.put("data", students);
            response.put("content", students);
            response.put("total", students.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取课程学生名单失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 综合统计：学生 / 课程 / 选课 / 成绩概览
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> statistics() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("student", studentMpService.getStudentStatistics());
            data.put("course", courseMpService.getCourseStatistics());
            data.put("selection", studentCourseMpService.getStudentCourseStatistics());
            data.put("grade", studentCourseMpService.getGradeOverview());

            response.put("success", true);
            response.put("data", data);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取统计信息失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 对内存中的列表做分页，返回与其他分页接口一致的字段
     */
    private Map<String, Object> paginate(List<Map<String, Object>> all, int page, int size) {
        Map<String, Object> result = new HashMap<>();
        int total = all.size();
        int safePage = Math.max(1, page);
        int safeSize = Math.max(1, size);
        int from = Math.min((safePage - 1) * safeSize, total);
        int to = Math.min(from + safeSize, total);

        List<Map<String, Object>> pageList = all.subList(from, to);
        result.put("data", pageList);
        result.put("content", pageList);
        result.put("total", total);
        result.put("page", safePage);
        result.put("size", safeSize);
        result.put("totalPages", (int) Math.ceil((double) total / safeSize));
        return result;
    }

    /**
     * 按百分制成绩换算 4.0 制绩点
     */
    private Double defaultGradePoint(Double grade) {
        if (grade == null) {
            return null;
        }
        if (grade >= 90) return 4.0;
        if (grade >= 85) return 3.7;
        if (grade >= 82) return 3.3;
        if (grade >= 78) return 3.0;
        if (grade >= 75) return 2.7;
        if (grade >= 72) return 2.3;
        if (grade >= 68) return 2.0;
        if (grade >= 64) return 1.5;
        if (grade >= 60) return 1.0;
        return 0.0;
    }

    private Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : Double.valueOf(text);
    }

    private boolean notBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
