package com.icps.service.impl;

import com.icps.entity.CourseMp;
import com.icps.mapper.CourseMpMapper;
import com.icps.mapper.StudentCourseMpMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 课程服务单元测试：重点覆盖按选课记录重算已选人数
 */
@ExtendWith(MockitoExtension.class)
class CourseMpServiceImplTest {

    @Mock
    private CourseMpMapper courseMpMapper;

    @Mock
    private StudentCourseMpMapper studentCourseMpMapper;

    @InjectMocks
    private CourseMpServiceImpl courseService;

    @Test
    void syncEnrolledRecalculatesFromSelectionRecords() {
        CourseMp first = course(1L, 60, 1);
        CourseMp second = course(2L, 50, 0);
        when(courseMpMapper.selectList(null)).thenReturn(Arrays.asList(first, second));

        when(studentCourseMpMapper.countStudentsByCourse()).thenReturn(Arrays.asList(
                countRow(1L, 2L),
                countRow(2L, 1L)
        ));

        Map<String, Object> result = courseService.syncEnrolled();

        assertTrue((Boolean) result.get("success"));
        assertEquals(2, result.get("updated"));
        assertEquals(2, first.getEnrolled().intValue());
        assertEquals(1, second.getEnrolled().intValue());
        verify(courseMpMapper).updateById(first);
        verify(courseMpMapper).updateById(second);
    }

    @Test
    void syncEnrolledResetsCoursesWithoutSelectionsToZero() {
        CourseMp course = course(1L, 60, 5);
        when(courseMpMapper.selectList(null)).thenReturn(Arrays.asList(course));
        when(studentCourseMpMapper.countStudentsByCourse()).thenReturn(new java.util.ArrayList<>());

        Map<String, Object> result = courseService.syncEnrolled();

        assertEquals(1, result.get("updated"));
        assertEquals(0, course.getEnrolled().intValue());
        verify(courseMpMapper).updateById(course);
    }

    @Test
    void syncEnrolledLeavesAlreadyCorrectCoursesUntouched() {
        CourseMp course = course(1L, 60, 2);
        when(courseMpMapper.selectList(null)).thenReturn(Arrays.asList(course));
        when(studentCourseMpMapper.countStudentsByCourse()).thenReturn(Arrays.asList(countRow(1L, 2L)));

        Map<String, Object> result = courseService.syncEnrolled();

        assertEquals(0, result.get("updated"));
        verify(courseMpMapper, never()).updateById(course);
    }

    private CourseMp course(Long id, Integer capacity, Integer enrolled) {
        CourseMp course = new CourseMp();
        course.setCourseId(id);
        course.setCapacity(capacity);
        course.setEnrolled(enrolled);
        return course;
    }

    private Map<String, Object> countRow(Long courseId, Long count) {
        Map<String, Object> row = new HashMap<>();
        row.put("course_id", courseId);
        row.put("count", count);
        return row;
    }
}
