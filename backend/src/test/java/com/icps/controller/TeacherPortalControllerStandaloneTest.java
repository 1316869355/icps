package com.icps.controller;

import com.icps.entity.StudentCourseMp;
import com.icps.entity.StudentMp;
import com.icps.service.CourseMpService;
import com.icps.service.StudentCourseMpService;
import com.icps.service.StudentMpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 教师端接口控制层测试（standalone MockMvc，不启 Spring 上下文与数据库）
 */
@ExtendWith(MockitoExtension.class)
class TeacherPortalControllerStandaloneTest {

    @Mock
    private StudentMpService studentMpService;

    @Mock
    private StudentCourseMpService studentCourseMpService;

    @Mock
    private CourseMpService courseMpService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        TeacherPortalController controller = new TeacherPortalController();
        ReflectionTestUtils.setField(controller, "studentMpService", studentMpService);
        ReflectionTestUtils.setField(controller, "studentCourseMpService", studentCourseMpService);
        ReflectionTestUtils.setField(controller, "courseMpService", courseMpService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void listStudentsReturnsPagedResult() throws Exception {
        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("list", Arrays.asList(studentRow("2023001001", "张三")));
        pageResult.put("total", 1);
        pageResult.put("totalPages", 1);
        when(studentMpService.getStudentsByPage(1, 10)).thenReturn(pageResult);

        mockMvc.perform(get("/teacher/students").param("page", "1").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].studentId").value("2023001001"));
    }

    @Test
    void listStudentsUsesSearchServiceWhenFiltered() throws Exception {
        when(studentMpService.searchStudents("张三", null, null, null))
                .thenReturn(Arrays.asList(studentRow("2023001001", "张三")));

        mockMvc.perform(get("/teacher/students").param("name", "张三"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].name").value("张三"));

        verify(studentMpService, never()).getStudentsByPage(anyInt(), anyInt());
    }

    @Test
    void searchWithoutKeywordReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/teacher/students/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.total").value(0));
        verify(studentMpService, never()).searchStudentsByKeyword(anyString());
    }

    @Test
    void searchWithKeywordDelegatesToService() throws Exception {
        when(studentMpService.searchStudentsByKeyword("张三"))
                .thenReturn(Arrays.asList(studentRow("2023001001", "张三")));

        mockMvc.perform(get("/teacher/students/search").param("keyword", "张三"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].studentId").value("2023001001"));
    }

    @Test
    void studentDetailReturnsProfileAndCourses() throws Exception {
        StudentMp student = new StudentMp();
        student.setUserId(1011L);
        student.setStuCardNo("CARD001");
        student.setSno("2023001001");
        when(studentMpService.resolveStudent("1011")).thenReturn(student);
        when(studentMpService.convertStudentToMap(student)).thenReturn(studentRow("2023001001", "张三"));

        Map<String, Object> course = new HashMap<>();
        course.put("courseId", 1L);
        course.put("courseName", "计算机基础");
        when(studentCourseMpService.getGradeDetails("CARD001")).thenReturn(Arrays.asList(course));

        mockMvc.perform(get("/teacher/students/1011"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.studentId").value("2023001001"))
                .andExpect(jsonPath("$.data.courses[0].courseName").value("计算机基础"));
    }

    @Test
    void studentDetailReturns404ForUnknownStudent() throws Exception {
        when(studentMpService.resolveStudent("ghost")).thenReturn(null);

        mockMvc.perform(get("/teacher/students/ghost"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void inputGradeReturns404WhenStudentMissing() throws Exception {
        when(studentMpService.resolveStudent("ghost")).thenReturn(null);

        mockMvc.perform(put("/teacher/students/ghost/courses/1/grade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"grade\":90}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("学生不存在"));
    }

    @Test
    void inputGradeReturns404WhenCourseNotSelected() throws Exception {
        StudentMp student = new StudentMp();
        student.setStuCardNo("CARD001");
        when(studentMpService.resolveStudent("1011")).thenReturn(student);
        when(studentCourseMpService.findByStuAndCourse("CARD001", 9L)).thenReturn(null);

        mockMvc.perform(put("/teacher/students/1011/courses/9/grade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"grade\":90}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("该学生未选择此课程，无法录入成绩"));
    }

    @Test
    void inputGradeReturns400WithoutAnyScore() throws Exception {
        StudentMp student = new StudentMp();
        student.setStuCardNo("CARD001");
        StudentCourseMp record = new StudentCourseMp();
        record.setId(11L);
        when(studentMpService.resolveStudent("1011")).thenReturn(student);
        when(studentCourseMpService.findByStuAndCourse("CARD001", 1L)).thenReturn(record);

        mockMvc.perform(put("/teacher/students/1011/courses/1/grade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
        verify(studentCourseMpService, never()).updateGrade(anyLong(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void inputGradeComputesGradePointWhenAbsent() throws Exception {
        StudentMp student = new StudentMp();
        student.setStuCardNo("CARD001");
        StudentCourseMp record = new StudentCourseMp();
        record.setId(11L);
        when(studentMpService.resolveStudent("1011")).thenReturn(student);
        when(studentCourseMpService.findByStuAndCourse("CARD001", 1L)).thenReturn(record);

        Map<String, Object> updateResult = new HashMap<>();
        updateResult.put("success", true);
        updateResult.put("message", "成绩更新成功");
        when(studentCourseMpService.updateGrade(eq(11L), eq(95.0), eq(4.0))).thenReturn(updateResult);

        mockMvc.perform(put("/teacher/students/1011/courses/1/grade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"grade\":95}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").value(11));
    }

    @Test
    void courseStudentsReturnsRoster() throws Exception {
        Map<String, Object> row = new HashMap<>();
        row.put("sno", "2023001001");
        row.put("sname", "张三");
        when(studentCourseMpService.getCourseGradeDetails(1L)).thenReturn(Arrays.asList(row));

        mockMvc.perform(get("/teacher/courses/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.total").value(1))
                .andExpect(jsonPath("$.data[0].sno").value("2023001001"));
    }

    @Test
    void statisticsAggregatesFourSections() throws Exception {
        Map<String, Object> student = new HashMap<>();
        student.put("totalStudents", 2L);
        Map<String, Object> course = new HashMap<>();
        course.put("totalCourses", 3L);
        Map<String, Object> selection = new HashMap<>();
        selection.put("totalRecords", 3L);
        Map<String, Object> grade = new HashMap<>();
        grade.put("record_count", 3L);

        when(studentMpService.getStudentStatistics()).thenReturn(student);
        when(courseMpService.getCourseStatistics()).thenReturn(course);
        when(studentCourseMpService.getStudentCourseStatistics()).thenReturn(selection);
        when(studentCourseMpService.getGradeOverview()).thenReturn(grade);

        mockMvc.perform(get("/teacher/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.student.totalStudents").value(2))
                .andExpect(jsonPath("$.data.course.totalCourses").value(3))
                .andExpect(jsonPath("$.data.selection.totalRecords").value(3))
                .andExpect(jsonPath("$.data.grade.record_count").value(3));
    }

    private Map<String, Object> studentRow(String sno, String name) {
        Map<String, Object> row = new HashMap<>();
        row.put("studentId", sno);
        row.put("name", name);
        row.put("userId", 1011L);
        return row;
    }
}
