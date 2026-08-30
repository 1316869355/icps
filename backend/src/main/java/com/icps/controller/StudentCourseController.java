package com.icps.controller;

import com.icps.entity.StudentMp;
import com.icps.security.SecurityUtils;
import com.icps.service.StudentCourseMpService;
import com.icps.service.StudentMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 选课与成绩接口。
 *
 * <p>{@code {studentId}} 支持三种写法：用户ID（纯数字，登录态推荐）、学号、身份证号。</p>
 */
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class StudentCourseController {

    @Autowired
    private StudentCourseMpService studentCourseMpService;

    @Autowired
    private StudentMpService studentMpService;

    /**
     * 学生已选课程（含课程信息）
     */
    @GetMapping("/students/{studentId}/courses")
    public ResponseEntity<Map<String, Object>> getStudentCourses(@PathVariable String studentId,
                                                                 @RequestParam(required = false) String semester,
                                                                 @RequestParam(required = false) String academicYear) {
        Map<String, Object> response = new HashMap<>();
        try {
            StudentTarget target = resolveTarget(studentId, "无权查看其他学生的课程");
            if (target.error != null) {
                return target.error;
            }

            List<Map<String, Object>> courses =
                    studentCourseMpService.getGradeDetails(target.student.getStuCardNo());
            courses = filter(courses, semester, academicYear);

            response.put("success", true);
            response.put("data", courses);
            response.put("content", courses);
            response.put("total", courses.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取学生课程失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 学生成绩
     */
    @GetMapping("/students/{studentId}/grades")
    public ResponseEntity<Map<String, Object>> getStudentGrades(@PathVariable String studentId,
                                                                @RequestParam(required = false) String semester,
                                                                @RequestParam(required = false) String academicYear) {
        Map<String, Object> response = new HashMap<>();
        try {
            StudentTarget target = resolveTarget(studentId, "无权查看其他学生的成绩");
            if (target.error != null) {
                return target.error;
            }

            List<Map<String, Object>> grades =
                    studentCourseMpService.getGradeDetails(target.student.getStuCardNo());
            grades = filter(grades, semester, academicYear);

            response.put("success", true);
            response.put("data", grades);
            response.put("content", grades);
            response.put("total", grades.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取学生成绩失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 选课
     */
    @PostMapping("/students/{studentId}/courses/{courseId}/select")
    public ResponseEntity<Map<String, Object>> selectCourse(@PathVariable String studentId,
                                                            @PathVariable Long courseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            StudentTarget target = resolveTarget(studentId, "无权替其他学生选课");
            if (target.error != null) {
                return target.error;
            }

            Map<String, Object> result =
                    studentCourseMpService.selectCourse(target.student.getStuCardNo(), courseId);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "选课失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 退课
     */
    @DeleteMapping("/students/{studentId}/courses/{courseId}/drop")
    public ResponseEntity<Map<String, Object>> dropCourse(@PathVariable String studentId,
                                                          @PathVariable Long courseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            StudentTarget target = resolveTarget(studentId, "无权替其他学生退课");
            if (target.error != null) {
                return target.error;
            }

            Map<String, Object> result =
                    studentCourseMpService.dropCourse(target.student.getStuCardNo(), courseId);
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "退课失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 某门课程的成绩名单
     */
    @GetMapping("/courses/{courseId}/grades")
    public ResponseEntity<Map<String, Object>> getCourseGrades(@PathVariable Long courseId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> grades = studentCourseMpService.getCourseGradeDetails(courseId);
            response.put("success", true);
            response.put("data", grades);
            response.put("content", grades);
            response.put("total", grades.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "获取课程成绩失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 录入/更新成绩：存在记录则更新，不存在则创建
     */
    @PostMapping("/grades")
    public ResponseEntity<Map<String, Object>> inputGrade(@RequestBody Map<String, Object> gradeData) {
        Map<String, Object> response = new HashMap<>();
        try {
            String cardNo = gradeData.get("stuCardNo") == null
                    ? resolveCardNo(String.valueOf(gradeData.get("studentId")))
                    : String.valueOf(gradeData.get("stuCardNo"));

            Object idValue = gradeData.get("id");
            if (idValue != null) {
                Double grade = toDouble(gradeData.get("grade"));
                Double gradePoint = toDouble(gradeData.get("gradePoint"));
                Map<String, Object> result = studentCourseMpService.updateGrade(
                        Long.parseLong(String.valueOf(idValue)), grade, gradePoint);
                return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                        ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(result);
            }

            Object courseIdValue = gradeData.get("courseId");
            if (cardNo == null || courseIdValue == null) {
                response.put("success", false);
                response.put("message", "缺少学生标识或课程ID");
                return ResponseEntity.badRequest().body(response);
            }

            Map<String, Object> result = studentCourseMpService.selectCourse(cardNo, Long.parseLong(String.valueOf(courseIdValue)));
            if (!Boolean.TRUE.equals(result.get("success"))) {
                return ResponseEntity.badRequest().body(result);
            }
            Map<String, Object> updateResult = studentCourseMpService.updateGrade(
                    Long.parseLong(String.valueOf(result.get("id"))),
                    toDouble(gradeData.get("grade")),
                    toDouble(gradeData.get("gradePoint")));
            return ResponseEntity.status(Boolean.TRUE.equals(updateResult.get("success"))
                    ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(updateResult);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "录入成绩失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * 更新成绩
     */
    @PutMapping("/grades/{id}")
    public ResponseEntity<Map<String, Object>> updateGrade(@PathVariable Long id,
                                                           @RequestBody Map<String, Object> gradeData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> result = studentCourseMpService.updateGrade(id,
                    toDouble(gradeData.get("grade")), toDouble(gradeData.get("gradePoint")));
            return ResponseEntity.status(Boolean.TRUE.equals(result.get("success"))
                    ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(result);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新成绩失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private String resolveCardNo(String studentId) {
        StudentMp student = studentMpService.resolveStudent(studentId);
        return student == null ? null : student.getStuCardNo();
    }

    /**
     * 解析学生标识并做数据归属校验：教师与管理员可操作任意学生，学生仅可操作本人。
     *
     * @return 校验通过时 {@code error == null} 且 {@code student != null}；
     *         校验失败时 {@code error} 为可直接返回的 404 / 403 响应
     */
    private StudentTarget resolveTarget(String studentId, String deniedMessage) {
        StudentMp student = studentMpService.resolveStudent(studentId);
        if (student == null) {
            return StudentTarget.error(SecurityUtils.notFound("学生不存在"));
        }
        if (!SecurityUtils.canAccessStudent(student)) {
            return StudentTarget.error(SecurityUtils.forbidden(deniedMessage));
        }
        return StudentTarget.ok(student);
    }

    /**
     * 学生归属校验的结果载体
     */
    private static final class StudentTarget {
        private final StudentMp student;
        private final ResponseEntity<Map<String, Object>> error;

        private StudentTarget(StudentMp student, ResponseEntity<Map<String, Object>> error) {
            this.student = student;
            this.error = error;
        }

        static StudentTarget ok(StudentMp student) {
            return new StudentTarget(student, null);
        }

        static StudentTarget error(ResponseEntity<Map<String, Object>> error) {
            return new StudentTarget(null, error);
        }
    }

    private List<Map<String, Object>> filter(List<Map<String, Object>> rows, String semester, String academicYear) {
        if ((semester == null || semester.trim().isEmpty())
                && (academicYear == null || academicYear.trim().isEmpty())) {
            return rows;
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            if (semester != null && !semester.trim().isEmpty()
                    && !semester.equals(String.valueOf(row.get("semester")))) {
                continue;
            }
            if (academicYear != null && !academicYear.trim().isEmpty()
                    && !academicYear.equals(String.valueOf(row.get("academicYear")))) {
                continue;
            }
            result.add(row);
        }
        return result;
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
}
