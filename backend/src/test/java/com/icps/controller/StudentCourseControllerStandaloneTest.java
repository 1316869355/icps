package com.icps.controller;

import com.icps.entity.StudentMp;
import com.icps.security.JwtTokenProvider;
import com.icps.service.StudentCourseMpService;
import com.icps.service.StudentMpService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 选课 / 成绩接口控制层测试（standalone MockMvc）。
 *
 * <p>注意：{@link StudentCourseController} 内部统一通过
 * {@code studentMpService.resolveStudent(...)} 解析学生标识，因此本测试桩一律打在
 * {@code resolveStudent} 上，而非 getByUserId / getBySno / getByCardNo。</p>
 *
 * <p>由于 {@code resolveTarget} 会调用 {@code SecurityUtils.canAccessStudent} 做归属校验，
 * standalone MockMvc 不会套用 Spring Security 过滤器链，故在 @BeforeEach 中手动塞入一个
 * teacher 身份，表示「教师可访问任意学生数据」这一分支。</p>
 */
@ExtendWith(MockitoExtension.class)
class StudentCourseControllerStandaloneTest {

    @Mock
    private StudentCourseMpService studentCourseMpService;

    @Mock
    private StudentMpService studentMpService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        StudentCourseController controller = new StudentCourseController();
        ReflectionTestUtils.setField(controller, "studentCourseMpService", studentCourseMpService);
        ReflectionTestUtils.setField(controller, "studentMpService", studentMpService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // 以教师身份访问，绕过学生归属校验（canAccessStudent 对 teacher/admin 一律放行）
        JwtTokenProvider.JwtPrincipal principal =
                new JwtTokenProvider.JwtPrincipal(4L, "teacher1", "teacher", System.currentTimeMillis() + 3600_000L);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void stubResolve(String id, String cardNo) {
        StudentMp student = new StudentMp();
        student.setStuCardNo(cardNo);
        when(studentMpService.resolveStudent(id)).thenReturn(student);
    }

    @Test
    void studentCoursesResolvesStudentAndReturnsGrades() throws Exception {
        stubResolve("1011", "CARD001");
        when(studentCourseMpService.getGradeDetails("CARD001")).thenReturn(Arrays.asList(gradeRow()));

        mockMvc.perform(get("/students/1011/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].courseName").value("计算机基础"));
    }

    @Test
    void studentCoursesFallsBackThroughResolveStudent() throws Exception {
        stubResolve("2023001002", "CARD002");
        when(studentCourseMpService.getGradeDetails("CARD002")).thenReturn(Arrays.asList(gradeRow()));

        mockMvc.perform(get("/students/2023001002/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    void studentCoursesReturns404ForUnknownStudent() throws Exception {
        when(studentMpService.resolveStudent("404")).thenReturn(null);

        mockMvc.perform(get("/students/404/courses"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("学生不存在"));
    }

    @Test
    void studentGradesCanBeFilteredBySemester() throws Exception {
        stubResolve("1011", "CARD001");

        Map<String, Object> first = gradeRow();
        Map<String, Object> second = new HashMap<>(first);
        second.put("semester", "2");
        when(studentCourseMpService.getGradeDetails("CARD001")).thenReturn(Arrays.asList(first, second));

        mockMvc.perform(get("/students/1011/grades").param("semester", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].semester").value("1"));
    }

    @Test
    void selectCourseReturnsServiceResult() throws Exception {
        stubResolve("1011", "CARD001");

        Map<String, Object> ok = new HashMap<>();
        ok.put("success", true);
        ok.put("message", "选课成功");
        when(studentCourseMpService.selectCourse("CARD001", 1L)).thenReturn(ok);

        mockMvc.perform(post("/students/1011/courses/1/select"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void selectCourseReturns400WhenServiceRejects() throws Exception {
        stubResolve("1011", "CARD001");

        Map<String, Object> fail = new HashMap<>();
        fail.put("success", false);
        fail.put("message", "课程容量已满");
        when(studentCourseMpService.selectCourse("CARD001", 1L)).thenReturn(fail);

        mockMvc.perform(post("/students/1011/courses/1/select"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("课程容量已满"));
    }

    @Test
    void dropCourseReturnsServiceResult() throws Exception {
        stubResolve("1011", "CARD001");

        Map<String, Object> ok = new HashMap<>();
        ok.put("success", true);
        when(studentCourseMpService.dropCourse("CARD001", 1L)).thenReturn(ok);

        mockMvc.perform(delete("/students/1011/courses/1/drop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void courseGradesReturnsRoster() throws Exception {
        when(studentCourseMpService.getCourseGradeDetails(1L)).thenReturn(Arrays.asList(gradeRow()));

        mockMvc.perform(get("/courses/1/grades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.total").value(1));
    }

    private Map<String, Object> gradeRow() {
        Map<String, Object> row = new HashMap<>();
        row.put("courseId", 1L);
        row.put("courseName", "计算机基础");
        row.put("semester", "1");
        row.put("academicYear", "2023-2024");
        return row;
    }
}
