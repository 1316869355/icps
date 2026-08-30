package com.icps;

import com.icps.entity.CourseMp;
import com.icps.mapper.CourseMpMapper;
import com.icps.mapper.StudentCourseMpMapper;
import com.icps.service.CourseMpService;
import com.icps.service.StudentCourseMpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 选课 / 退课与课程已选人数同步的集成测试（H2 真实 SQL，事务内回滚）
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CourseSelectionIntegrationTest {

    @Autowired
    private StudentCourseMpService studentCourseMpService;

    @Autowired
    private StudentCourseMpMapper studentCourseMpMapper;

    @Autowired
    private CourseMpService courseMpService;

    @Autowired
    private CourseMpMapper courseMpMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void syncEnrolledFixesStaleCounts() {
        // 种子数据故意把 enrolled 写成过期值：CS101=1(实际 2)、CS201=0(实际 1)
        assertEquals(1, courseMpMapper.selectById(1L).getEnrolled());
        assertEquals(0, courseMpMapper.selectById(2L).getEnrolled());

        Map<String, Object> result = courseMpService.syncEnrolled();

        assertTrue((Boolean) result.get("success"));
        assertEquals(2, result.get("updated"));
        assertEquals(2, courseMpMapper.selectById(1L).getEnrolled());
        assertEquals(1, courseMpMapper.selectById(2L).getEnrolled());
        assertEquals(0, courseMpMapper.selectById(3L).getEnrolled());
    }

    @Test
    void selectCourseIncrementsEnrolled() {
        Long courseId = newCourse("CS999", 30, 0, 1);

        Map<String, Object> result = studentCourseMpService.selectCourse("CARD001", courseId);

        assertTrue((Boolean) result.get("success"));
        assertEquals(1, courseMpMapper.selectById(courseId).getEnrolled());
        assertTrue(studentCourseMpMapper.existsByStuAndCourse("CARD001", courseId));
    }

    @Test
    void selectCourseIsIdempotentGuarded() {
        Long courseId = newCourse("CS998", 30, 0, 1);
        studentCourseMpService.selectCourse("CARD001", courseId);

        Map<String, Object> second = studentCourseMpService.selectCourse("CARD001", courseId);

        assertFalse((Boolean) second.get("success"));
        assertEquals("该学生已选择此课程", second.get("message"));
        assertEquals(1, courseMpMapper.selectById(courseId).getEnrolled());
    }

    @Test
    void selectCourseRespectsCapacity() {
        Long courseId = newCourse("CS997", 1, 1, 1);

        Map<String, Object> result = studentCourseMpService.selectCourse("CARD001", courseId);

        assertFalse((Boolean) result.get("success"));
        assertEquals("课程容量已满", result.get("message"));
    }

    @Test
    void selectCourseRejectsStoppedCourse() {
        // CS301 status = 0
        Map<String, Object> result = studentCourseMpService.selectCourse("CARD001", 3L);

        assertFalse((Boolean) result.get("success"));
        assertEquals("课程已停开", result.get("message"));
    }

    @Test
    void dropCourseIsRejectedWhenGradeExists() {
        // id=3：CARD002 + CS101，已有成绩 92
        Map<String, Object> result = studentCourseMpService.dropCourse("CARD002", 1L);

        assertFalse((Boolean) result.get("success"));
        assertEquals("该课程已录入成绩，无法退课", result.get("message"));
        assertTrue(studentCourseMpMapper.existsByStuAndCourse("CARD002", 1L));
    }

    @Test
    void dropCourseLogicallyDeletesRecordAndDecrementsEnrolled() {
        // 先把 CS201 的 enrolled 修正为真实值 1，再退选 CARD001 的那条
        courseMpService.syncEnrolled();
        assertEquals(1, courseMpMapper.selectById(2L).getEnrolled());

        Map<String, Object> result = studentCourseMpService.dropCourse("CARD001", 2L);

        assertTrue((Boolean) result.get("success"));
        assertEquals(0, courseMpMapper.selectById(2L).getEnrolled());
        assertFalse(studentCourseMpMapper.existsByStuAndCourse("CARD001", 2L));

        // 逻辑删除：物理行仍在，只是 deleted = 1
        Integer deleted = jdbcTemplate.queryForObject(
                "SELECT deleted FROM icps_student_course WHERE id = 2", Integer.class);
        assertEquals(1, deleted);
    }

    @Test
    void gradeDetailsCarryCourseInformation() {
        assertEquals(2, studentCourseMpService.getGradeDetails("CARD001").size());
        assertEquals(1, studentCourseMpService.getGradeDetails("CARD002").size());
        assertEquals(2, studentCourseMpService.getCourseGradeDetails(1L).size());
    }

    private Long newCourse(String code, Integer capacity, Integer enrolled, Integer status) {
        CourseMp course = new CourseMp();
        course.setCourseCode(code);
        course.setCourseName("集成测试课程-" + code);
        course.setCredit(2);
        course.setHours(32);
        course.setCapacity(capacity);
        course.setEnrolled(enrolled);
        course.setStatus(status);
        course.setSemester("1");
        course.setAcademicYear("2023-2024");
        courseMpMapper.insert(course);
        assertNotNull(course.getCourseId());
        return course.getCourseId();
    }
}
