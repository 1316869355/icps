package com.icps;

import com.icps.mapper.CourseMpMapper;
import com.icps.mapper.StudentCourseMpMapper;
import com.icps.mapper.StudentMpMapper;
import com.icps.entity.StudentMp;
import com.icps.mapper.UserMpMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * H2 建表脚本与原生 SQL 的兼容性探针。
 *
 * <p>重点验证在 H2(MySQL 模式) 下仍然成立、但语法存在 MySQL 特性的查询写法：
 * {@code SELECT COUNT(*) > 0}、{@code CONCAT('%',?,'%')}、{@code GROUP BY} 统计等。</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class H2SchemaCompatibilityTest {

    @Autowired
    private StudentMpMapper studentMpMapper;

    @Autowired
    private StudentCourseMpMapper studentCourseMpMapper;

    @Autowired
    private CourseMpMapper courseMpMapper;

    @Autowired
    private UserMpMapper userMpMapper;

    @Test
    void contextLoads() {
        assertNotNull(studentMpMapper);
    }

    @Test
    void booleanCountQueryIsSupported() {
        assertTrue(studentCourseMpMapper.existsByStuAndCourse("CARD001", 1L));
        assertFalse(studentCourseMpMapper.existsByStuAndCourse("CARD001", 3L));
        // 逻辑删除的记录不应命中
        assertFalse(studentCourseMpMapper.existsByStuAndCourse("CARD002", 2L));
    }

    @Test
    void concatLikeQueryIsSupported() {
        // 关键词取自库内数据，避免测试对种子文件编码产生依赖
        StudentMp zhangsan = studentMpMapper.selectBySno("2023001001");
        String keyword = zhangsan.getSname().substring(0, 1);

        List<StudentMp> rows = studentMpMapper.selectBySnameLike(keyword);
        assertEquals(1, rows.size());
        assertEquals("2023001001", rows.get(0).getSno());
    }

    @Test
    void groupByCountQueriesAreSupported() {
        // 未删除学生分属 2 个学院（第三个学生已逻辑删除，不应计入）
        List<Map<String, Object>> deptStats = studentMpMapper.countByDepartment();
        assertEquals(2, deptStats.size());

        List<Map<String, Object>> semesterStats = courseMpMapper.countBySemester();
        assertEquals(2, semesterStats.size());

        List<Map<String, Object>> roleStats = userMpMapper.countByRole();
        assertEquals(3, roleStats.size());
    }

    @Test
    void countQueriesRespectLogicalDelete() {
        // 学生 3 条，其中 1 条已逻辑删除
        assertEquals(2L, studentMpMapper.countStudents());
        // 选课 4 条，其中 1 条已逻辑删除
        assertEquals(3L, studentCourseMpMapper.selectCount(null));
        // 用户 5 条，全部有效
        assertEquals(5L, userMpMapper.countUsers());
    }

    @Test
    void joinGradeDetailQueryIsSupported() {
        List<Map<String, Object>> details = studentCourseMpMapper.selectGradeDetailsByStuCardNo("CARD001");
        assertEquals(2, details.size());
        Map<String, Object> first = details.get(0);
        assertEquals("CS101", first.get("course_code"));
        // 与 MP 直接读取的实体比对，避免断言依赖种子文件编码
        assertEquals(courseMpMapper.selectById(1L).getCourseName(), first.get("course_name"));

        List<Map<String, Object>> courseDetails = studentCourseMpMapper.selectGradeDetailsByCourseId(1L);
        assertEquals(2, courseDetails.size());
    }

    @Test
    void gradeOverviewAggregateIsSupported() {
        Map<String, Object> overview = studentCourseMpMapper.selectGradeOverview();
        assertEquals(3L, ((Number) overview.get("record_count")).longValue());
        assertEquals(2L, ((Number) overview.get("graded_count")).longValue());
    }
}
