package com.icps.security;

import com.icps.entity.CourseMp;
import com.icps.entity.StudentMp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 鉴权辅助工具：从 {@code SecurityContextHolder} 读取当前 {@link JwtTokenProvider.JwtPrincipal}，
 * 提供角色判断与数据归属判断，供各 Controller 做水平越权校验。
 *
 * <p>URL 维度（路径 + HTTP 方法）的权限由 {@code SecurityConfig} 统一配置；
 * 涉及「某条数据属于谁」的判断无法靠 URL 表达，统一走这里。</p>
 */
public final class SecurityUtils {

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_TEACHER = "teacher";
    public static final String ROLE_STUDENT = "student";

    /**
     * 学生自助修改本人档案时允许提交的字段白名单。
     *
     * <p>{@code sblood / bloodType}、{@code start_sign / zodiac} 是同一列的两套别名：
     * 前者是列名，后者是 Service 层与前端实际使用的键名，两套都接受。</p>
     */
    private static final Set<String> STUDENT_SELF_EDITABLE_FIELDS = new HashSet<>(Arrays.asList(
            "stuAddress", "shbt", "sblood", "bloodType", "start_sign", "zodiac", "region"));

    private SecurityUtils() {
    }

    /**
     * 当前登录主体，未认证时返回 {@code null}
     */
    public static JwtTokenProvider.JwtPrincipal currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !(authentication.getPrincipal() instanceof JwtTokenProvider.JwtPrincipal)) {
            return null;
        }
        return (JwtTokenProvider.JwtPrincipal) authentication.getPrincipal();
    }

    /**
     * 当前登录用户的 userId
     */
    public static Long currentUserId() {
        JwtTokenProvider.JwtPrincipal principal = currentPrincipal();
        return principal == null ? null : principal.getUserId();
    }

    /**
     * 当前登录角色（小写：admin / teacher / student）
     */
    public static String currentRole() {
        JwtTokenProvider.JwtPrincipal principal = currentPrincipal();
        return principal == null || principal.getRole() == null
                ? null : principal.getRole().toLowerCase();
    }

    public static boolean isAdmin() {
        return ROLE_ADMIN.equals(currentRole());
    }

    public static boolean isTeacherOrAdmin() {
        String role = currentRole();
        return ROLE_TEACHER.equals(role) || ROLE_ADMIN.equals(role);
    }

    public static boolean isStudent() {
        return ROLE_STUDENT.equals(currentRole());
    }

    /**
     * 数据归属校验：教师与管理员可访问任意学生数据；学生仅可访问本人数据。
     */
    public static boolean canAccessStudent(StudentMp student) {
        if (isTeacherOrAdmin()) {
            return true;
        }
        if (student == null || student.getUserId() == null) {
            return false;
        }
        Long currentUserId = currentUserId();
        return currentUserId != null && currentUserId.equals(student.getUserId());
    }

    /**
     * 课程归属校验：admin 直通；teacher 仅当 course.teacherId 与当前登录教师 userId 一致时通过。
     *
     * <p>icps_course.teacher_id 对齐 icps_user.user_id（即 JWT principal.userId），
     * 与 icps_teacher.user_id 同源，故直接比 currentUserId。</p>
     *
     * @param course 已加载的课程实体，null 时返回 false
     */
    public static boolean canAccessCourse(CourseMp course) {
        if (isAdmin()) {
            return true;
        }
        if (course == null || course.getTeacherId() == null) {
            return false;
        }
        Long currentUserId = currentUserId();
        return currentUserId != null && currentUserId.equals(course.getTeacherId());
    }

    /**
     * 学生自助修改档案时过滤提交字段，只保留白名单内的键
     */
    public static Map<String, Object> filterStudentSelfUpdate(Map<String, Object> studentData) {
        Map<String, Object> filtered = new HashMap<>();
        if (studentData == null) {
            return filtered;
        }
        for (Map.Entry<String, Object> entry : studentData.entrySet()) {
            if (entry.getKey() != null && STUDENT_SELF_EDITABLE_FIELDS.contains(entry.getKey())) {
                filtered.put(entry.getKey(), entry.getValue());
            }
        }
        return filtered;
    }

    /**
     * 统一 403 响应体：{@code {"success":false,"message":"...","code":403}}
     */
    public static ResponseEntity<Map<String, Object>> forbidden(String message) {
        return status(HttpStatus.FORBIDDEN, message);
    }

    /**
     * 统一 404 响应体：{@code {"success":false,"message":"...","code":404}}
     */
    public static ResponseEntity<Map<String, Object>> notFound(String message) {
        return status(HttpStatus.NOT_FOUND, message);
    }

    private static ResponseEntity<Map<String, Object>> status(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>(4);
        body.put("success", false);
        body.put("message", message);
        body.put("code", status.value());
        return ResponseEntity.status(status).body(body);
    }
}
