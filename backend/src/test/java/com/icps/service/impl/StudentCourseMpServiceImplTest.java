package com.icps.service.impl;

import com.icps.entity.CourseMp;
import com.icps.entity.StudentCourseMp;
import com.icps.mapper.CourseMpMapper;
import com.icps.mapper.StudentCourseMpMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 选课 / 退课与课程已选人数同步的单元测试（纯 Mockito）
 */
@ExtendWith(MockitoExtension.class)
class StudentCourseMpServiceImplTest {

    @Mock
    private StudentCourseMpMapper studentCourseMpMapper;

    @Mock
    private CourseMpMapper courseMpMapper;

    @InjectMocks
    private StudentCourseMpServiceImpl service;

    @Test
    void selectCourseSucceedsAndIncrementsEnrolled() {
        CourseMp course = course(1L, 60, 3, 1);
        when(courseMpMapper.selectById(1L)).thenReturn(course);
        when(studentCourseMpMapper.existsByStuAndCourse("CARD001", 1L)).thenReturn(false);
        when(studentCourseMpMapper.insert(any(StudentCourseMp.class))).thenReturn(1);

        Map<String, Object> result = service.selectCourse("CARD001", 1L);

        assertTrue((Boolean) result.get("success"));
        assertEquals(4, course.getEnrolled().intValue());
        verify(courseMpMapper).updateById(course);
        verify(studentCourseMpMapper).insert(any(StudentCourseMp.class));
    }

    @Test
    void selectCourseCopiesTermFromCourse() {
        CourseMp course = course(1L, 60, 0, 1);
        course.setAcademicYear("2023-2024");
        course.setSemester("1");
        when(courseMpMapper.selectById(1L)).thenReturn(course);
        when(studentCourseMpMapper.existsByStuAndCourse("CARD001", 1L)).thenReturn(false);
        when(studentCourseMpMapper.insert(any(StudentCourseMp.class))).thenReturn(1);

        service.selectCourse("CARD001", 1L);

        org.mockito.ArgumentCaptor<StudentCourseMp> captor =
                org.mockito.ArgumentCaptor.forClass(StudentCourseMp.class);
        verify(studentCourseMpMapper).insert(captor.capture());
        assertEquals("2023-2024", captor.getValue().getAcademicYear());
        assertEquals("1", captor.getValue().getSemester());
        assertEquals("CARD001", captor.getValue().getStuCardNo());
    }

    @Test
    void selectCourseRejectsMissingCourse() {
        when(courseMpMapper.selectById(404L)).thenReturn(null);

        Map<String, Object> result = service.selectCourse("CARD001", 404L);

        assertFalse((Boolean) result.get("success"));
        assertEquals("课程不存在", result.get("message"));
        verify(studentCourseMpMapper, never()).insert(any());
    }

    @Test
    void selectCourseRejectsStoppedCourse() {
        CourseMp course = course(3L, 30, 0, 0);
        when(courseMpMapper.selectById(3L)).thenReturn(course);

        Map<String, Object> result = service.selectCourse("CARD001", 3L);

        assertFalse((Boolean) result.get("success"));
        assertEquals("课程已停开", result.get("message"));
    }

    @Test
    void selectCourseRejectsDuplicateSelection() {
        CourseMp course = course(1L, 60, 3, 1);
        when(courseMpMapper.selectById(1L)).thenReturn(course);
        when(studentCourseMpMapper.existsByStuAndCourse("CARD001", 1L)).thenReturn(true);

        Map<String, Object> result = service.selectCourse("CARD001", 1L);

        assertFalse((Boolean) result.get("success"));
        assertEquals("该学生已选择此课程", result.get("message"));
        verify(studentCourseMpMapper, never()).insert(any());
    }

    @Test
    void selectCourseRejectsWhenFull() {
        CourseMp course = course(1L, 10, 10, 1);
        when(courseMpMapper.selectById(1L)).thenReturn(course);
        when(studentCourseMpMapper.existsByStuAndCourse("CARD001", 1L)).thenReturn(false);

        Map<String, Object> result = service.selectCourse("CARD001", 1L);

        assertFalse((Boolean) result.get("success"));
        assertEquals("课程容量已满", result.get("message"));
    }

    @Test
    void selectCourseRejectsBlankArguments() {
        assertFalse((Boolean) service.selectCourse(null, 1L).get("success"));
        assertFalse((Boolean) service.selectCourse("CARD001", null).get("success"));
    }

