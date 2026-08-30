package com.icps;

import com.icps.entity.StudentCourseMp;
import com.icps.entity.StudentMp;
import com.icps.entity.UserMp;
import com.icps.mapper.CourseMpMapper;
import com.icps.mapper.StudentCourseMpMapper;
import com.icps.mapper.StudentMpMapper;
import com.icps.mapper.UserMpMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 逻辑删除集成测试：真实执行 SQL，验证所有自定义 @Select 的 deleted = 0 条件均生效。
 *
 * <p>测试在事务中运行并回滚，不会污染 H2 种子数据。</p>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LogicDeleteIntegrationTest {

    @Autowired
    private StudentMpMapper studentMpMapper;

    @Autowired
    private UserMpMapper userMpMapper;

    @Autowired
    private CourseMpMapper courseMpMapper;

    @Autowired
    private StudentCourseMpMapper studentCourseMpMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void logicallyDeletedStudentIsExcludedFromAllQueries() {
        // 种子数据：CARD004 / 2023001004 为 deleted = 1
        assertNull(studentMpMapper.selectBySno("2023001004"));
        assertNull(studentMpMapper.selectByUserId(1014L));
        assertEquals(2L, studentMpMapper.countStudents());
        assertTrue(studentMpMapper.selectBySnameLike("%").stream()
                .noneMatch(s -> "2023001004".equals(s.getSno())));
    }

    @Test
    void logicallyDeletedStudentRowStillExistsInDatabase() {
        Integer deleted = jdbcTemplate.queryForObject(
                "SELECT deleted FROM icps_stu WHERE sno = '2023001004'", Integer.class);
        assertEquals(1, deleted);
    }

    @Test
    void countByDepartmentExcludesDeletedRows() {
        List<Map<String, Object>> stats = studentMpMapper.countByDepartment();
        long total = stats.stream().mapToLong(row -> ((Number) row.get("count")).longValue()).sum();
        assertEquals(2L, total);
    }

    @Test
    void userQueriesExcludeDeletedRows() {
        assertNotNull(userMpMapper.selectByUsername("2023001001"));
        assertEquals(5L, userMpMapper.countUsers());

        // 逻辑删除一个用户后，所有查询都应立即过滤掉它
        UserMp user = userMpMapper.selectByUsername("2023001002");
        assertNotNull(user);
        userMpMapper.deleteById(user.getUserId());

        assertNull(userMpMapper.selectByUsername("2023001002"));
        assertEquals(4L, userMpMapper.countUsers());

        List<Map<String, Object>> roleStats = userMpMapper.countByRole();
        long studentCount = roleStats.stream()
                .filter(row -> "student".equals(row.get("role")))
                .mapToLong(row -> ((Number) row.get("count")).longValue())
                .sum();
        // 种子含 3 个学生（1011/1012/1013），删除 2023001002 后还剩 2 个
        assertEquals(2L, studentCount);
    }

    @Test
    void courseQueriesExcludeDeletedRows() {
        assertEquals(3L, courseMpMapper.countCourses());

        courseMpMapper.deleteById(1L);

        assertEquals(2L, courseMpMapper.countCourses());
        assertNull(courseMpMapper.selectByCourseCode("CS101"));
        assertFalse(courseMpMapper.selectBySemester("1").stream()
                .anyMatch(c -> "CS101".equals(c.getCourseCode())));
    }

    @Test
    void selectionQueriesExcludeDeletedRows() {
        // 种子：id=4 (CARD002 + CS201) 为 deleted = 1
        assertEquals(3L, studentCourseMpMapper.selectCount(null));

        List<StudentCourseMp> cardTwo = studentCourseMpMapper.selectByStuCardNo("CARD002");
        assertEquals(1, cardTwo.size());
        assertEquals(1L, cardTwo.get(0).getCourseId());

        List<StudentCourseMp> cs201 = studentCourseMpMapper.selectByCourseId(2L);
        assertEquals(1, cs201.size());
        assertEquals("CARD001", cs201.get(0).getStuCardNo());

        assertFalse(studentCourseMpMapper.existsByStuAndCourse("CARD002", 2L));
        assertTrue(studentCourseMpMapper.existsByStuAndCourse("CARD001", 2L));
    }

    @Test
    void joinedGradeDetailsExcludeDeletedRows() {
        List<Map<String, Object>> cardTwo = studentCourseMpMapper.selectGradeDetailsByStuCardNo("CARD002");
        assertEquals(1, cardTwo.size());
        assertEquals("CS101", cardTwo.get(0).get("course_code"));

        List<Map<String, Object>> cs201 = studentCourseMpMapper.selectGradeDetailsByCourseId(2L);
        assertEquals(1, cs201.size());
        assertEquals("CARD001", cs201.get(0).get("stu_card_no"));
    }

    @Test
    void studentSelectionStatisticsExcludeDeletedRows() {
        List<Map<String, Object>> perCourse = studentCourseMpMapper.countStudentsByCourse();
        Map<Long, Long> counts = new java.util.HashMap<>();
        for (Map<String, Object> row : perCourse) {
            counts.put(((Number) row.get("course_id")).longValue(), ((Number) row.get("count")).longValue());
        }
        assertEquals(2L, counts.get(1L));
        assertEquals(1L, counts.get(2L));
        assertNull(counts.get(3L));
    }

    @Test
    void gradeOverviewAggregateExcludesDeletedRows() {
        Map<String, Object> overview = studentCourseMpMapper.selectGradeOverview();
        assertEquals(3L, ((Number) overview.get("record_count")).longValue());
        // id=4 是已删除且有成绩(70 分)的记录，不应计入已录入成绩人次
        assertEquals(2L, ((Number) overview.get("graded_count")).longValue());
    }

    @Test
    void pageQueryExcludesDeletedRows() {
        com.baomidou.mybatisplus.core.metadata.IPage<StudentMp> page = studentMpMapper.selectPageByCondition(
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10),
                null, null, null, null);
        assertEquals(2, page.getRecords().size());
    }
}