    @Test
    void dropCourseSucceedsAndDecrementsEnrolled() {
        CourseMp course = course(1L, 60, 3, 1);
        StudentCourseMp record = new StudentCourseMp();
        record.setId(11L);
        record.setStuCardNo("CARD001");
        record.setCourseId(1L);

        when(studentCourseMpMapper.selectByStuAndCourse("CARD001", 1L)).thenReturn(record);
        when(courseMpMapper.selectById(1L)).thenReturn(course);
        when(studentCourseMpMapper.deleteById(11L)).thenReturn(1);

        Map<String, Object> result = service.dropCourse("CARD001", 1L);

        assertTrue((Boolean) result.get("success"));
        assertEquals(2, course.getEnrolled().intValue());
        verify(courseMpMapper).updateById(course);
    }

    @Test
    void dropCourseNeverGoesBelowZero() {
        CourseMp course = course(2L, 50, 0, 1);
        StudentCourseMp record = new StudentCourseMp();
        record.setId(12L);

        when(studentCourseMpMapper.selectByStuAndCourse("CARD001", 2L)).thenReturn(record);
        when(courseMpMapper.selectById(2L)).thenReturn(course);
        when(studentCourseMpMapper.deleteById(12L)).thenReturn(1);

        service.dropCourse("CARD001", 2L);

        assertEquals(0, course.getEnrolled().intValue());
    }

    @Test
    void dropCourseRejectsWhenNoSelection() {
        when(studentCourseMpMapper.selectByStuAndCourse("CARD001", 9L)).thenReturn(null);

        Map<String, Object> result = service.dropCourse("CARD001", 9L);

        assertFalse((Boolean) result.get("success"));
        assertEquals("未选择该课程", result.get("message"));
    }

    @Test
    void dropCourseRejectsWhenGradeAlreadyRecorded() {
        StudentCourseMp record = new StudentCourseMp();
        record.setId(13L);
        record.setGrade(88.0);
        when(studentCourseMpMapper.selectByStuAndCourse("CARD001", 1L)).thenReturn(record);

        Map<String, Object> result = service.dropCourse("CARD001", 1L);

        assertFalse((Boolean) result.get("success"));
        assertEquals("该课程已录入成绩，无法退课", result.get("message"));
        verify(studentCourseMpMapper, never()).deleteById(any(Long.class));
    }

    @Test
    void findByStuAndCourseDelegatesToMapper() {
        StudentCourseMp record = new StudentCourseMp();
        record.setId(5L);
        when(studentCourseMpMapper.selectByStuAndCourse("CARD001", 1L)).thenReturn(record);

        assertEquals(record, service.findByStuAndCourse("CARD001", 1L));
        assertNull(service.findByStuAndCourse(null, 1L));
        assertNull(service.findByStuAndCourse("CARD001", null));
    }

    @Test
    void gradeDetailsConvertSnakeCaseKeysToCamelCase() {
        Map<String, Object> row = new HashMap<>();
        row.put("course_code", "CS101");
        row.put("course_name", "计算机基础");
        row.put("grade_point", 3.7);
        row.put("academic_year", "2023-2024");
        when(studentCourseMpMapper.selectGradeDetailsByStuCardNo("CARD001"))
                .thenReturn(new ArrayList<>(Arrays.asList(row)));

        List<Map<String, Object>> details = service.getGradeDetails("CARD001");

        assertEquals(1, details.size());
        assertEquals("CS101", details.get(0).get("courseCode"));
        assertEquals("计算机基础", details.get(0).get("courseName"));
        assertEquals(3.7, details.get(0).get("gradePoint"));
        assertEquals("2023-2024", details.get(0).get("academicYear"));
    }

    @Test
    void gradeOverviewReturnsEmptyMapWhenMapperReturnsNull() {
        when(studentCourseMpMapper.selectGradeOverview()).thenReturn(null);
        assertTrue(service.getGradeOverview().isEmpty());
    }

    private CourseMp course(Long id, Integer capacity, Integer enrolled, Integer status) {
        CourseMp course = new CourseMp();
        course.setCourseId(id);
        course.setCapacity(capacity);
        course.setEnrolled(enrolled);
        course.setStatus(status);
        return course;
    }
}
